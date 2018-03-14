package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for UCSBCurriculumSearch
 * 
 * @author Phill Conrad
 * @author Daniel Vicory
 * @version cs56.projects.W11, Issue 50
 * @see UCSBCurriculumSearch
 */

public class UCSBCurriculumSearchTest {
	

	@Test
	public void test_loadCourses_CMPSC_F16() {
		try {
			System.setProperty("javax.net.ssl.trustStore", "jssecacerts");

UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
			
			BufferedReader reader = new BufferedReader(new FileReader("sampleData/CMPSC_F16_html.txt"));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			// delete the last new line separator
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String html = stringBuilder.toString();
			
			int num_courses = uccs.loadCoursesJsoup(html);

			// Should have found 23 lectures
			assertEquals(23, num_courses);
			
			UCSBLecture firstLecture = uccs.getLectures().get(0);
			assertEquals("Introduction to Computer Science", firstLecture.getFullTitle());
			assertEquals(0, firstLecture.getEnrolled());
			assertEquals(125, firstLecture.getCapacity());
			assertEquals("T B A", firstLecture.getInstructor());
			assertEquals("T B A", firstLecture.getRoom());
			assertEquals("Cancelled", firstLecture.getStatus());		
			assertEquals(4, uccs.getLectures().get(0).getSections().size());
			
			UCSBSection lastSection = firstLecture.getSections().get(3);
			assertEquals("PHELP3525", lastSection.getRoom());
			assertEquals("11:00am - 11:50am", lastSection.getTime());
			assertEquals("Cancelled", lastSection.getStatus());
			assertEquals("08292", lastSection.getEnrollCode());
			assertEquals(31, lastSection.getCapacity());
			

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
