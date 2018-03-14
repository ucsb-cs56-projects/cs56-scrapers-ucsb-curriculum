package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import static org.junit.Assert.*;

import org.junit.Test;

/** test class for UCSBLecture

@author Phill Conrad
@author James Neally
@version W12 Issue 396 extension of cs56.projects.W11, Issue 50
@see UCSBLecture

*/

public class UCSBLectureTest {
  
//// Init some variables to be used
	String courseTitle = "CMPSC     8";
	String primaryCourseAbbr = "INTRO TO COMP SCI";
	String status = "Closed";
	String instructor = "BUONI M J";
	String days = "T R";
	String time = "3:30pm - 4:45pm";
	String room = "CHEM 1171";
	int enrolled = 63;
	int capacity = 88;
	String enrollCode = "00000";
    String grading = "Letter";
    String units= "4.0";
    String college = "ENGR";
    String preRequisite = "None";
    String description = "See dept";
    String fullTitle = "Introduction to CS";
    String levelLimit = "None";
    String majorLimitPass = "CSMPC";
    String messages = "None";
    String majorLimit = "None";
	
     @Test
	public void test_Constructor() {
		UCSBLecture lecture = 
		    new UCSBLecture(
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
	
		// Test Constructor
     	assertEquals(courseTitle, lecture.getCourseTitle());
		assertEquals(primaryCourseAbbr, lecture.getPrimaryCourseAbbr());
		assertEquals(status, lecture.getStatus());
		assertEquals(instructor, lecture.getInstructor());
		assertEquals(days, lecture.getDays());
		assertEquals(time, lecture.getTime());
		assertEquals(room, lecture.getRoom());
		assertEquals(enrolled, lecture.getEnrolled());
		assertEquals(capacity, lecture.getCapacity());
		assertEquals(enrollCode, lecture.getEnrollCode());
	
		// Test Setters/Getters
		lecture.setCourseTitle("CMPSC 56");
		lecture.setPrimaryCourseAbbr("Computer Science 56");
		lecture.setStatus("Full");
		lecture.setInstructor("CONRAD P");
		lecture.setDays("M W");
		lecture.setTime("9:00am - 9:50am");
		lecture.setRoom("HFH 1132");
		lecture.setEnrolled(50);
		lecture.setCapacity(50);
		lecture.setEnrollCode("00000");
	
		assertEquals("CMPSC 56", lecture.getCourseTitle());
		assertEquals("Computer Science 56", lecture.getPrimaryCourseAbbr());
		assertEquals("Full", lecture.getStatus());
		assertEquals("CONRAD P", lecture.getInstructor());
		assertEquals("M W", lecture.getDays());
		assertEquals("9:00am - 9:50am", lecture.getTime());
		assertEquals("HFH 1132", lecture.getRoom());
		assertEquals(50, lecture.getEnrolled());
		assertEquals(50, lecture.getCapacity());
		assertEquals("00000", lecture.getEnrollCode());

    }

    @Test
    public void test_toString(){
    	UCSBLecture lecture = 
    		    new UCSBLecture(
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
        
        String expected = "Course Title: " + courseTitle + "\n"
       + "Course Abbreviation: " + primaryCourseAbbr + "\n"
        
        + "\n ---- Course Details for " + courseTitle + "----\n"
        + "Course Full Title: " + "Introduction to CS" + "\n"
        + "Course Description: " + "See dept" + "\n"
        + "Course Prerequisites: " + "None" + "\n"
        + "College: " + "ENGR" + "\n"
        + "Messages: " + "None" + "\n"
        
        + "\n ---- Enrollment Details for " + courseTitle + "----\n"
        + "Lecture Status: " + status + "\n"
        + "Enrolled / Capacity: " + enrolled + " / " + capacity + "\n"
        + "Units: " + "4.0" + "\n"
        + "Grading: " + "Letter" + "\n"
        + "Level Limit: " + "None" + "\n"
        + "Major Limit: " + "None" + "\n"
        + "Major Limit Pass: " + "CSMPC" + "\n"
        + "Enroll Code: " + enrollCode + "\n"
        
        + "\n ---- Lecture Details for " + courseTitle + "----\n"
        + "Lecture Instructor: " + instructor + "\n"
        + "Lecture Days: " + days + "\n"
        + "Lecture Time: " + time + "\n"
        + "Lecture Room: " + room + "\n";

        assertEquals(expected, lecture.toString());
    }
    

}
