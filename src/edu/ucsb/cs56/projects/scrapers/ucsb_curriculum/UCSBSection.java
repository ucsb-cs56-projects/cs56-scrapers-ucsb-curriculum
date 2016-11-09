package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

/** UCSBSection -- Stores information from a single discussion section,
    plus contains a reference to the associated lecture section
@author Phill Conrad
@author Kevin Mai
@author Richard Young
@author Jim Vargas
@version cs56.projects.W11, Issue 50
@see UCSBSectionTest
*/

public class UCSBSection {
    private UCSBLecture parent;
    private String status; // e.g. "Full"
    private String enrollCode; // e.g. "07989"
    private String sectionDay; // e.g. "T R"
    private String sectionTime; // e.g. "3:30pm - 4:45pm"
    private String sectionRoom; // e.g. Chem 1171"
    private int enrolled; // e.g. from 63/88 take the 63
    private int capacity; // e.g. from 63/88 take the 88
  
    /**
     * Default Constructor
     */
    public UCSBSection(){ };

    /**
     * Detailed Constructor
     @param parent UCSB lecture object
     @param status string object of the status of a class  e.g. "Full"
     @param enrollCode string object of the enroll code of a class  e.g. "07989"
     @param sectionDay string object of the days where section is held  e.g. "T R"
     @param sectionTime string object of the times when section is held  e.g. "3:30pm - 4:45pm"
     @param sectionRoom  string object of the room where section is held  e.g. Chem 1171"
     @param enrolled integer object of the number of students that are enrolled in the class  e.g. from 63/88 take the 63
     @param capacity integer object of the amount of students that can enroll in the class  e.g. from 63/88 take the 88
    */
    public UCSBSection(UCSBLecture parent, String status, String enrollCode, 
    	String sectionDay, String sectionTime, String sectionRoom, int enrolled, int capacity)
    {
	this.parent = parent;	
	this.status = status;
	this.enrollCode = enrollCode;
	this.sectionDay = sectionDay;
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

    public String getEnrollCode(){ return enrollCode;}
    public void setEnrollCode(String enrollCode){ this.enrollCode = enrollCode;}

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
	    + "\t Section Day: " + sectionDay + "\n"
	    + "\t Section Time: " + sectionTime + "\n"
	    + "\t Section Rm: " + sectionRoom + "\n"
	    + "\t Enrolled / Capacity: " + enrolled + " / " + capacity + "\n";
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UCSBSection other = (UCSBSection) obj;
	if (capacity != other.capacity)
	    return false;
	if (enrollCode != other.enrollCode)
	    return false;
	if (enrolled != other.enrolled)
	    return false;
	if (parent == null) {
	    if (other.parent != null)
		return false;
	} else if (!parent.equals(other.parent))
	    return false;
	if (sectionRoom == null) {
	    if (other.sectionRoom != null)
		return false;
	} else if (!sectionRoom.equals(other.sectionRoom))
	    return false;
	if (sectionTime == null) {
	    if (other.sectionTime != null)
		return false;
	} else if (!sectionTime.equals(other.sectionTime))
	    return false;
	if (status == null) {
	    if (other.status != null)
		return false;
	} else if (!status.equals(other.status))
	    return false;
	return true;
    }
    
}
