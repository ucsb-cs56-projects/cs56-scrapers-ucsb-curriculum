package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class UCSBgui{
	
	public static void main (String [] args){
		JFrame frame = new JFrame();
		
		String [] subject = {"ANTH" , "ART", "ART CS", "BIO", "CMPSC"};
		//TODO: scrape all the subjects and add to array
		
		
		//creates new drop down selection bar
		JComboBox box = new JComboBox(subject);
		box.setEditable(false);
		//cbox.addActionListener(this);
		

		//adds to JPanel so it uses flow layout, uses minimum size
		JPanel panel = new JPanel();
		panel.add(box);
		
		
		frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE) ;
		frame.getContentPane().add(panel);
		frame.setSize(960,540);
		frame.setVisible(true);
	}
}



//BELOW IS CODE FROM EXAMPLE ONLINE

//import java.awt.Container;
//import java.awt.FlowLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JApplet;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.JTextField;
//
//public class UCSBgui extends JApplet {
//	private String[] description = { "Ebullient", "Obtuse", "Recalcitrant",
//	"Brilliant", "Somnescent", "Timorous", "Florid", "Putrescent" };
//	
//	private JTextField t = new JTextField(15);
//	
//	private JComboBox c = new JComboBox();
//	
//	private JButton b = new JButton("Add items");
//	
//	private int count = 0;
//	
//	public void init() {
//		for (int i = 0; i < 4; i++)
//			c.addItem(description[count++]);
//		t.setEditable(false);
//		b.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if (count < description.length)
//					c.addItem(description[count++]);
//			}
//		});
//		c.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				t.setText("index: " + c.getSelectedIndex() + "   "
//						  + ((JComboBox) e.getSource()).getSelectedItem());
//			}
//		});
//		Container cp = getContentPane();
//		cp.setLayout(new FlowLayout());
//		cp.add(t);
//		cp.add(c);
//		cp.add(b);
//	}
//	
//	public static void main(String[] args) {
//		run(new UCSBgui(), 200, 125);
//	}
//	
//	public static void run(JApplet applet, int width, int height) {
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(applet);
//		frame.setSize(width, height);
//		applet.init();
//		applet.start();
//		frame.setVisible(true);
//	}
//}
