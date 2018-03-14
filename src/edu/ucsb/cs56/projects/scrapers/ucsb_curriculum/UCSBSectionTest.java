package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** test class for UCSBSection

@author Phill Conrad
@version cs56.projects.W11, Issue 50
@see UCSBSection

*/

public class UCSBSectionTest {

	
    @Test
	public void test1() {
    	
    	
    	// Placeholder until tests are refactored
   	 	assertTrue(false);
   	 
   	 
		//initialize section parent
		UCSBLecture parent =
		new UCSBLecture("CMPSC 56", "Computer Science 56", "Full", "Conrad P",
						"M W", "9:00am - 9:50am", "HFH 1132", "Letter", "4.0", 
						"ENGR", "None", "Java class", "Advanced App", 50, 50, "77872", 
						"Sophmore", "CMPSC", "See department", "None");
		
		//variables used for section testing
		String status = "Full";
		String enrollCode = "07989";
		String sectionDay = "T R";
		String sectionTime = "3:30pm - 4:45pm";
		String sectionRoom = "Chem 1171";
		int enrolled = 63;
		int capacity = 88;
		
		//Test Constructor
		UCSBSection test1 = new UCSBSection(parent, status, enrollCode, sectionDay,
		 sectionTime, sectionRoom, enrolled, capacity);

		//test getter methods
		assertEquals(parent, test1.getParent());
		assertEquals(status, test1.getStatus());
		assertEquals(enrollCode, test1.getEnrollCode());
		assertEquals(sectionDay, test1.getDays());
		assertEquals(sectionTime,test1.getTime());
		assertEquals(sectionRoom, test1.getRoom());
		assertEquals(enrolled, test1.getEnrolled());
		assertEquals(capacity, test1.getCapacity());
		
		//test tostring method
		String expected = "\t Course Title: CMPSC 56\n"
		+ "\t Section Status: Full\n"
		+ "\t Enroll Code: 07989\n"
		+ "\t Section Day: T R\n"
		+ "\t Section Time: 3:30pm - 4:45pm\n"
		+ "\t Section Rm: Chem 1171\n"
		+ "\t Enrolled / Capacity: 63 / 88\n";
		assertEquals(expected, test1.toString());
		

    }
	
//	@Test
//	public void test2() {
//		//initialize section parent
//		UCSBLecture parent =
//		new UCSBLecture("CMPSC 64", "Computer Science 64", "", "Dewey K",
//						"T R", "3:30pm - 4:45pm", "Phelps 3526", 5, 80, "00000");
//		
//		//variables used for section testing
//		String status = "";
//		String enrollCode = "36555";
//		String sectionDay = "T";
//		String sectionTime = "5:30pm - 6:30pm";
//		String sectionRoom = "Phelps 3526";
//		int enrolled = 5;
//		int capacity = 80;
//		
//		//Test Constructor
//		UCSBSection test2 = new UCSBSection(parent, status, enrollCode, sectionDay,
//											sectionTime, sectionRoom, enrolled, capacity);
//		
//		
//		//test getter methods
//		assertEquals(parent, test2.getParent());
//		assertEquals(status, test2.getStatus());
//		assertEquals(enrollCode, test2.getEnrollCode());
//		assertEquals(sectionDay, test2.getSectionDay());
//		assertEquals(sectionTime,test2.getSectionTime());
//		assertEquals(sectionRoom, test2.getSectionRoom());
//		assertEquals(enrolled, test2.getEnrolled());
//		assertEquals(capacity, test2.getCapacity());
//		
//		//test tostring method
//		String expected = "\t Course Title: CMPSC 64\n"
//		+ "\t Section Status: \n"
//		+ "\t Enroll Code: 36555\n"
//		+ "\t Section Day: T\n"
//		+ "\t Section Time: 5:30pm - 6:30pm\n"
//		+ "\t Section Rm: Phelps 3526\n"
//		+ "\t Enrolled / Capacity: 5 / 80\n";
//		assertEquals(expected, test2.toString());
//		
//	}
	
//	
//	@Test
//	public void test3() {
//		//initialize section parent
//		UCSBLecture parent =
//		new UCSBLecture("ECON 10A", "Microecon Theory", "", "Hartman J L",
//						"M W F", "12:00pm - 12:50pm", "Buchn 1910", 194, 200, "");
//
//		//variables used for section testing
//		String status = "Closed";
//		String enrollCode = "12815";
//		String sectionDay = "R";
//		String sectionTime = "5:00pm - 5:50pm";
//		String sectionRoom = "Girv 2119";
//		int enrolled = 0;
//		int capacity = 40;
//		
//		//Test Constructor
//		UCSBSection test3 = new UCSBSection(parent, status, enrollCode, sectionDay,
//											sectionTime, sectionRoom, enrolled, capacity);
//		
//		//test setter methods
//		test3.setParent(parent);
//		test3.setStatus(status);
//		test3.setEnrollCode(enrollCode);
//		test3.setSectionDay(sectionDay);
//		test3.setSectionTime(sectionTime);
//		test3.setSectionRoom(sectionRoom);
//		test3.setEnrolled(enrolled);
//		test3.setCapacity(capacity);
//
//		//test getter methods
//		assertEquals(parent, test3.getParent());
//		assertEquals(status, test3.getStatus());
//		assertEquals(enrollCode, test3.getEnrollCode());
//		assertEquals(sectionDay, test3.getSectionDay());
//		assertEquals(sectionTime,test3.getSectionTime());
//		assertEquals(sectionRoom, test3.getSectionRoom());
//		assertEquals(enrolled, test3.getEnrolled());
//		assertEquals(capacity, test3.getCapacity());
//		
//		//test tostring method
//		String expected = "\t Course Title: ECON 10A\n"
//		+ "\t Section Status: Closed\n"
//		+ "\t Enroll Code: 12815\n"
//		+ "\t Section Day: R\n"
//		+ "\t Section Time: 5:00pm - 5:50pm\n"
//		+ "\t Section Rm: Girv 2119\n"
//		+ "\t Enrolled / Capacity: 0 / 40\n";
//		assertEquals(expected, test3.toString());
//
//
//		
//	}
	
}