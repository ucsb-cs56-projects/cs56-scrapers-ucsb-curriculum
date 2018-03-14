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
	
}