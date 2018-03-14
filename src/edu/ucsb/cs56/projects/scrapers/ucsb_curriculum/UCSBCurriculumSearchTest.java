package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	public void test_loadCourses_CMPSC_F16() throws IOException {
			String html = getSampleHtml();
			UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
			int num_courses = uccs.loadCoursesJsoup(html);
			// Should have found 23 lectures
			assertEquals(23, num_courses);
			UCSBLecture cmpsc8 = uccs.getLectures().get(0);
			assertEquals("Introduction to Computer Science", cmpsc8.getFullTitle());
			assertEquals(0, cmpsc8.getEnrolled());
			assertEquals(125, cmpsc8.getCapacity());
			assertEquals("T B A", cmpsc8.getInstructor());
			assertEquals("T B A", cmpsc8.getRoom());
			assertEquals("Cancelled", cmpsc8.getStatus());		
			assertEquals(4, uccs.getLectures().get(0).getSections().size());
			
			UCSBSection cmpsc8LastSection = cmpsc8.getSections().get(3);
			assertEquals("PHELP3525", cmpsc8LastSection.getRoom());
			assertEquals("11:00am - 11:50am", cmpsc8LastSection.getTime());
			assertEquals("Cancelled", cmpsc8LastSection.getStatus());
			assertEquals("08292", cmpsc8LastSection.getEnrollCode());
			assertEquals(31, cmpsc8LastSection.getCapacity());
	}
	
	@Test
	public void test_loadCourses_CMPSC_F16_CMPSC99() throws IOException {
		String sampleHtml = getSampleHtml();
		UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
		uccs.loadCoursesJsoup(sampleHtml);
		UCSBLecture cmpsc99 = uccs.getLectures().get(8);
		assertTrue(cmpsc99.getCourseTitle().equals("CMPSC 99"));
		assertTrue(cmpsc99.getFullTitle().equals("Independent Studies in Computer Science"));
		assertTrue(cmpsc99.getDescription().equals("Independent studies in computer science for advanced students."));
		assertTrue(cmpsc99.getPrerequisite().equals(""));
		assertTrue(cmpsc99.getCollege().equals("ENGR"));
		assertTrue(cmpsc99.getUnits().equals("1.0 - 4.0"));
		assertTrue(cmpsc99.getGrading().equals("Pass/No Pass"));
		assertTrue(cmpsc99.getPrimaryCourseAbbr().equals("INDEPENDENT STUDIES"));
		assertTrue(cmpsc99.getLevelLimit().equals("L"));
		assertTrue(cmpsc99.getMajorLimitPass().equals(""));
		assertTrue(cmpsc99.getMessages().equals("DEPT. APPROVAL REQUIRED PRIOR TO REGISTRATION."));
		assertTrue(cmpsc99.getMajorLimit().equals(""));
		assertTrue(cmpsc99.getInstructor().equals("T B A"));
		assertTrue(cmpsc99.getDays().equals(""));
		assertTrue(cmpsc99.getTime().equals(""));
		assertTrue(cmpsc99.getRoom().equals("T B A"));
		assertTrue(cmpsc99.getEnrolled() == 0);
		assertTrue(cmpsc99.getCapacity() == 5);
		assertTrue(cmpsc99.getStatus().equals(""));
		assertTrue(cmpsc99.getEnrollCode().equals("78113"));
	}
	
	private String getSampleHtml() {
		String html = "";
		try {
			System.setProperty("javax.net.ssl.trustStore", "jssecacerts");
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

			html = stringBuilder.toString();
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return html;
	}
}
