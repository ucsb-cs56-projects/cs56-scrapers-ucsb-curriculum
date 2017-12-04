package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import java.util.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** test class for UCSBDepartments

@author Jake Guida
@author Danny Millstein
@see UCSBLecture

*/
public class UCSBDepartmentTest {
    //Init the current department list. This list is updated as of November 2017. If you wish to test the department scraping you must update any differences between the current list below and list found in the html code of     //the main page at https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx
    ArrayList<String> departments = new ArrayList<>(Arrays.asList("ANTH", "ART", "ART  CS", "ARTHI", "ARTST", "AS AM", "ASTRO", "BIOL", "BIOL CS", "BMSE", "BL ST", "CH E", "CHEM CS", "CHEM", "CH ST", "CHIN", "CLASS", "COMM", "C LIT", "CMPSC", "CMPSCCS", "CMPTG", "CMPTGCS", "CNCSP", "DANCE", "DYNS", "EARTH", "EACS", "EEMB", "ECON", "ED", "ECE", "ENGR", "ENGL", "ESM", "ENV S", "ESS", "ES", "FEMST", "FAMST", "FR", "GEN S", "GEN SCS", "GEOG", "GER", "GPS", "GLOBL", "GRAD", "GREEK", "HEB", "HIST", "INT", "INT  CS", "ITAL", "JAPAN", "KOR", "LATIN", "LAIS", "LING", "LIT", "LIT  CS", "MARSC", "MATRL", "MATH", "MATH CS", "ME", "MAT", "ME ST", "MES", "MS", "MCDB", "MUS", "MUS  CS","MUS A","PHIL",  "PHYS",  "PHYS CS", "POL S", "PORT", "PSY", "RG ST", "RENST", "SLAV", "SOC","SPAN", "SHS", "PSTAT", "TMP", "THTR",
"WRIT", "W&amp;L", "W&amp;L  CS"));
   
    /* Test the scraping of department list */
    @Test
    public void test_scraped_departments() {
	try{
	UCSBCurriculumSearch cssc = new UCSBCurriculumSearch();
	ArrayList<String> scraped_departments = new ArrayList<String>();
	scraped_departments = cssc.findSubjectAreas(cssc.getMainPage());
	
	//test that lists are the same size
	assertEquals(departments.size(), scraped_departments.size());

	//test that all elements are equal between scraped and hard coded list

	for (int i = 0; i < departments.size(); i++)
	    {
		//somtetimes the scraped department has special whitespace characters that dont change what you see in the GUI so well ignore those
		String d = new String();
		d = departments.get(i).replace(" ", "");
		String j = new String();
		j = scraped_departments.get(i).replace(" ", "");
		assertEquals(j, d);
	    }

	
	}	catch (Exception e){
	System.err.println(e);
	e.printStackTrace();
	}
    }

    

}
