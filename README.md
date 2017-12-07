# cs56-scrapers-ucsb-curriculum

This is a prototype (incomplete) for an object for screen scraping the Curriculum Search page at this link: https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx

The long term goal is to be add UCSB Courses directly into a Calendar program (e.g. iCal or Google Calendar). The classes in this package are a first step towards that long-term goal.

project history
===============
```
F16 | jimvargas 6pm | ryoung141 | Jar compatibility for webapp
W14 | jstaahl 4pm | kmai12 | Parser for the UCSB curriculum search
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
To run the course scraper with a GUI, do: 
	
	ant run

To run the course scraper using just the terminal, do:

	ant cli
	
To generate the Jar file for the cs56-webapps-curriculum repo, do:
```
	ant golderJar
```
The golderTest target is largely unused, except for demo purposes, but can be accessed by:
```
	ant golderTest
```


If this is the first time running it, it will automatically download the course curriculum search page's SSL certificate. You should verify that this is the correct SSL certificate. After it has installed the SSL certificate, the course scraper will run. Once you select the search criteria: DEPARTMENT, Quarter, Year, Level. (e.g. CMPSC, Spring, 2014, Undergraduate)
   Hopefully you will see the following when running the GUI.

![](http://i.imgur.com/GZy6QEG.png)

You should see something similar to this when running the program via terminal. 
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

W16 Final Remarks:

To the next set of students working on this project, I encourage you to look through UCSBCirriculumSearch first. That class is the one that actually goes through the course site and scrapes all of the course data in order to display it to us. If you are not familiar with writing or reading programs that scrape data off of a site, the file will seem difficult to understand and thus I would put most of my effort there. To make matters worse we found some instances of code that was written but never used in that class so there may be more throughout the file. If you're planning on adding search criteria, as one of the next issues I recommend you check out JSoup: ---> http://jsoup.org/. It makes writing scraper code much easier and all it takes is a bit of familiarity with HTML and CSS (well, so does writing the code without JSoup). In addition, you could go even further and refactor the previous scraper code with JSoup entirely! That would make it nicer for future students of the course. The rest of the files in this project are not as complex and are much easier to follow, so just take a look at those to familarize yourself with all aspects of the project.

As for bugs, the most troubling one we found in our time with the project was that the program would seemingly not work at times. We found that the fix for it was to simply refresh the course page in our browser. Once that's done the program would once again find your desried course. We couldn't quite figure out the cause of this bug, but it's something to make note of. 
You could add, as mentioned in the first paragraph, more search criteria to the program as a new feature. Right now you make the search using the aforementioned: DEPT, QUARTER, YEAR, LEVEL system; however, what if you want to just look up "Courses with professor Conrad"? The possibilities here are limited to just what kind of search criteria you may find useful! Porting the program to an Android App would also be pretty handy if you want to search through courses on the go.

F16 Final Remarks:

To the next group of students, we would recommend looking frist at the UCSBCurriculumSearch file as that one contains all of the actual accessing and scraping of the UCSB Curriculum website. It's a fairly complex file, but with some perserverance, things begin to become clear. The rest of the files are much more simple to understand once you understand what is going on in the UCSBCurriculumSearch file. In regards to the UCSBGui file, it has now taken on the role of being a demo for both testing, and presentation, and as such, I would not recommend spending too much time working there. Focus your efforts more on refactoring the UCSBCurriculumSearch file and expanding JUNIT test coverage. 

In addition, make sure to work closely with the group that has  cs56-webapps-curriculum as their project to ensure the things you may be changing are still compatible with their needs as well as expanding functionality to suit their needs. The two projects combined can grow into something awesome, so go out there and make it awesome! 

F17 Final Remarks:

To the next group of students, we would reccomend reading as much of the code as you can before doing aything. Read UCSBCurriculumSearch first that will give you the best idea of how the whole project works. This file will look very ambigous until you inspect the actual HTML code found at the main page URL (https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx). Once you read this you will understand how the scraping process works. By the time you read this the main page URL may have changed and many  of the scraping functions will no longer function. We'd suggest checking out the test files then running ant test first to see what has broken. 
