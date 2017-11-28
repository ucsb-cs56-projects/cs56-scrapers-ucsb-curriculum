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
public class UCSBQuarterAndYearTest {
    /*Init the current quarter and year list. This list is updated as of November 2017. If you wish to test the quarter and year scraping you must update any differences between the current list below and list found in the html code of the main page at https://my.sa.ucsb.edu/publiccurriculum/coursesearch.aspx*/
    ArrayList<String> QandY = new ArrayList<>(Arrays.asList("WINTER 2018", "FALL 2017", "SUMMER 2017", "SPRING 2017", "WINTER 2017", "FALL 2016", "SUMMER 2016", "SPRING 2016", "WINTER 2016", "FALL 2015"));

    /* Test the scraping of Quarter and Year list */
    @Test
    public void test_Quarter_and_Year() {
        try{
        UCSBCurriculumSearch cssc = new UCSBCurriculumSearch();
        ArrayList<String> scraped_QandY = new ArrayList<String>();
        scraped_QandY = cssc.findQuarterAndYear(cssc.getMainPage());

        //test that lists are the same size
        assertEquals(QandY.size(), scraped_QandY.size());

        //test that all elements are equal between scraped and hard coded list

        for (int i = 0; i < QandY.size(); i++)
            {
                //somtetimes the scraped Quarter and Year has special whitespace characters that dont change what you see in the GUI so well ignore those
                String d = new String();
                d = QandY.get(i).replace(" ", "");
                String j = new String();
                j = scraped_QandY.get(i).replace(" ", "");
                assertEquals(j, d);
            }


        }       catch (Exception e){
        System.err.println(e);
        e.printStackTrace();
        }
    }



}
