package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import java.net.*;
import java.io.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;


public class Main{	
/** main method to demonstrate that the page is being accessed
        @param args String arguments in the order of: Department (CMPSC), quarter (Spring), year (2014), and level (Undergraduate)
    */
    public static void main(String [] args) {
        try {
            System.setProperty("javax.net.ssl.trustStore","jssecacerts");

            // Asks for user input and outputs corresponding lectures/sections
            while(true){
                UCSBCurriculumSearch uccs = new UCSBCurriculumSearch();
                System.out.println("Enter the dept, qtr, year, and crs lvl: ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String s = bufferedReader.readLine();
                // Closes program if user inputs empty string
                if(s.equals("")){
                    System.out.println("You have closed the Program.");
                    break;
                }
                String[] inputList = s.split(", ");
                // Checks if user inputs 4 items. If not, goes to next iteration
                if(inputList.length != 4){
                    System.out.println("Error in input format! Try again!\n" +
                                       "Ex. CMPSC, Spring, 2014, Undergraduate");
                    bufferedReader.close();
                    continue;
                }
                String dept = inputList[0]; // The Department
                String qtr = inputList[1]; // The Quarter
                qtr = qtrParse(qtr);
                String year = inputList[2]; //The Year
                qtr = year + qtr; // [YYYYQ, where Q is 1,2,3,4 (1=W, 2=S, 3=M, 4=F)]
                String level = inputList[3]; //The course level: Undergraduate, Graduate, or All

                // Pulls from the html using user input and calls
                // the toString() of the UCSBLectures
                uccs.loadCourses(dept, qtr, level);
                uccs.printLectures();
                bufferedReader.close();
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    /** Parses the quarter to the correct corresponding number that represents it.
        @param qtr string of the quarter e.g Summer, Winter, Fall, Spring
        @return String quarter number (Winter - 1, Spring - 2, Summer - 3, Fall - 4)
    */
    public static String qtrParse(String qtr){
        String tmp = qtr;
        switch(tmp.toUpperCase()){
        case "SUMMER":
            tmp = "3";
            break;
        case "FALL":
            tmp = "4";
            break;
        case "WINTER":
            tmp = "1";
            break;
        case "SPRING":
            tmp = "2";
            break;
        }
        return tmp;
    }
}
