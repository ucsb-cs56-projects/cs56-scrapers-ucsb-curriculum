# cs56-scrapers-ucsb-curriculum

This is a prototype (incomplete) for an object for screen scraping the Curriculum Search page at this link: https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx

The long term goal is to be add UCSB Courses directly into a Calendar program (e.g. iCal or Google Calendar). The classes in this package are a first step towards that long-term goal.

project history
===============
```
 ? | jstaahl 4pm | kmai12 | Parser for the UCSB curriculum search
```

## Documentation

The UCSBCurriculumSearch object is designed to load its data automatically from the web page: http://my.sa.ucsb.edu/public/curriculum/. The current version shows a Java approach to solving the "viewstate" problem that arises when working with ASPX websites, as well as illustrating how to do a POST in Java. It also sketches out the java objects needed, though there is lots of work still left to do---mostly, parsing the HTML.

You can find an example of the HTML that you need to be able to parse in the directory [sampleData](https://github.com/UCSB-CS56-Projects/cs56-scrapers-ucsb-curriculum/tree/master/sampleData) in the file [coursesearch2014.html](https://raw.github.com/UCSB-CS56-Projects/cs56-scrapers-ucsb-curriculum/master/sampleData/coursesearch2014.html). This file contains the result of doing a query on CMPSC undergraduate courses for Spring 2014 (retrieved 03/10/2014).

You will see that each lecture or discussion section is represented by a table row that starts with this tag:

			<tr class="CourseInfoRow">
	
You will see that the fields in the UCSBLecture and UCSBSection classes correspond to the fields that can be found in these rows. A few observations may help:

* A lot of redundant information (e.g. long form course description, major restrictions) is included in both the lecture section rows and the discussion section rows. We suggest making the simplifying assumption that all of the sections for a given lecture have the same information for these fields, and so we store that only for the lecture sections.
* In the HTML for each CourseInfoRow there is a field called "section" that has a boolean value of "true" or "false" (spelled out in text). Confusingly, it appears to be "true" for the Lectures, and "false" for the Discussion Sections (which is the opposite of what you might expect.)
* Note the spacing of course numbers—there is more information about this in the Javadoc
* The UCSBCurriculumSearch constructor does an initial read of the URL to initialize two instance variables: viewStateString and eventValString. This is because those fields have to be "echoed back" to the server on every subsequent query, or the server will return a bad status code (HTTP status code 500). You shouldn't have to worry too much about that, though. Your main job is to flesh out the UCSBLecture and UCSBSection classes, and add methods to parse the HTML and initialize the fields in those classes.

## Usage

To run the course scraper, do:

	ant run

If this is the first time running it, it will automatically download the course curriculum search page's SSL certificate. You should verify that this is the correct SSL certificate. After it has installed the SSL certificate, the course scraper will run. Type in a query in the format: DEPARTMENT, Quarter, Year, Level. (e.g. CMPSC, Spring, 2014, Undergraduate)
   Hopefully you will see the following.

NOTE: Be aware that sometimes copying and pasting input will cause an error depending on where you copied the text from.

![](http://imgur.com/TIAro8F.png)
![](http://imgur.com/BC4g9hy.png)

* The untabbed output represents lectures and the tabbed output represents sections.
* To exit the program, just press enter w/o any input.


### Old Archive Info
* [Archive link](https://foo.cs.ucsb.edu/cs56/issues/0000660/)
* Relevant Mantis links
	* [660](https://foo.cs.ucsb.edu/56mantis/view.php?id=660)
	* [396](https://foo.cs.ucsb.edu/56mantis/view.php?id=396)
