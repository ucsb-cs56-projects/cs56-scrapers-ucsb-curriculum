package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import java.net.*;
import java.io.*;
import org.junit.Test;
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

        int num_lectures = 0;

        String search_string = "<tr class=\"CourseInfoRow\">";

        int course_pos = page.indexOf(search_string,0);
	
        int next_course_pos = page.indexOf(search_string,course_pos
					   + search_string.length());

        ArrayList<String> lecture_html = new ArrayList<String>();


	
        page = page.substring(0, page.lastIndexOf("</table>"));
	//page = page.substring(0, page.lastIndexOf("</table>"));


        while(course_pos != -1){

            String lect = "";
            if (next_course_pos == -1){
                lect += page.substring(course_pos);
            }
	    else{
                lect += page.substring(course_pos, next_course_pos);
             }
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

        int enrollment = Integer.parseInt(enrollment_html.substring(0, enrollment_html.indexOf("/")).trim());
        int capacity = Integer.parseInt(enrollment_html.substring(enrollment_html.indexOf("/") + 1).trim());

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
	return null; // STUB!
    }
    
    /** return an ArrayList of  UCSBLecture objects given a course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	dddddddd is the department, extended with spaces if
	needed, nnn is the course number, right justified,
	and xx is the extension if any.  Examples:
	"CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return an ArrayList of  UCSBLecture objects for that courseNum.
	If there are none, an empty ArrayList is returned.
    */
    
    public ArrayList<UCSBLecture> getLectures(String courseNum, String quarter) {
	return null; // STUB!
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
	return -42; // STUB!
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
	return null; // STUB!
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
    
    /** main method to demonstrate that the page is being accessed
	@param args String arguments in the order of: Department (CMPSC), quarter (Spring), year (2014), and level (Undergraduate)
    */
    public static void main(String [] args) {
	try {
	    System.setProperty("javax.net.ssl.trustStore","jssecacerts");
	    
	    // Asks for user input and outputs corresponding lectures/sections
	    while(true){
		UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
		System.out.println("Enter the dept, qtr, year, and crs lvl: ");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String s = bufferedReader.readLine();
		// Closes program if user inputs empty string
		if(s.equals("")){
		    System.out.println("You have closed the Program.");
		    break;
		}
		String[] inputList = s.split(", ");
		// Checks if user inputs 4 items. If not, goes to next iteration
		if(inputList.length != 4){
		    System.out.println("Error in input format! Try again!\n" +
				       "Ex. CMPSC, Spring, 2014, Undergraduate");
		    bufferedReader.close();
		    continue;
		}
		String dept = inputList[0]; // The Department
		String qtr = inputList[1]; // The Quarter
		qtr = qtrParse(qtr);
		String year = inputList[2]; //The Year
		qtr = year + qtr; // [YYYYQ, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
		String level = inputList[3]; //The course level: Undergraduate, Graduate, or All
		
		// Pulls from the html using user input and calls
		// the toString() of the UCSBLectures
		uccs.loadCourses(dept, qtr, level);
		uccs.printLectures();
		bufferedReader.close();
	    }
	} catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	}
    }
    
    /** Parses the quarter to the correct corresponding number that represents it.
	@param qtr string of the quarter e.g Summer, Winter, Fall, Spring
	@return String quarter number (Winter - 1, Spring - 2, Summer - 3, Fall - 4)
    */
    public static String qtrParse(String qtr){
	String tmp = qtr;
	switch(tmp.toUpperCase()){
	case "SUMMER":
	    tmp = "3";
	    break;
	case "FALL":
	    tmp = "4";
	    break;
	case "WINTER":
	    tmp = "1";
	    break;
	case "SPRING":
	    tmp = "2";
	    break;
	}
	return tmp;
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
