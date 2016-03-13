package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** test class for UCSBLecture

@author Phill Conrad
@author James Neally
@version W12 Issue 396 extension of cs56.projects.W11, Issue 50
@see UCSBLecture

*/

public class UCSBLectureTest {
    
// Init some variables to be used
	String courseTitle = "CMPSC     8";
	String primaryCourseAbbr = "INTRO TO COMP SCI";
	String status = "Closed";
	String instructor = "BUONI M J";
	String lectDays = "T R";
	String lectTime = "3:30pm - 4:45pm";
	String lectRoom = "CHEM 1171";
	int enrolled = 63;
	int capacity = 88;
	String enrollCode = "00000";

    /* Test the constructor written for issue 396
     TODO: Extend test when extended cstruct is written */
    @Test
	public void test_Constructor() {

	

	UCSBLecture lecture = 
	    new UCSBLecture(courseTitle, primaryCourseAbbr, status, instructor,
			    lectDays, lectTime, lectRoom, enrolled, capacity, enrollCode);

	// Test Constructor
	assertEquals(courseTitle, lecture.getCourseTitle());
	assertEquals(primaryCourseAbbr, lecture.getPrimaryCourseAbbr());
	assertEquals(status, lecture.getStatus());
	assertEquals(instructor, lecture.getInstructor());
	assertEquals(lectDays, lecture.getLectDays());
	assertEquals(lectTime, lecture.getLectTime());
	assertEquals(lectRoom, lecture.getLectRoom());
	assertEquals(enrolled, lecture.getEnrolled());
	assertEquals(capacity, lecture.getCapacity());
	assertEquals(enrollCode, lecture.getEnrollCode());

	// Test Setters/Getters
	lecture.setCourseTitle("CMPSC 56");
	lecture.setPrimaryCourseAbbr("Computer Science 56");
	lecture.setStatus("Full");
	lecture.setInstructor("CONRAD P");
	lecture.setLectDays("M W");
	lecture.setLectTime("9:00am - 9:50am");
	lecture.setLectRoom("HFH 1132");
	lecture.setEnrolled(50);
	lecture.setCapacity(50);
	lecture.setEnrollCode("00000");

	assertEquals("CMPSC 56", lecture.getCourseTitle());
	assertEquals("Computer Science 56", lecture.getPrimaryCourseAbbr());
	assertEquals("Full", lecture.getStatus());
	assertEquals("CONRAD P", lecture.getInstructor());
	assertEquals("M W", lecture.getLectDays());
	assertEquals("9:00am - 9:50am", lecture.getLectTime());
	assertEquals("HFH 1132", lecture.getLectRoom());
	assertEquals(50, lecture.getEnrolled());
	assertEquals(50, lecture.getCapacity());
	assertEquals("00000", lecture.getEnrollCode());

    }

    @Test
    public void test_toString(){
        UCSBLecture lecture =
	    new UCSBLecture(courseTitle, primaryCourseAbbr, status, instructor,
			    lectDays, lectTime, lectRoom, enrolled, capacity, enrollCode);

        String expected = "Course Title: CMPSC     8\n"
                        + "Course Abbreviation: INTRO TO COMP SCI\n"
                        + "Lecture Status: Closed\n"
                        + "Lecture Instructor: BUONI M J\n"
                        + "Lecture Days: T R\n"
                        + "Lecture Time: 3:30pm - 4:45pm\n"
                        + "Lecture Room: CHEM 1171\n"
                        + "Enrolled / Capacity: 63 / 88\n"
						+ "Enroll Code: 00000\n";

        assertEquals(expected, lecture.toString());
    }
    

}