package edu.ucsb.cs56.projects.scrapers.ucsb_curriculum;

/**
 Class item to use in vector of quarter system
 @param id = 1,2,3,4
 @param description = Winter, Spring, Summer, Fall
 
 */
class Item
{
	private String id;
	private String description;
	
	public Item(String id, String description)
	{
		this.id = id;
		this.description = description;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String toString()
	{
		return description;
	}
}
