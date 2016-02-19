package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

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
		
		String [] subject = {"ANTH" , "ART", "ART CS", "ARTHI", "ARTST", "AS AM", "ASTRO", "BIOL",
			"BIOL CS", "BMSE","BL ST", "CH E", "CHEM CS", "CHEM", "CH ST", "CHIN", "CLASS",
			"COMM", "C LIT", "CMPSC", "CMPSCCS", "CMPTG", "CMPTGCS", "CNCSP", "DANCE", "DYNS",
			"EARTH", "EACS", "EEMB", "ECON", "ED", "ECE", "ENGR", "ENGL", "ESM", "ENV S", "ESS",
			"ES", "FEMST", "FAMST", "FLMST", "FR", "GEN S", "GEN SCS", "GEOG", "GER", "GPS", "GLOBL",
			"GREEK", "HEB", "HIST", "INT", "INT CS", "ITAL", "JAPAN", "KOR", "LATIN", "LAIS", "LING",
			"LIT", "LIT CS", "MARSC", "MATRL", "MATH", "MATH CS", "ME", "MAT", "ME ST", "MES",
			"MS", "MCDB", "MUS", "MUS CS", "MUS A", "PHIL", "PHYS", "PHYS CS", "POL S", "PORT", "PSY", "RG ST",
			"RENST", "SLAV", "SOC", "SPAN", "SHS", "PSTAT", "TMP", "THTR", "WRIT", "W&L", "W&L CS"};
		
		String [] quarter = {"SPRING" , "WINTER", "FALL", "SUMMER"};
		
		String [] year = {"2016", "2015", "2014"};
		
		String [] level = {"Undergaduate", "Graduate", "ALL"};
		
		
		//creates new drop down selection bar
		JComboBox subjectBox = new JComboBox(subject);
		subjectBox.setEditable(false);
		
		
		JComboBox quarterBox = new JComboBox(quarter);
		quarterBox.setEditable(false);
		
		JComboBox yearBox = new JComboBox(year);
		yearBox.setEditable(false);
		
		JComboBox levelBox = new JComboBox(level);
		levelBox.setEditable(false);
		
		JButton search = new JButton("search");
		
		//adds to JPanel so it uses flow layout, uses minimum size
		JPanel panel = new JPanel();
		panel.add(subjectBox);
		panel.add(quarterBox);
		panel.add(yearBox);
		panel.add(levelBox);
		
		
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String subject = String.valueOf(subjectBox.getSelectedItem());
				String quarter = String.valueOf(quarterBox.getSelectedItem());
				String year = String.valueOf(yearBox.getSelectedItem());
				String level = String.valueOf(levelBox.getSelectedItem());
				
				System.out.println(subject+ ", " + quarter + ", " + year + ", " + level);
			}
		});
		
		panel.add(search);
		
		
		frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE) ;
		frame.getContentPane().add(panel);
		frame.setSize(960,540);
		frame.setVisible(true);
	}
}





//package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;
//
//import javax.swing.*;
//
//import java.awt.*;
//import java.awt.event.*;
//
//public class UCSBgui implements ActionListener{
//	
//	public static void main (String [] args){
//		JFrame frame = new JFrame();
//		
//		String [] subject = {"ANTH" , "ART", "ART CS", "ARTHI", "ARTST", "AS AM", "ASTRO", "BIOL",
//			"BIOL CS", "BMSE","BL ST", "CH E", "CHEM CS", "CHEM", "CH ST", "CHIN", "CLASS",
//			"COMM", "C LIT", "CMPSC", "CMPSCCS", "CMPTG", "CMPTGCS", "CNCSP", "DANCE", "DYNS",
//			"EARTH", "EACS", "EEMB", "ECON", "ED", "ECE", "ENGR", "ENGL", "ESM", "ENV S", "ESS",
//			"ES", "FEMST", "FAMST", "FLMST", "FR", "GEN S", "GEN SCS", "GEOG", "GER", "GPS", "GLOBL",
//			"GREEK", "HEB", "HIST", "INT", "INT CS", "ITAL", "JAPAN", "KOR", "LATIN", "LAIS", "LING",
//			"LIT", "LIT CS", "MARSC", "MATRL", "MATH", "MATH CS", "ME", "MAT", "ME ST", "MES",
//			"MS", "MCDB", "MUS", "MUS CS", "MUS A", "PHIL", "PHYS", "PHYS CS", "POL S", "PORT", "PSY", "RG ST",
//			"RENST", "SLAV", "SOC", "SPAN", "SHS", "PSTAT", "TMP", "THTR", "WRIT", "W&L", "W&L CS"};
//		
//		String [] quarter = {"SPRING" , "WINTER", "FALL", "SUMMER"};
//		
//		String [] year = {"2016", "2015", "2014"};
//		
//		String [] level = {"Undergaduate", "Graduate", "ALL"};
//		
//		
//		//creates new drop down selection bar
//		JComboBox subjectBox = new JComboBox(subject);
//		subjectBox.setEditable(false);
//
//		
//		JComboBox quarterBox = new JComboBox(quarter);
//		quarterBox.setEditable(false);
//		
//		JComboBox yearBox = new JComboBox(year);
//		yearBox.setEditable(false);
//		
//		JComboBox levelBox = new JComboBox(level);
//		levelBox.setEditable(false);
//		
//		JButton search = new JButton("search");
//
//		//adds to JPanel so it uses flow layout, uses minimum size
//		JPanel panel = new JPanel();
//		panel.add(subjectBox);
//		panel.add(quarterBox);
//		panel.add(yearBox);
//		panel.add(levelBox);
//		
//		panel.add(search);
//		
//		search.addActionListener(this);
//		
//		String x = String.valueOf(subjectBox.getSelectedItem());
//		
//		System.out.println(x);
//		
//		
//		frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE) ;
//		frame.getContentPane().add(panel);
//		frame.setSize(960,540);
//		frame.setVisible(true);
//	}
//}
//
//
//
