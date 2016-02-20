package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.plaf.basic.*;

import java.io.PrintStream;

public class UCSBgui{
	static JFrame frame;

	
	public static void main (String [] args){
		SwingUtilities.invokeLater(new Runnable() {
	  public void run()
	  {
		  displayJFrame();
	  }
		});
	}
	
	static void displayJFrame() {
		frame = new JFrame();
		
		//Array of all the different departmens on GOLD
		String [] subject = {"ANTH" , "ART", "ART CS", "ARTHI", "ARTST", "AS AM", "ASTRO", "BIOL",
			"BIOL CS", "BMSE","BL ST", "CH E", "CHEM CS", "CHEM", "CH ST", "CHIN", "CLASS",
			"COMM", "C LIT", "CMPSC", "CMPSCCS", "CMPTG", "CMPTGCS", "CNCSP", "DANCE", "DYNS",
			"EARTH", "EACS", "EEMB", "ECON", "ED", "ECE", "ENGR", "ENGL", "ESM", "ENV S", "ESS",
			"ES", "FEMST", "FAMST", "FLMST", "FR", "GEN S", "GEN SCS", "GEOG", "GER", "GPS", "GLOBL",
			"GREEK", "HEB", "HIST", "INT", "INT CS", "ITAL", "JAPAN", "KOR", "LATIN", "LAIS", "LING",
			"LIT", "LIT CS", "MARSC", "MATRL", "MATH", "MATH CS", "ME", "MAT", "ME ST", "MES",
			"MS", "MCDB", "MUS", "MUS CS", "MUS A", "PHIL", "PHYS", "PHYS CS", "POL S", "PORT", "PSY", "RG ST",
			"RENST", "SLAV", "SOC", "SPAN", "SHS", "PSTAT", "TMP", "THTR", "WRIT", "W&L", "W&L CS"};
		
		//Different quarters with their corresponding number ID (used by previous programmers
		//to identify each quarter
		Vector quarter = new Vector();
		quarter.addElement( new Item("1", "Winter"));
		quarter.addElement( new Item("2", "Spring"));
		quarter.addElement( new Item("3", "Summer"));
		quarter.addElement( new Item("4", "Fall"));
		String [] year = {"2016", "2015", "2014"};
		
		//Array of Course Levels
		String [] level = {"Undergraduate", "Graduate", "ALL"};
		
		
		//creates new drop down selection bar
		JComboBox subjectBox = new JComboBox(subject);
		subjectBox.setEditable(false);
		
		
		//Creates ComboBoxes of the aforementioned search criteria
		JComboBox quarterBox = new JComboBox(quarter);
		quarterBox.setEditable(false);
		
		JComboBox yearBox = new JComboBox(year);
		yearBox.setEditable(false);
		
		JComboBox levelBox = new JComboBox(level);
		levelBox.setEditable(false);
		
		//Search Button
		JButton search = new JButton("SEARCH");
		
		
		//Creates textArea that displays your search results
		JTextArea textbox = new JTextArea();
		textbox.setEditable(false);
		
		//Redirects terminal output to GUI
		PrintStream stream = new PrintStream(new CustomOutputStream(textbox));
		System.setOut(stream);
		
		//Makes it scrollable
		JScrollPane scrollbar = new JScrollPane(textbox);

		JPanel panel = new JPanel(new GridLayout(5,0,45,15));
		
		panel.add(subjectBox);

		

		panel.add(quarterBox);
		

		panel.add(yearBox);
		

		panel.add(levelBox);
		
		panel.add(search);
		
		scrollbar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(scrollbar);
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ee) { try{
				textbox.setText(null);
				UCSBCurriculumSearch cssc = new UCSBCurriculumSearch();
				
				String dept = String.valueOf(subjectBox.getSelectedItem());
				
				Item quarter = (Item) quarterBox.getSelectedItem();
				String quarter2 = quarter.getId();
				
				String year = String.valueOf(yearBox.getSelectedItem());
				String lev = String.valueOf(levelBox.getSelectedItem());
				
				
				String qtr = year + quarter2;
				
				
				cssc.loadCourses(dept, qtr, lev);
				cssc.printLectures();
				
				
			}catch (Exception e){
				System.err.println(e);
				e.printStackTrace();
			}
			}
		});
		
		


		
		
		frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE) ;
		frame.getContentPane().add(panel);
		frame.setSize(1500,720);
		frame.setVisible(true);
	}
}





