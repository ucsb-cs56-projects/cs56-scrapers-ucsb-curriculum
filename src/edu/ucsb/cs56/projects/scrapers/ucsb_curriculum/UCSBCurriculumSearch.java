p
import java.net.*;
import java.io.*;
import org.junit.Test;

import javafx.util.Pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

/**
   This object is designed to parse input from the Curriculum Search
   page at:
       http://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx
   Getting information from this site is especially challenging
   because of the ASPX viewstate issue; the viewstate must be
   preserved from transaction to transaction.
   @author Richard Young
   @author Jim Vargas
   @author Phill Conrad
   @author James Neally
   @author Mark Nguyen
   @author Daniel Vicory
   @author Kevin Mai
   @version F16, extended from Matis ticket 396, W12, CS56
*/

public class UCSBCurriculumSearch {
    public static final boolean debug = false;

    /** default URL used in the main */

    public static final String MAINPAGE_URL =
	"https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx";

    public static final String MAINPAGE_EXPECTED_TITLE = "Curriculum Search";
    public static final String MAINPAGE_EXPECTED_FORM = "aspnetForm";

    private String viewStateString;
    private String eventValString;

    private ArrayList<UCSBLecture> lectures;

    /** getMainPage() returns the contents of the main page at MAINPAGE_URL
       as a String.   This is primarily used internally to initialize the
       viewstate (needed for screenscraping ASPX websites).
       @return HTML code for the page
    */
    public static String getMainPage() {
	StringBuffer wholeResponse = null;
	try {
	    String agent = "Mozilla/4.0";
	    String encodedData = "";
	    URL endpoint = new URL(MAINPAGE_URL);

	    HttpsURLConnection urlc = null;

	    urlc = (HttpsURLConnection) endpoint.openConnection();
	    urlc.setRequestMethod("GET");
            urlc.setRequestProperty("User-Agent", agent);
            urlc.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    urlc.setDoInput(true);
	    urlc.setUseCaches(false);
	    urlc.setAllowUserInteraction(false);
	    urlc.setRequestProperty("Referer",MAINPAGE_URL);

	    int rc = urlc.getResponseCode();

	    BufferedReader in =
		new BufferedReader(new InputStreamReader(urlc.getInputStream()));

	    String inputLine;
	    wholeResponse = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
		wholeResponse.append(inputLine + "\n");
	    }
	    in.close();
	}
	catch( IOException e ){
	    System.out.println(e);
	    e.printStackTrace();
	}
	if (wholeResponse==null)
	    return null;
	else
	    return wholeResponse.toString();

    }

    /** extractHiddenFielddValue is used to extract the __VIEWSTATE and the
	__EVENTVALIDATION values from the HTML for an ASPX web page.
	@param name name of the hidden field (e.g. "__VIEWSTATE" or "__EVENTVALIDATION")
	@param page HTML for the page (e.g. result of getMainPage())
	@return value of the value attribute of that hidden field
    */
    public static String extractHiddenFieldValue(String name, String page) {
	final String beforeValue =
	    "<input type=\"hidden\" name=\"" + name +
	    "\" id=\"" + name + "\" value=\"";

	final String afterValue="\" />";

	if (debug) {
	    System.out.println("extractHiddenFieldValue: page=" + page);
	    System.out.println("extractHiddenFieldValue: name=" + name);
	}

	int firstPos = page.indexOf(beforeValue) + beforeValue.length();
	if (debug) { System.out.println("extractHiddenFieldValue: firstPos="+firstPos); }
	
	int afterPos = page.indexOf(afterValue,firstPos);
	if (debug) { System.out.println("afterPos="+afterPos); }
	
	return page.substring(firstPos,afterPos);
    }
    
    
    /** Constructor---called to initialize the UCSBCurriculumSearch object
	
	The default constructor initially makes an empty list of
	lectures, and initializes the viewstate for doing
	searches---but does not actually load up any courses.
	@throws java.io.IOException thrown when an error occurs with input and output
    */
    public UCSBCurriculumSearch() throws java.io.IOException {
	
	this.lectures = new ArrayList<UCSBLecture>(); // initially empty.
	
	String mainPage = getMainPage();
		//refresh page to make sure that search results show up
		mainPage = getMainPage();
	
	this.viewStateString =
	    extractHiddenFieldValue("__VIEWSTATE",mainPage);
	this.eventValString =
	    extractHiddenFieldValue("__EVENTVALIDATION",mainPage);
    }
    
    /** When searching for courses, the HTTP POST method must be used---this method
	helps to encode the HTML Form parameters properly (using URLEncoding)
	
	@param name name of the HTML input parameters
	@param value value of the HTML input parameter
	@return string that should be appended to the characters to be sent in the payload
	of the HTML request. The &amp; characters are not included.
	@throws java.lang.Exception thrown when an error occurs
    */
    public static String encodedNameValuePair(String name, String value) throws Exception {
	return  URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }
    
    /** loadCourses is used to load up the UCSBCurriculumSearch object with a set of
	courses for a given Department, Quarter, and level.
	
	After courses are loaded into the object, other methods can be used to
	look up courses by course number and/or enrollment code
	@param dept department code.  (e.g., "CMPSC" or "ART")
	@param qtr 5 character quarter code (yyyyq where q=1,2,3,4 for Winter,Spring,Summer,Fall)
	@param level one of "Undergraduate","Graduate","All"
	@return the number of courses loaded
	@throws java.lang.Exception thrown when an error occurs
    */
    public int loadCourses(String dept, String qtr, String level) throws Exception {
    	
        String page = getPage(dept,qtr,level);
        String origpage = page;
        int num_lectures = 0;

        String search_string = "<tr class=\"CourseInfoRow\">";

        int course_pos = page.indexOf(search_string,0);
	
        int next_course_pos = page.indexOf(search_string,course_pos
					   + search_string.length());

        ArrayList<String> lecture_html = new ArrayList<String>();

		try {
		    
	        page = page.substring(0, page.lastIndexOf("</table>"));
			//next line causes problems
		}
		catch (Exception e) {
		    throw new Exception ("webpage did not have expected structure"+origpage);
		}

        while(course_pos != -1){

            String lect = "";
            if (next_course_pos == -1){
                lect += page.substring(course_pos);
            } else{
                lect += page.substring(course_pos, next_course_pos);
             }
            System.out.println(lect);
            lecture_html.add(lect);
            course_pos = next_course_pos;
            next_course_pos = page.indexOf(search_string, course_pos + search_string.length());
        }

		int lecture_index = -1;
        for(String html : lecture_html){
            String course_abbr = findPrimaryCourseAbbr(html);

			// If the course abbr is blank, then this is a section.
            if(course_abbr.equals("")){
				UCSBSection tmp = new UCSBSection();
				tmp = parseSectionHtml(html, lectures.get(lecture_index));
				lectures.get(lecture_index).addSection(tmp);
			}
			else{
				lectures.add(parseLectureHtml(html));
				lecture_index++;
				num_lectures++;
			}
		}
		
		return num_lectures;
	}
    public int loadCoursesJsoup(String dept, String qtr, String level) throws Exception {
    	String html = getPage(dept, qtr, level);
    	Document doc = Jsoup.parse(html);
    	Elements courseInfoRows = doc.getElementsByClass("CourseInfoRow");
    	int numberOfLectures = 0; 
    	for (Element courseInfoRow: courseInfoRows) {
    		String line = courseInfoRow.text();
    		if (isLecture(line) && !isLectureWithoutSection(line)) {
    		System.out.println(numberOfLectures + ": " + line);
    		parseLecture(line);
    	//	while ()
    		numberOfLectures++;
    		}
    	}
    	System.out.println(courseInfoRows.size());
    	
    	return numberOfLectures;
    }
  //Write now this is just printing the values it parses and not creating a UCSBLecture Object from it
    public void parseSection(String line) {
    	
    }
    public void parseLectureWithoutSections(String line) {
    	//stub
    }
    //Write now this is just printing the values it parses and not creating a UCSBLecture Object from it
    public void parseLecture(String line) {
    	//Below is a sample of the input
    	//	"CMPSC 4 Click box to close. Full Title: Computer Science Boot Camp Description: An introduction to computational thinking," 
    	//+ 	" computing, data management, and problem solving using computers, for non-majors. Topics include coding basics, representing"
    	//+	"code and data using a computer, and applications of computing that are important to society. PreRequisite: College: ENGR Units: 4.0 "
    	//+ "Grading: Letter Textbook Information: http://www.ucsbstuff.com/SelectTermDept.aspx CS BOOT CAMP Restrictions Click box to close. Level-Limit:"
    	//+ " Major-Limit-Pass: Major-Limit: Not these majors: CMPSC CMPEN Grading: Letter Messages: KOC C K M W 3:30pm - 4:45pm TD-W 1701 102 / 120 True";
    	int courseTitleEndIndex = line.indexOf(" Click box to close.");
    	System.out.println(courseTitleEndIndex);
    	String courseTitle = line.substring(0, courseTitleEndIndex);
    	System.out.println("Course Title: " + courseTitle);
    	int fullTitleEndIndex = line.indexOf(" Description: ");
    	String fullTitle = line.substring(line.indexOf(" Full Title: ") + 13, fullTitleEndIndex);
    	System.out.println("Full Title: " + fullTitle);
    	int descriptionEndIndex = line.indexOf(" PreRequisite: ");
    	String description = line.substring(line.indexOf(" Description: ") + 14, descriptionEndIndex);
    	System.out.println("Description: " + description);
    	int prerequisiteEndIndex = line.indexOf(" College:");
    	String prerequisite = line.substring(line.indexOf(" PreRequisite:") + 14, prerequisiteEndIndex);
    	System.out.println("Prerequisite: " + prerequisite);
    	int collegeEndIndex = line.indexOf(" Units:");
    	String college = line.substring(line.indexOf(" College: ") + 10, collegeEndIndex);
    	System.out.println("College: " + college);
    	int unitsEndIndex = line.indexOf(" Grading:");
    	String units = line.substring(line.indexOf(" Units: ") + 8, unitsEndIndex);
    	System.out.println("Units: " + units);
    	int gradingEndIndex = line.indexOf(" Textbook Information: ");
    	String grading = line.substring(line.indexOf(" Grading: ") + 10, gradingEndIndex);
    	System.out.println("Grading: " + grading);
    	//primaryCourseAbbr is going to need ssome extra work because sometimes something comes before "Restrictions Click Box..." but for now just gonna leave as is
    	int primaryCourseAbbrEndIndex = line.indexOf(" Restrictions Click box to close. ");
    	String primaryCourseAbbr = line.substring(line.indexOf("http://www.ucsbstuff.com/SelectTermDept.aspx ") + 45, primaryCourseAbbrEndIndex);
    	System.out.println("Primary Course Abbreviation: " + primaryCourseAbbr);
    	//System.out.println("Missing status rn");
    	//System.out.println("Missing level limit rn");
    	//System.out.println("Missing major limit pass rn");
    	int majorLimitEndIndex = line.indexOf(" Grading: ", line.indexOf("Grading: ") + 1); // The " Grading: " substring appears twice and we need the 2nd one
    	String majorLimit = "";
    	if (!line.substring(line.indexOf(" Major-Limit: ") + 14, line.indexOf(" Major-Limit: ") + 21).equals("Grading")) {
    		//This means major list is not empty
    		majorLimit += line.substring(line.indexOf(" Major-Limit: ") + 14, majorLimitEndIndex);
    	}
    	
    	System.out.println("Major-Limit: " + majorLimit);
    	//System.out.println("Missing Messages rn");
    	int instructorBeginIndex = line.indexOf("Messages: ");
    	String lectTime = getLectTime(line.substring(instructorBeginIndex));
    	System.out.println("Time: " + lectTime);
    	Pair <String, String> instructorAndDays = getInstructorAndDays(line.substring(line.indexOf("Messages: ") + 10, line.indexOf(lectTime)));
    	String instructor = instructorAndDays.getKey();
    	String lectDays = instructorAndDays.getValue();
    	System.out.println("Instructor: " + instructor);
    	System.out.println("Days: " + lectDays);
    	Pair <Integer, Integer> enrolledAndCapacity = getEnrolledAndCapacity(line.substring(instructorBeginIndex));
    	int enrolled = enrolledAndCapacity.getKey();
    	int capacity = enrolledAndCapacity.getValue();
    	System.out.println("Enrolled: " + enrolled);
    	System.out.println("Capacity: " + capacity);
    	String enrolledCapacityStr = enrolledAndCapacity.getKey().toString() + " / " + enrolledAndCapacity.getValue().toString();
    	int lectRoomStartIndex = line.indexOf(lectTime) + lectTime.length() + 1;
    	int lectRoomEndIndex = line.indexOf(enrolledCapacityStr);
    	String lectRoom = line.substring(lectRoomStartIndex, lectRoomEndIndex);
    	System.out.println("Lecture Room: " + lectRoom);
    	
    	UCSBLecture lecture = new UCSBLecture();
        lecture.setCourseTitle(courseTitle);
        lecture.setFullTitle(fullTitle);
        lecture.setDescription(description);
        
		
	
		//lect.setEnrollCode(enrollcode);
    }
    public boolean isLectureWithoutSection(String line) {
    	//String dummy  = 
    	//		"CMPSC 99 Click box to close. Full Title: Independent Studies in Computer Science Description: Independent studies in "
    	//		+ "computer science for advanced students. PreRequisite: College: ENGR Units: 1.0 - 4.0 Grading: Pass/No Pass Textbook Information: "
    	//		+ "http://www.ucsbstuff.com/SelectTermDept.aspx INDEPENDENT STUDIES 08466 Restrictions Click box to close. Level-Limit: L Major-Limit-Pass: "
    	//		+ "Major-Limit: Grading: Pass/No Pass Messages: DEPT. APPROVAL REQUIRED PRIOR TO REGISTRATION. T B A T B A 0 / 5 True";
    	int restrictionsIndex = line.indexOf("Restrictions");
    	String enrollCode = "";
    	for (int i = restrictionsIndex - 6; i < restrictionsIndex; i++) {
    		if (Character.isDigit(line.charAt(i))) {
    			enrollCode += line.charAt(i);
    		}
    	}
    	//System.out.println(enrollCode);
    	if (enrollCode.length() == 5) {
    		return true;
    	} else {
    		return false;
    	}
    }
    public Pair<Integer, Integer> getEnrolledAndCapacity(String line) {
    	int slashIndex = line.indexOf('/');
    	Integer enrolled, capacity; 
    	String strReverseEnrolled = "";
    	String strCapacity = "";
    	//Reading backwards from slash to get Enrolled number, it'll be in reverse since we're reading it backwards
    	for (int i = slashIndex; i >= 0; i--) {
    		if (strReverseEnrolled.length() == 0 && Character.isWhitespace(line.charAt(i))) {
    			continue;
    		} else if (Character.isDigit(line.charAt(i))) {
    			strReverseEnrolled += line.charAt(i);
    		} else if (Character.isWhitespace(line.charAt(i)) && strReverseEnrolled.length() > 0) {
    			break;
    		}
    	}
    	String strEnrolled = new StringBuilder(strReverseEnrolled).reverse().toString();
    	for (int i = slashIndex; i < line.length(); i++) {
    		if (strCapacity.length() == 0 && Character.isWhitespace(line.charAt(i))) {
    			continue;
    		} else if (Character.isDigit(line.charAt(i))) {
    			strCapacity += line.charAt(i);
    		} else if (Character.isWhitespace(line.charAt(i)) && strCapacity.length() > 0) {
    			break;
    		}
    	}
    	enrolled = Integer.parseInt(strEnrolled);
    	capacity = Integer.parseInt(strCapacity);
    	System.out.println(enrolled);
    	System.out.println(capacity);
    	return new Pair<Integer, Integer>(enrolled, capacity);
    }
    public String getLectTime(String line) {
    	//start represents the index of where the time starts;
    	int start = 0;
    	//mCount counts the amount of "m"s (from am and pm) and once two "m"s have been read, we know we've finished reading the time
    	int mCount = 0;
    	//end represents the index of where the time ends
    	int end = 0;
    	for (int i = 0; i < line.length(); i++) {
    		if (Character.isDigit(line.charAt(i))) {
    			start = i;
    			break;
    		}
    	}
    	for (int i = start; i < line.length(); i++) {
    		if (line.charAt(i) == 'm') {
    			mCount += 1;
    		}
    		if (mCount == 2) {
    			end = i + 1;
    			break;
    		}
    	}
    	return line.substring(start, end);
    }
    public Pair <String, String> getInstructorAndDays (String line) {
    	//Case where instructor is TBA
    	if (line.substring(0, 5).equals("T B A")) {
    		String instructor = "T B A";
    		String days = line.substring(5);
    		return new Pair<String, String>(instructor, days);
    	}
    	String instructor = "";
    	String unorderedDays = "";
    	String possibleDays = "MTWRF";
    	String orderedDays = "";
    	int instructorEndIndex = 0;
    	for (int i = line.length() - 1; i >= 0; i--) {
    		if (Character.isLetter(line.charAt(i))){
    			//This means the character is a part of the instructor's initial and not a day
        		if (possibleDays.indexOf(line.charAt(i)) == -1) {
        			instructorEndIndex = i + 1;
        			break;
        		}
        		//If a certain day character appears twice, that means that the 2nd character is apart of the name and not a day
    			if (unorderedDays.indexOf(line.charAt(i)) != -1) {
    				instructorEndIndex = i;
    				break;
    			} else if (possibleDays.indexOf(line.charAt(i)) != -1) {
    				unorderedDays += line.charAt(i);
    				unorderedDays += " ";
    			}
    		}
    	}
		instructor = line.substring(0, instructorEndIndex);
		if (unorderedDays.indexOf('M') != -1)
			orderedDays += "M ";
		if (unorderedDays.indexOf('T') != -1)
			orderedDays += "T ";
		if (unorderedDays.indexOf('W') != -1)
			orderedDays += "W ";
		if (unorderedDays.indexOf('R') != -1)
			orderedDays += "R ";
		if (unorderedDays.indexOf('F') != -1)
			orderedDays += "F";
		return new Pair <String, String>(instructor, orderedDays);
    }
    public boolean isLecture(String line) {
    	if (line.indexOf("True") != -1) {
    		return true;
    	} else {
		return false;
    	}
    }
    /** Find the list of offered Subject Areas given the HTML of the main page
	@param html HTML of the main url
	@return ArrayLIst of Strings of offered subject areas
    */
     public ArrayList<String> findSubjectAreas(String html){
		ArrayList<String> SubjectAreas = new ArrayList<String>();
		String before_list_string = "<select name=\"ctl00$pageContent$courseList\" id=\"ctl00_pageContent_courseList\" class=\"droplist\">";
		String after_list_string = "</select>";
		String all_subjects = "";
		String[] all_subjects_split;
		try{
		    all_subjects  = html.substring(html.indexOf(before_list_string)+ before_list_string.length(),html.indexOf(after_list_string) );
		   // 	    System.out.println(all_subjects);
	
		}catch (Exception e){
				System.err.println("The HTML of UCSB Curriculum Serach has changed.");
				System.err.println("This scraper must be updated.");
			}
		all_subjects_split = all_subjects.split("\n");
		for(int i = 1; i < all_subjects_split.length; i++){
		    // System.out.println(all_subjects_split[i]);
		     String temp = all_subjects_split[i].substring(all_subjects_split[i].lastIndexOf("=")+2, all_subjects_split[i].lastIndexOf("\""));
		     SubjectAreas.add(temp);
		     //	     System.out.println(temp);
			}
	
		return SubjectAreas;
     }
    /** Find the list of quarters that are viewable given the HTML
	@param html HTML of the main URL
        @return ArrayList of Strings of quarters
     */
    public ArrayList<String> findQuarterAndYear(String html){
		ArrayList<String> availableQuarters = new ArrayList<String>();
		String before_list_string = "<select name=\"ctl00$pageContent$quarterList\" id=\"ctl00_pageContent_quarterList\" class=\"droplist\">";
		String after_list_string = "<option value=\"20154\">FALL 2015   </option>";
		String all_quarters = "";
		String[] all_quarters_split;
		try{
		    all_quarters  = html.substring(html.indexOf(before_list_string)+ before_list_string.length(),html.indexOf(after_list_string)+after_list_string.length());
		}catch (Exception e){
		     System.err.println("The HTML of UCSB Curriculum Serach has changed.");
		     System.err.println("This scraper must be updated.");
		}
		all_quarters_split = all_quarters.split("\n");
		for(int i = 1; i < all_quarters_split.length; i++)
		    {
			int startQuarterName = all_quarters_split[i].indexOf(">") + 1;
			String temp = all_quarters_split[i].substring(startQuarterName, all_quarters_split[i].lastIndexOf("<"));
			temp = temp.trim();
			availableQuarters.add(temp);
		    }
		return availableQuarters;
	}
    
    
    /** Find the Course Title given a subsection of HTML only including one section or lecture.
	@param html HTML of only one lecture or section
	@return String Course Title e.g. "CMPSC     8"
    */
    private String findCourseTitle(String html){
		String after_title_string = "";
		try{
			after_title_string = "<div class=\"MasterCourseTableDiv\">";
		}catch (Exception e){
			System.err.println("The HTML of UCSB Curriculum Serach has changed.");
			System.err.println("This scraper must be updated.");
		}
		
		return html.substring(html.substring(0, html.indexOf(after_title_string)).lastIndexOf('>') + 1, html.indexOf(after_title_string)).trim();
	}
	
    /** Find the Full Course Title (Abbreviation) given a subsection of HTML only inclduing one section or lecture.
        If no Course Title abbreviation is found in the given section of HTML, return an empty string ("").
	(This is how we know this portion of html holds the information of a section, not a lecture.)
	 @param html HTML of only one lecture or section
	 @return String Full Course Title e.g. "APP TO UNIV WRIT"
    */
	private String findPrimaryCourseAbbr(String html){

			// If exists, it's the first text after this string:
			String search = "decoration:underline;\">";
			String title = "";

		try{
			title = html.substring(html.indexOf(search)+search.length(),
								   html.indexOf("<a id=\"ctl00_pageContent_repeaterSearchResults"));
			
			title = title.substring(0, title.indexOf("</span>"));
			
		}catch (Exception e){
			System.err.println("The HTML of UCSB Curriculum Serach has changed.");
			System.err.println("This scraper must be updated.");
		}
		
		return title;
		
    }

    /** Find the course description given a subsection of HTML only including on section or lecture
	@param html HTML of one lecture or section
	@return String course description
    */
	private String findDescription(String html){
		String description = "";
		try{
			String search = "labelDescription\">";
			description += html.substring(html.indexOf(search) + search.length());
			description = description.substring(0, description.indexOf('<'));
		}catch (Exception e){
			System.err.println("The HTML of UCSB Curriculum Serach has changed.");
			System.err.println("This scraper must be updated.");
		}
		return description.trim();
	}

    /** Find the course status given a subsection of HTML only including on section or lecture
	@param html HTML of one lecture or section
	@return String course status
     */
    private String findStatus(String html){
		String status = "";
		try{
			String search = "class=\"Status\">";
			
			status += html.substring(html.indexOf(search) + search.length());
			status = status.substring(0, status.indexOf('<'));
		}catch (Exception e){
			System.err.println("The HTML of UCSB Curriculum Serach has changed.");
			System.err.println("This scraper must be updated.");
		}
		return status.trim();
	}
	
    /** Find the course enrollment code given a subsection of HTML only including on section or lecture
	@param html HTML of one lecture or section
	@return String course enrollment code
     */
	private String findEnrollCode(String html){
		String status = "";
		try{
			String search = "target=\"_self\">";
			status += html.substring(html.indexOf(search) + search.length());
			status = status.substring(0, status.indexOf("<"));
			status = status.trim();
		} catch (Exception e){
			System.err.println("The HTML of UCSB Curriculum Serach has changed.");
			System.err.println("This scraper must be updated.");
		}
		return status;
	
    }

    /** This method is different because the end of the tables, with info about
	instructor, enrolled, etc. has no unique defining characteristics.
	We need to simply back up through each and know what they mean.
	@param html HTML to parse. Only looks at the end
	@param lect Lecture to set with the parsed elements
     */
    private UCSBLecture parseEnd(String html, UCSBLecture lect){
    	UCSBLecture temp = lect;
   
        html = removeLastElement(html);
        String enrollment_html = getEndElement(html);
        
        System.out.println(html.toString());

        int enrollment = Integer.parseInt(enrollment_html.substring(0, enrollment_html.indexOf("/")).trim());
        int capacity = Integer.parseInt(enrollment_html.substring(enrollment_html.indexOf("/") + 1).trim());
        
        //int enrollment = 23;
        //int capacity = 36;

        // Take out the enrollment/capacity because it has been parsed
        html = removeLastElement(html);

        String lect_room_html = getEndElement(html);
        String lectRoom = lect_room_html.trim();
        html = removeLastElement(html);

        String lect_time_html = getEndElement(html);
        String lectTime = lect_time_html.trim();
        html = removeLastElement(html);

        String lect_days_html = getEndElement(html);
        String lectDays = lect_days_html.trim();
        html = removeLastElement(html);

        String instructor_html = getEndElement(html);
        int br = instructor_html.indexOf("<br />");
        if(br != -1) // Instructors have a break in them for some reason. TBA's don't though.
            instructor_html = instructor_html.substring(0, br);
        String instructor = instructor_html.trim();
        html = removeLastElement(html);


	temp.setEnrolled(enrollment);
        temp.setCapacity(capacity);
        temp.setLectRoom(lectRoom);
        temp.setLectTime(lectTime);
        temp.setLectDays(lectDays);
        temp.setInstructor(instructor);
	
	return temp;

    }

    /** This method is different because the end of the tables, with info about
	instructor, enrolled, etc. has no unique defining characteristics.
	We need to simply back up through each and know what they mean.
	@param html HTML to parse. Only looks at the end
	@param lect Lecture to set with the parsed elements
     */
    private UCSBSection parseEndSection(String html, UCSBSection sect){
	UCSBSection temp = sect;
	
        html = removeLastElement(html);
        String enrollment_html = getEndElement(html);

        int enrollment = Integer.parseInt(enrollment_html.substring(0, enrollment_html.indexOf("/")).trim());
        int capacity = Integer.parseInt(enrollment_html.substring(enrollment_html.indexOf("/") + 1).trim());

        // Take out the enrollment/capacity because it has been parsed
        html = removeLastElement(html);

        String sect_room_html = getEndElement(html);
        String sectRoom = sect_room_html.trim();
        html = removeLastElement(html);

        String sect_time_html = getEndElement(html);
        String sectTime = sect_time_html.trim();
        html = removeLastElement(html);

        String sect_days_html = getEndElement(html);
        String sectDays = sect_days_html.trim();
        html = removeLastElement(html);

        String instructor_html = getEndElement(html);
        int br = instructor_html.indexOf("<br />");
        if(br != -1) // Instructors have a break in them for some reason. TBA's don't though.
	    instructor_html = instructor_html.substring(0, br);
        String instructor = instructor_html.trim();
        html = removeLastElement(html);

	temp.setEnrolled(enrollment);
        temp.setCapacity(capacity);
        temp.setSectionRoom(sectRoom);
        temp.setSectionTime(sectTime);
        temp.setSectionDay(sectDays);
  
	return temp;

    }

    /** This is specific for parseEnd(). Input the html and this will return the element
	at the end. E.g. if the html has <td ...> 63 / 88</td> at the end, it will return "63 / 88"
	@param html HTML to get the end element of
	@return String content of the last element
     */
    private String getEndElement(String html){
        // Each section starts this way
        String start_tag = "<td";
        String end_tag = "</td>";

        String element_html = html.substring(html.lastIndexOf(start_tag));
        return element_html.substring(element_html.indexOf(">") + 1, element_html.indexOf(end_tag)).trim();
    }

    /** Removes last element because it doesn't do anything
	@param html HTML to remove last element of
	@return String with last element removed
    */
    
    private String removeLastElement(String html){
        int index = html.lastIndexOf("<td");
        return html.substring(0, index);
    }

    

    /**  Parses the HTML of a Lecture and returns a new UCSBLecture object
	 @param html HTML of a lecture.
	 @return UCSBLecture object with added members
     */
    public UCSBLecture parseLectureHtml(String html){
	
        UCSBLecture lect = new UCSBLecture();

        String courseTitle = findCourseTitle(html);
        String primaryCourseAbbr = findPrimaryCourseAbbr(html);
		
       // String description = findDescription(html); // @TODO: This is unused as of now. Not in ticket but written by accident.
        String status = findStatus(html);
	    String enrollcode = findEnrollCode(html);
		
        lect.setCourseTitle(courseTitle);
        lect.setPrimaryCourseAbbr(primaryCourseAbbr);
        lect.setStatus(status);
		
	
		lect.setEnrollCode(enrollcode);
		
		
        lect = parseEnd(html, lect);

        return lect;

    }

    /** Parses the HTML of a Section and returns a new UCSBSection object
	 @param html HTML of section
	 @param parent UCSBLecture object that correlates to the section
	 @return UCSBSection object with added members
     */
    public UCSBSection parseSectionHtml(String html, UCSBLecture parent){
		UCSBSection sect = new UCSBSection();
		
		String status = findStatus(html);
		String enrollCode = findEnrollCode(html);
		
		sect.setStatus(status);

		sect.setEnrollCode(enrollCode);
		sect.setParent(parent);
		sect = parseEndSection(html, sect);

        return sect;
    }

    public String getPageJsoup (String dept, String qtr, String level) throws Exception {
    	String url = "https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx";
    	Document doc = Jsoup.connect(url).get();
    	System.out.println("Printing the doc!! \n \n \n");
    	System.out.print(doc);
    	System.out.println("FINISHED");
    	//System.out
    	return "wat";
    }
    /** getPage() returns the contents of a page of HTML containing the courses
	for a given department, quarter, and level. It is NOT a static method--it can
	only be invoked from an object, because it needs the instance variables
	for viewstate and event validation that were initialized in the constructor.
	This is primarily used internally to get the HTML that loadCourses
	parses to load courses into the object.
	@param dept department code.  (e.g., "CMPSC" or "ART")
	@param qtr 5 character quarter code (yyyyq where q=1,2,3,4 for Winter,Spring,Summer,Fall)
	@param level one of "Undergraduate","Graduate","All"
	@return HTML code for the page
	@throws java.lang.Exception thrown when an error occurs
    */
    public String getPage(String dept, String qtr, String level) throws Exception {
	StringBuffer wholeResponse = null;

	try {
	    String agent = "Mozilla/4.0";
	    String encodedData = "";
	    encodedData +=  encodedNameValuePair("__VIEWSTATE",viewStateString);
	    encodedData += ("&" + encodedNameValuePair("__EVENTVALIDATION",eventValString));
	    encodedData += ("&" + encodedNameValuePair("ctl00$pageContent$courseList",dept));
	    encodedData += ("&" + encodedNameValuePair("ctl00$pageContent$quarterList",qtr));
	    encodedData += ("&" + encodedNameValuePair("ctl00$pageContent$dropDownCourseLevels",level));
	    encodedData += ("&" + encodedNameValuePair("ctl00$pageContent$searchButton.x", "0"));
	    encodedData += ("&" + encodedNameValuePair("ctl00$pageContent$searchButton.y", "0"));

	    byte[] encodedBytes = encodedData.getBytes();


	    String type = "application/x-www-form-urlencoded";

	    URL endpoint = new URL(MAINPAGE_URL);
	    HttpURLConnection urlc = null;

	    urlc = (HttpURLConnection) endpoint.openConnection();
	    urlc.setRequestMethod("POST");
	    urlc.setRequestProperty("User-Agent", agent);
	    urlc.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    urlc.setDoOutput(true);
	    urlc.setDoInput(true);
	    urlc.setUseCaches(false);
	    urlc.setAllowUserInteraction(false);
	    urlc.setRequestProperty("Content-type", type);
	    urlc.setRequestProperty("Referer",MAINPAGE_URL);
	    urlc.setRequestProperty( "Content-Length", Integer.toString(encodedBytes.length) );

	    OutputStream os = urlc.getOutputStream();
	    os.write( encodedBytes );
	    os.flush();

	    int rc = urlc.getResponseCode();

	    BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

	    String inputLine;
	    wholeResponse = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
		wholeResponse.append(inputLine + "\n");
	    }
	    in.close();


	}
	catch( IOException e ){
	    System.out.println(e);
	    e.printStackTrace();
	}
	if (wholeResponse==null)
	    return null;
	else
	    return wholeResponse.toString();

    }

    /** return a UCSBSection object given an enrollcode, if it doesn't exist,
     *  return a null object
     @param enrollCode string of an enrollment code
     @return UCSBSection section object correlating to the right enrollment code
     */
    public UCSBSection getSection(String enrollCode) {
		UCSBSection section = null;
		for(UCSBLecture lect : lectures){
			for(UCSBSection sect : lect.getSections()){
				if(sect.getEnrollCode().equals(enrollCode))
					section = sect;
			}
		}
		return section;
	}

    /** return a UCSBLecture object given a course number and quarter
	@param Title 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return a UCSBLecture object for that courseNum.  If there are multiple
	instances, only the first one is returned. (Use getLectures() to
	get an ArrayList, and countLectures to determine how many there are.)
    */
    
    public UCSBLecture getLecture(String Title, String quarter) {
	String department;
	String CourseNum;
	UCSBLecture lecture = null;
                for(UCSBLecture lect : lectures){
                                if(lect.getCourseTitle().equals(Title))
                                        lecture = lect;
                }
                return lecture;
    }
    
    /** return an ArrayList of  UCSBLecture objects
	@return an ArrayList of UCSBLecture objects.
	If there are none, an empty ArrayList is returned.
    */
    
    public ArrayList<UCSBLecture> getLectures() {
	ArrayList<UCSBLecture> retval = new ArrayList<UCSBLecture>(lectures.size());
	for (UCSBLecture l: lectures) {
	    retval.add(new UCSBLecture(l));  
	}

	return retval;
    }
    
    /** return the number of UCSBLecture objects already loaded that match
	the given course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return how many instances there are of that lecture
	
    */
    
    public int countLectures(String courseNum, String quarter) {
	ArrayList<UCSBLecture> retval = getLectures();
	    
	    return retval.size();	
    }
    
    
    /** return a UCSBSection object given a course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return a UCSBSection object for that courseNum.  If there are multiple
	instances, only the first one is returned. (Use getSections() to
	get an ArrayList, and countSections to determine how many there are.)
    */
    
    public UCSBSection getSection(String courseNum, String quarter) {
	UCSBSection section = null;
               for(UCSBLecture lect : lectures){
                     if(lect.getCourseTitle().equals(courseNum)){
                           section = lect.getSections().get(0);
                        }
                }
                return section;
    }
    
    /** return an ArrayList of  UCSBSection objects given a course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return an ArrayList of  UCSBSection objects for that courseNum (possibly
	spanning multiple lectures sections).
	If there are none, an empty ArrayList is returned.
    */
    
    public ArrayList<UCSBSection> getSections(String courseNum, String quarter) {
	return null; // STUB!
    }
    
    /** return the number of UCSBSection objects already loaded that match
	the given course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return how many sections there are for that course (possibly across multiple lectures)
    */
    
    public int countSections(String courseNum, String quarter) {
	return -42; // STUB!
    }
    
    /** Prints lectures and subsquent section
     */
    public void printLectures(){
	for(UCSBLecture lect : lectures){
	    System.out.println(lect);
	    for(UCSBSection sect : lect.getSections()){
		System.out.println(sect);
	    }
        }
    }
    
    /**
     * Builds a query to submit to the UCSBCourseCurriculumSearch 
     * by enumerating Quarters, CourseLevels, etc.  We also handle 
     * formatting.
     * @author Mark Nguyen
     * @author Daniel Vicory
     * @see UCSBCourseCurriculumSearch
     */ 
    private static class Query {
	
	/**
	 * Enumerates possible quarters to query for and translates them 
	 * to values that are understood by the search API.
	 */
	public static enum Quarter {
	    Winter(1),
	    Spring(2),
	    Summer(3),
	    Fall(4);
	    
	    private final int value;
	    
	    Quarter(int value) {
		this.value = value;
	    }
	    
	    /**
	     * Converts to String representation of integer value
	     * of this Quarter that can be understood by the API.
	     * For example, Winter = 1, Spring = 2, Summer = 3, Fall = 4.
	     */
	    public String toString() {
		return String.valueOf(value);
	    }
	}
	
	
	/**
	 * Enumerates the possible course levels offered by the API
	 */
	public static enum CourseLevel {
	    Undergraduate("Undergraduate"),
	    Graduate("Graduate"),
	    All("All");
	    
	    private final String value;
	    CourseLevel(String value) {
		this.value = value;
	    }
	    
	    public String toString() {
		return value;
	    }
	    
	}
	
	private String department;
	private int year;
	private Quarter quarter;
	private CourseLevel courseLevel;
	
	/**
	 * Initializes this query and performs necessary vield validation
	 * @param department the name of the department.  we will trim and put to uppercase
	 * @param quarter the quarter to search for
	 * @param year the integer representation of the year (e.g., 2013 or 1996)
	 * @param courseLevel the course level to filter by, if at all
	 */
	Query(String department, Quarter quarter, int year, CourseLevel courseLevel) {
	    this.department = department.trim().toUpperCase();
	    this.year = year;
	    this.quarter = quarter;
	    this.courseLevel = courseLevel;
	}

	/** 
	    @return String with quarter integer appended
	 */
	public String getTerm() {
	    return String.valueOf(year) + quarter;
	}
	
	/** 
	    @return the parsed department name 
	*/
	public String getDepartment() {
	    return department;
	}
    }
}
