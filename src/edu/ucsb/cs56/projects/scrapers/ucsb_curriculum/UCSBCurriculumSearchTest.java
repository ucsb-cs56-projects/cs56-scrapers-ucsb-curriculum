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
	public void test_loadCourses_ARTHI_S17() {
		try {
			System.setProperty("javax.net.ssl.trustStore", "jssecacerts");

			UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
			
			BufferedReader reader = new BufferedReader(new FileReader("sampleData/ARTHI_S17_html.txt"));
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

			// Should have found 27 lectures
			assertEquals(27, num_courses);

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	@Test
	public void test_loadCourses2() {
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

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}
}
