package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** test class for UCSBSection

@author Phill Conrad
@version cs56.projects.W11, Issue 50
@see UCSBSection

*/

public class UCSBSectionTest {
	//Inititalize some variables to be used 
	UCSBLecture parent = 
	    new UCSBLecture("CMPSC 56", "Computer Science 56", "Full", "Conrad P",
			    "M W", "9:00am - 9:50am", "HFH 1132", 50, 50);

	String status = "Full";
	String enrollCode = "07989"; 
	String sectionDay = "T R";
	String sectionTime = "3:30pm = 4:45pm";
	String sectionRoom = "Chem 1171";
	int enrolled = 63;
	int capacity = 88;

    @Test
	public void test1() {
		UCSBSection test1 = new UCSBSection(parent, status, enrollCode, sectionDay,
		 sectionTime, sectionRoom, enrolled, capacity);

		//Test Constructor
		assertEquals(parent, test1.getParent());
		assertEquals(status, test1.getStatus());
		assertEquals(enrollCode, test1.getEnrollCode());
		assertEquals(sectionDay, test1.getSectionDay());
		assertEquals(sectionTime,test1.getSectionTime());
		assertEquals(sectionRoom, test1.getSectionRoom());
		assertEquals(enrolled, test1.getEnrolled());
		assertEquals(capacity, test1.getCapacity());

		String expected = test1.toString(););
    }
    
}