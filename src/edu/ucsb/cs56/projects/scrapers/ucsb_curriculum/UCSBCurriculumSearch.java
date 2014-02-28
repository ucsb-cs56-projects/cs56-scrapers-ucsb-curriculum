package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import java.net.*;
import java.io.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;

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

   @author Phill Conrad
   @author James Neally
   @author Mark Nguyen
   @author Daniel Vicory
   @author Kevin Mai
   @version W14, extended from Matis ticket 396, W12, CS56
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
	    // URL endpoint = new URL("http://foo.cs.ucsb.edu:21000");
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
    */
    public UCSBCurriculumSearch() throws java.io.IOException {

	this.lectures = new ArrayList<UCSBLecture>(); // initially empty.

	String mainPage = getMainPage();

	this.viewStateString =
	     extractHiddenFieldValue("__VIEWSTATE",mainPage);
	this.eventValString =
	     extractHiddenFieldValue("__EVENTVALIDATION",mainPage);

	//System.out.println("__VIEWSTATE=" + viewStateString);
	//System.out.println("__EVENTVALIDATION=" + eventValString);

	// System.out.println("End of Constructor");
   }

    /** When searching for courses, the HTTP POST method must be used---this method
	helps to encode the HTML Form parameters properly (using URLEncoding)

	@param name name of the HTML input parameters
	@param value value of the HTML input parameter
	@return string that should be appended to the characters to be sent in the payload
	of the HTML request. The & characters are not included.
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

    */
    public int loadCourses(String dept, String qtr, String level) throws Exception {

	// @@@ TODO
	//   For each discussion section, add it to the ArrayList<UCSBSection>
	// inside the appropriate UCSBLecture object.

	// Then add some methods that can be used to look up courses by
	// course number and/or enrollment code.


        // Get the page to parse. This is HTML
        String page = getPage(dept,qtr,level);

	// To return the total number at the end
        int num_lectures = 0;

	// Lectures or sections start with this
        String search_string = "<tr class=\"CourseInfoRow\">";

	// This string exists in HTML of lectures with at least one section
        String has_section_string = "title=\"Click for Restrictions\" "
	    + "Class=\"EnrollCodeLink\" target=\"_self\"></a>";

        int course_pos = page.indexOf(search_string,0);

	// Where to stop the substring
        int next_course_pos = page.indexOf(search_string,course_pos
					   + search_string.length());


        // Separate each lecture into separate smaller HTML Strings and
	// put them into an ArrayList. Includes the section HTML as well
        ArrayList<String> lecture_html = new ArrayList<String>();


        // Since the first found of a certain name is always a lecture,
	// it has sections only if that lecture does not list an Enroll Code.
	// Subsequent lectures with sections also do not have an enroll code


        // Cut off the end so the last one doesn't have extra. There are two
	// </table> tags, we want the second to last, so we do this twice.
        page = page.substring(0, page.lastIndexOf("</table>"));
	page = page.substring(0, page.lastIndexOf("</table>"));


        while(course_pos != -1){

            String lect = "";
            if (next_course_pos == -1){
                // Since we continued the loop, another course exists
		// but it's the last one.
                lect += page.substring(course_pos);
            }
	    else{
                lect += page.substring(course_pos, next_course_pos);
             }
            lecture_html.add(lect);
            course_pos = next_course_pos;
            next_course_pos = page.indexOf(search_string, course_pos + search_string.length());
        }

        // Now we go through the separate HTML sections and determine
	// whether it is a lecture or a section
	int lecture_index = -1;
        for(String html : lecture_html){
            String course_abbr = findPrimaryCourseAbbr(html);

            // If the course abbr is blank, then this is a section.
            if(course_abbr.equals("")){
		// Parses the HTML of a section.
                UCSBSection tmp = new UCSBSection();
		tmp = parseSectionHtml(html, lectures.get(lecture_index));
		lectures.get(lecture_index).addSection(tmp);
	    }
	    else{
		// Parses the HTML of a lecture.
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
        // This is always right after a lecture title
        String after_title_string = "<div class=\"MasterCourseTableDiv\">";

	//return html.substring(0, html.substring(0, html.indexOf(after_title_string)).lastIndexOf('>') + 1);
	//^^Some how this works
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
	title = html.substring(html.indexOf(search)+search.length(), 
			       html.indexOf("<a id=\"pageContent_repeaterSearchResults"));
	title = title.substring(0, title.indexOf("</span>"));
	
	return title;
    }

    /** Find the course description given a subsection of HTML only including on section or lecture
	@param html HTML of one lecture or section
	@return String course description
    */
    private String findDescription(String html){
        String search = "labelDescription\">";
        String description = "";
        description += html.substring(html.indexOf(search) + search.length());
        description = description.substring(0, description.indexOf('<'));
        return description.trim();
    }

    /**
     *
     */
    private String findStatus(String html){
        String search = "class=\"Status\">";
        String status = "";
        status += html.substring(html.indexOf(search) + search.length());
        status = status.substring(0, status.indexOf('<'));
        return status.trim();
    }

    /**
     *
     */
    private int findEnrollCode(String html){
	String search = "target=\"_self\">";
	String status = "";
	status += html.substring(html.indexOf(search) + search.length());
	status = status.substring(0, status.indexOf("<"));
	status = status.trim();
	int enrollCode = Integer.parseInt(status);
	return enrollCode;
	
    }

    /** This method is different because the end of the tables, with info about
	instructor, enrolled, etc. has no unique defining characteristics.
	We need to simply back up through each and know what they mean.
	@param html HTML to parse. Only looks at the end
	@param lect Lecture to set with the parsed elements
     */
    private UCSBLecture parseEnd(String html, UCSBLecture lect){
	UCSBLecture temp = lect;
        // Throw away the last part because it doesn't mean anything.
        html = removeLastElement(html);

        // The enrollment and capacity
        String enrollment_html = getEndElement(html);

        // First number is enrollment, second is capacity
        int enrollment = Integer.parseInt(enrollment_html.substring(0, enrollment_html.indexOf("/")).trim());
        int capacity = Integer.parseInt(enrollment_html.substring(enrollment_html.indexOf("/") + 1).trim());

        // Take out the enrollment/capacity because it has been parsed
        html = removeLastElement(html);

        // The location of the lecture or section room
        String lect_room_html = getEndElement(html);
        String lectRoom = lect_room_html.trim();
        html = removeLastElement(html);

        // Lecture Time
        String lect_time_html = getEndElement(html);
        String lectTime = lect_time_html.trim();
        html = removeLastElement(html);

        // Lect Days
        String lect_days_html = getEndElement(html);
        String lectDays = lect_days_html.trim();
        html = removeLastElement(html);

        // Instructor
        String instructor_html = getEndElement(html);
        int br = instructor_html.indexOf("<br />");
        if(br != -1) // Instructors have a break in them for some reason. TBA's don't though. What is this I don't even
            instructor_html = instructor_html.substring(0, br);
        String instructor = instructor_html.trim();
        html = removeLastElement(html);

        // Set all the fields
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
        // Throw away the last part because it doesn't mean anything.
        html = removeLastElement(html);

        // The enrollment and capacity
        String enrollment_html = getEndElement(html);

        // First number is enrollment, second is capacity
        int enrollment = Integer.parseInt(enrollment_html.substring(0, enrollment_html.indexOf("/")).trim());
        int capacity = Integer.parseInt(enrollment_html.substring(enrollment_html.indexOf("/") + 1).trim());

        // Take out the enrollment/capacity because it has been parsed
        html = removeLastElement(html);

        // The location of the lecture or section room
        String sect_room_html = getEndElement(html);
        String sectRoom = sect_room_html.trim();
        html = removeLastElement(html);

        // Secture Time
        String sect_time_html = getEndElement(html);
        String sectTime = sect_time_html.trim();
        html = removeLastElement(html);

        // Sect Days
        String sect_days_html = getEndElement(html);
        String sectDays = sect_days_html.trim();
        html = removeLastElement(html);

        // Instructor
        String instructor_html = getEndElement(html);
        int br = instructor_html.indexOf("<br />");
        if(br != -1) // Instructors have a break in them for some reason. TBA's don't though. What is this I don't even
            instructor_html = instructor_html.substring(0, br);
        String instructor = instructor_html.trim();
        html = removeLastElement(html);

        // Set all the fields
	temp.setEnrolled(enrollment);
        temp.setCapacity(capacity);
        temp.setSectionRoom(sectRoom);
        temp.setSectionTime(sectTime);
        temp.setSectionDay(sectDays);
	//        temp.setInstructor(instructor);
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

    private String removeLastElement(String html){
        int index = html.lastIndexOf("<td");
        return html.substring(0, index);
    }

    

    /**  Parses the HTML of a Lecture and returns a new UCSBLecture object
	 @param html HTML of a lecture.
	 @return UCSBLecture object with added members
     */
    public UCSBLecture parseLectureHtml(String html){
        // Create a default Lecture object
        UCSBLecture lect = new UCSBLecture();

        // Get all the information you need
        String courseTitle = findCourseTitle(html);
        String primaryCourseAbbr = findPrimaryCourseAbbr(html);
        String description = findDescription(html); // @TODO: This is unused as of now. Not in ticket but written by accident.
        String status = findStatus(html);

        // Set them in the obj
        lect.setCourseTitle(courseTitle);
        lect.setPrimaryCourseAbbr(primaryCourseAbbr);
        lect.setStatus(status);

        // Set the other properties
        lect = parseEnd(html, lect);

	//Returns UCSBLecture object
	return lect;

    }

    /** Parses the HTML of a Section and returns a new UCSBSection object
	@param html HTML of section
	@param parent UCSBLecture object that correlates to the section
	@return UCSBSection object with added members
     */
    public UCSBSection parseSectionHtml(String html, UCSBLecture parent){
	//Create a default Section object
	UCSBSection sect = new UCSBSection();

	String status = findStatus(html);
	int enrollCode = findEnrollCode(html);

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

	    //	    encodedData += "&ctl00%24pageContent%24searchButton.x=34&ctl00%24pageContent%24searchButton.y=6";

	    String type = "application/x-www-form-urlencoded";

	    URL endpoint = new URL(MAINPAGE_URL);
	    // URL endpoint = new URL("http://foo.cs.ucsb.edu:21000");
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

    /** return a UCSBSection object given an enrollcode
     */
    public UCSBSection getSection(int enrollCode) {
	return null; // STUB!
    }

    /** return a UCSBLecture object given a course number and quarter
	@param courseNum 13 character course num ddddddddnnnxx where
	       dddddddd is the department, extended with spaces if
	       needed, nnn is the course number, right justified,
	       and xx is the extension if any.  Examples:
	       "CMPSC     5JA", "CMPSC   130A ","MATH      3C "
	@param quarter quarter in yyyyQ format, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	@return a UCSBLecture object for that courseNum.  If there are multiple
	       instances, only the first one is returned. (Use getLectures() to
	       get an ArrayList, and countLectures to determine how many there are.)
     */

    public UCSBLecture getLecture(String courseNum, String quarter) {
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

    //Prints lectures and sections
    public void printLectures(){
        for(UCSBLecture lect : lectures){
            System.out.println(lect);
	    for(UCSBSection sect : lect.getSections()){
		System.out.println(sect);
	    }
        }
    }

    /** main method to demonstrate that the page is being accessed
     */
    public static void main(String [] args) {
	try {
	    System.setProperty("javax.net.ssl.trustStore","jssecacerts");

	    UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
	    final String dept = "CMPSC"; // the department
	    final String qtr = "20142";  // 20142 = S14 [YYYYQ, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
	    final String level = "Undergraduate"; // other options: "Graduate", "All".

	    // Pulls from the CMPSC page of Spring '14 and calls
	    // the toString() of the UCSBLectures

	    uccs.loadCourses(dept, qtr, level);
            uccs.printLectures();

	} catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	}
    }  // main

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
	
	public String getTerm() {
		// Append the quarter's integer value to the end of the year.
		return String.valueOf(year) + quarter;
	}
	
	/** @return the parsed department name */
	public String getDepartment() {
		return department;
	}
    }
}
