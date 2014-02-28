package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

/** UCSBSection -- Stores information from a single discussion section,
    plus contains a reference to the associated lecture section

@author Phill Conrad
@version cs56.projects.W11, Issue 50
@see UCSBSectionTest

*/

public class UCSBSection {
    private UCSBLecture parent;
    private String status; // e.g. "Full"
    private int enrollCode; // e.g. 07989
    private String sectionDay;
    private String sectionTime;
    private String sectionRoom;
    private int enrolled; 
    private int capacity;
  
    // TODO: Write constructor(s), getters/setters, toString(), equals()
    /**
     * Default Constructor
     */
    public UCSBSection(){ };

    /**
     * Detailed Constructor
     */
    public UCSBSection(UCSBLecture parent, String status, int enrollCode, 
		       String sectionTime, String sectionRoom, int enrolled, int capacity)
    {
	this.parent = parent;	
	this.status = status;
	this.enrollCode = enrollCode;
	this.sectionTime = sectionTime;
	this.sectionRoom = sectionRoom;
	this.enrolled = enrolled;
	this.capacity = capacity;
    }

    //Getters and Setters
    public UCSBLecture getParent(){ return parent;}
    public void setParent(UCSBLecture p){ this.parent = p;}

    public String getStatus(){return status;}
    public void setStatus(String s){ this.status = s;}

    public int getEnrollCode(){ return enrollCode;}
    public void setEnrollCode(int enrollCode){ this.enrollCode = enrollCode;}

    public String getSectionDay(){ return sectionDay;}
    public void setSectionDay(String sectionDay){ this.sectionDay = sectionDay;}

    public String getSectionTime(){ return sectionTime;}
    public void setSectionTime(String sectionTime){ this.sectionTime = sectionTime;}

    public String getSectionRoom(){ return sectionRoom;}
    public void setSectionRoom(String sectionRoom){ this.sectionRoom = sectionRoom;}

    public int getEnrolled(){ return enrolled;}
    public void setEnrolled(int enrolled){ this.enrolled = enrolled;}

    public int getCapacity(){ return capacity;}
    public void setCapacity(int capacity){ this.capacity = capacity;}

    @Override
    public String toString() {
	String result;
	result = "\t Course Title: " + parent.getCourseTitle() + "\n"
	    + "\t Section Status: " + status + "\n"
	    + "\t Enroll Code: " + enrollCode + "\n"
	    + "\t Section Time: " + sectionTime + "\n"
	    + "\t Section Rm: " + sectionRoom + "\n"
	    + "\t Enrolled / Capacity: " + enrolled + " / " + capacity + "\n";
	return result;
    }
    
}
