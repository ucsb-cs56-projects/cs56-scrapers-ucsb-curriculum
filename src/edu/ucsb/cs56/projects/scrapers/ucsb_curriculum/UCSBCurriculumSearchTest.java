package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** Test class for UCSBCurriculumSearch
 * @author Phill Conrad
 * @author Daniel Vicory
 * @version cs56.projects.W11, Issue 50
 * @see UCSBCurriculumSearch
*/

public class UCSBCurriculumSearchTest {



    @Test
    public void test_loadCourses() {
		try {
			System.setProperty("javax.net.ssl.trustStore", "jssecacerts");
			
			UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
			final String dept = "CMPSC"; // the department
			final String qtr = "20112";  // 2012 = S11 [yyyyQ, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
			final String level = "Undergraduate"; // other options: "Graduate", "All".
			
			//String page = uccs.getPage(dept,qtr,level);
			
			int num_courses = uccs.loadCourses(dept, qtr, level);
			
			// Should have found 23 lectures
			assertEquals(23, num_courses);
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_loadCourses2() {
		try{
			System.setProperty("javax.net.ssl.trustStore", "jssecacerts");
			
			UCSBCurriculumSearch uccs2 = new UCSBCurriculumSearch();
			final String dept = "JAPAN"; //the department
			final String qtr = "20112"; //2016 Winter quarter
			final String level = "Undergraduate";
			
			//String page = uccs2.getPage(dept,qtr,level);
			
			int num_courses = uccs2.loadCourses(dept, qtr, level);
			
			assertEquals(21, num_courses);
			
		}catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}
	
}