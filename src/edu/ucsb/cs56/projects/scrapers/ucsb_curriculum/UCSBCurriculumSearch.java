package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import java.net.*;
import java.io.*;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
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
   /* public int loadCourses(String dept, String qtr, String level) throws Exception {
    	
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
	} */
    public int loadCoursesJsoup(String dept, String qtr, String level) throws Exception {
    	String html = getPage(dept, qtr, level);
		String isLecture = "";
    	Document doc = Jsoup.parse(html);
    	Elements courseInfoRows = doc.getElementsByClass("CourseInfoRow");
    	int numberOfLectures = 0; 
    	UCSBLecture currentLecture = new UCSBLecture();
    	System.out.println(courseInfoRows.size());
    	for (Element courseInfoRow: courseInfoRows) {
    		isLecture = courseInfoRow.getElementsByClass("Section").text();
    		if (isLecture.equals("True")) {
    			System.out.println("Creating lecture");
    			currentLecture = parseLecture(courseInfoRow);
    			lectures.add(currentLecture);
    			//System.out.println(currentLecture);
    			numberOfLectures++;
    		} else if (isLecture.equals("False")) {
    			System.out.println("Creating section");
    			UCSBSection newSection = parseSection(courseInfoRow);
    			newSection.setParent(currentLecture);
    			System.out.println(currentLecture);
    			currentLecture.addSection(newSection);
    		}
    	}
    	
    	return numberOfLectures;
    }
    public UCSBLecture parseLecture(Element courseInfoRow) {
		String courseTitle, instructor, days, room, enrollment, fullTitle, description, college, units, grading, majorLimit, enrollCode, status, preRequisite, restrictions, levelLimit,
		   majorLimitPass, messages, primaryCourseAbbr, time;
		courseTitle = instructor = days = room = enrollment = enrollCode = status = preRequisite = college = restrictions = levelLimit = majorLimitPass = messages
			= primaryCourseAbbr = time = grading = units  = " ";
		Elements instructorDaysRoomElements = courseInfoRow.getElementsByAttributeValue("style", "text-align: left; vertical-align: middle;");
		List <String> instructorDaysRoomText = instructorDaysRoomElements.eachText();
		String[] instructorDaysRoom = parseInstructorDaysRoom(instructorDaysRoomText);
		instructor = instructorDaysRoom[0];
		days = instructorDaysRoom[1];
		room = instructorDaysRoom[2];
		time = courseInfoRow.getElementsByAttributeValueStarting("style", "text-align: left; vertical-align: top; white-space: nowrap; padding-left: 5px;").text();
		
		enrollment = courseInfoRow.getElementsByAttributeValue("style", "text-align: right; vertical-align: middle;").text().trim();
		int enrolled = Integer.parseInt(enrollment.substring(0, enrollment.indexOf('/')).trim());
		int capacity = Integer.parseInt(enrollment.substring(enrollment.indexOf('/') + 1).trim());
		courseTitle =courseInfoRow.getElementById("CourseTitle").ownText();
		enrollCode = courseInfoRow.getElementsByClass("EnrollCodeLink").text();
		
		Elements tableRows = courseInfoRow.select("tr");
		List<String> tableRowsText = tableRows.eachText();
		String [] courseData = parseTableRows(tableRowsText);
		primaryCourseAbbr = courseInfoRow.getElementsByAttributeValue("style", "text-decoration:underline;").text();
		status = courseInfoRow.getElementsByClass("Status").text();
		fullTitle = courseData[0];
		description = courseData[1];
		preRequisite = courseData[2];
		college = courseData[3];
		units = courseData[4];
		grading = courseData[5];
		restrictions = courseData[6];
		levelLimit = courseData[7];
		majorLimitPass = courseData[8];
		majorLimit = courseData[9];
		messages = courseData[10];
		UCSBLecture newLecture = new UCSBLecture(
				courseTitle,
				primaryCourseAbbr,
				status,
				instructor,
				days,
				time,
				room,
				grading,
				units,
				college,
				preRequisite,
				description,
				fullTitle,
				enrolled,
				capacity,
				enrollCode,
				levelLimit,
				majorLimitPass,
				messages,
				majorLimit);
		return newLecture;
    }
    
    public UCSBSection parseSection(Element courseInfoRow) {
    	String days, room, enrollment, enrollCode, status, time;
    	days = room = enrollment = enrollCode = status = time = "";
		Elements instructorDaysRoomElements = courseInfoRow.getElementsByAttributeValue("style", "text-align: left; vertical-align: middle;");
		List <String> instructorDaysRoomText = instructorDaysRoomElements.eachText();
		String[] instructorDaysRoom = parseInstructorDaysRoom(instructorDaysRoomText);
		days = instructorDaysRoom[1];
		room = instructorDaysRoom[2];
		time = courseInfoRow.getElementsByAttributeValueStarting("style", "text-align: left; vertical-align: top; white-space: nowrap; padding-left: 5px;").text();
		enrollment = courseInfoRow.getElementsByAttributeValue("style", "text-align: right; vertical-align: middle;").text().trim();
		int enrolled = Integer.parseInt(enrollment.substring(0, enrollment.indexOf('/')).trim());
		int capacity = Integer.parseInt(enrollment.substring(enrollment.indexOf('/') + 1).trim());
		enrollCode = courseInfoRow.getElementsByClass("EnrollCodeLink").text();
		status = courseInfoRow.getElementsByClass("Status").text();
		UCSBSection newSection = new UCSBSection(
				null,
				status,
				enrollCode,
				days,
				time,
				room,
				enrolled,
				capacity);
		return newSection;
    }
    
    public String[] parseTableRows(List<String> tableRows) {
	    /*
	    CMPSC 4 Click box to close. Full Title: Computer Science Boot Camp Description: An introduction to computational thinking, computing, data management, and problem solving using computers, for non-majors. Topics include coding basics, representing code and data using a computer, and applications of computing that are important to society. PreRequisite: College: ENGR Units: 4.0 Grading: Letter Textbook Information: http://www.ucsbstuff.com/SelectTermDept.aspx CS BOOT CAMP Restrictions Click box to close. Level-Limit: Major-Limit-Pass: Major-Limit: Not these majors: CMPSC CMPEN Grading: Letter Messages: KOC C K M W 3:30pm - 4:45pm TD-W 1701 102 / 120 True
		Click box to close.
		Full Title: Computer Science Boot Camp
		Description: An introduction to computational thinking, computing, data management, and problem solving using computers, for non-majors. Topics include coding basics, representing code and data using a computer, and applications of computing that are important to society.
		PreRequisite:
		College: ENGR
		Units: 4.0
		Grading: Letter
		Textbook Information: http://www.ucsbstuff.com/SelectTermDept.aspx
		Restrictions Click box to close.
		Level-Limit:
		Major-Limit-Pass:
		Major-Limit: Not these majors: CMPSC CMPEN
		Grading: Letter
		Messages:	
	     */
	    //Above is an example of table rows for a given lecture/section.
	    String fullTitle, description, preRequisite, college, units, grading, restrictions, levelLimit, majorLimitPass, majorLimit, messages;
	    fullTitle = description = preRequisite = college = units = grading = restrictions = levelLimit = majorLimitPass = majorLimit = messages = "";
	    fullTitle = tableRows.get(2).substring(tableRows.get(2).indexOf("Full Title:") + 11).trim();
	    description = tableRows.get(3).substring(tableRows.get(3).indexOf("Description:") + 12).trim();
	    preRequisite = tableRows.get(4).substring(tableRows.get(4).indexOf("PreRequisite:") + 13).trim();
	    college = tableRows.get(5).substring(tableRows.get(5).indexOf("College:") + 8).trim();
	    units = tableRows.get(6).substring(tableRows.get(6).indexOf("Units:") + 6).trim();
	    grading = tableRows.get(7).substring(tableRows.get(7).indexOf("Grading:") + 8).trim();
	    restrictions = tableRows.get(9).substring(tableRows.get(9).indexOf("Restrictions:") + 13, tableRows.get(9).indexOf("Click box to close.")).trim();
	    levelLimit = tableRows.get(10).substring(tableRows.get(10).indexOf("Level-Limit:") + 12).trim();
	    majorLimitPass = tableRows.get(11).substring(tableRows.get(11).indexOf("Major-Limit-Pass:") + 17).trim();
	    majorLimit = tableRows.get(12).substring(tableRows.get(12).indexOf("Major-Limit:") + 12).trim();
	    messages = tableRows.get(14).substring(tableRows.get(14).indexOf("Messages:") + 9).trim();
	    String[] tableRowData = {fullTitle, description, preRequisite, college, units, grading, restrictions, levelLimit, majorLimitPass, majorLimit, messages};
    return tableRowData;
    }
    
    /** Returns a list with the instructor, days, and room in that order associated with a lecture/section.*/
    public String[] parseInstructorDaysRoom(List<String> instructorDaysRoomText) {
    	String instructor, days, room;
    	instructor = days = room = "";
    	if (instructorDaysRoomText.size() == 2) {
    		instructor = instructorDaysRoomText.get(0);
    		room = instructorDaysRoomText.get(1);
    	} else if (instructorDaysRoomText.size() == 3) {
    		instructor = instructorDaysRoomText.get(0);
    		days = instructorDaysRoomText.get(1);
    		room = instructorDaysRoomText.get(2);
    	}
    	String[] instructorDaysRoom = {instructor, days, room};
    	return instructorDaysRoom;
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
		String after_list_string = "<option value=\"20164\">FALL 2016  </option>";
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
