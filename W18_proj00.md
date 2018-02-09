Justin Owusu, Nathan Inge

a) The project is for scraping the UCSB curriculum search page, the long term goal of the project is having the ability to add UCSB courses directly into a calendar program like Google Calendar.

b) Current User Stories:
- As a user I can search for available classes so that I can plan my upcoming schedule accordingly.
- As a user I can view details about past, current, and feature lectures and sections.
- As a user i can search past, current, and feature classes by department, class, and/or level.

c) The program runs and opens the GUI for the program, however there's no functionality because the scraper was made for the Gold website and it received a massive update so now the scraper must be updated for it.

d) Potential User Stories: 
- As a user I can add my class schedule to my calendar app so that I can be more organized.
- As a user I can search for a class by proffessor/TA.
- As a user I can search for a class based on available lecture/section space.
- As a user I can search for a class/section based on location (lecture hall, building).

e) 
Overall, it is a good quality README but there're some things that could be improved. 
- The README starts by saying "The UCSBCurriculumSearch object is designed to load its data automatically from the web page: http://my.sa.ucsb.edu/public/curriculum/." but unfortunately the website just displays a forbidden access page when you go on it. It'd be helpful if a functioning link was added.
- It talks about the Java approach to solving the "viewstate" problem with no brief description of the viewstate problem which would  make the passage more clear.


f) The `build.xml` file is built using `ant`. It appears that all of the targets need descriptions, however the README details several targets and what their functions are. The targets and dependencies themselves appear to be up-to-date and accurate. 

g) After looking through the current issues, yes, there are enough issues to earn 1000 points by working through them. Yes, the issues are clear regarding what their expectations are. The first issue listed clearly states that it must be completed first as it outlines a refactor neccessary to allow the project to run successfully because of recent website updates.

h) Additional issue suggested: 
- https://github.com/ucsb-cs56-projects/cs56-scrapers-ucsb-curriculum/issues/57

i) The bulk of the code is contained in `UCSBCirriculumSearch.java`. This file is well documented and it is obvious what the function of this file is. However, it is a relatively long file (~800 lines) and has potential to be broken up to make testing and refactoring easier. There are several utility functions that get specific information from a given string of HTML (ie lecture tile, department code, section time). It might be beneficial to split these functions off into a different file so refactoring and adding tests for these methods is simplified. Aside from this large file, the rest of the code base is broken up into smaller, more managable classes and files. For example `UCSBSection.java` and `UCSBLecture.java` are both concise and self-explanatory. In addition, there are several test classes (`UCSBDepartmentTest.java`, `UCSBQuarterAndYearTest.java`, `UCSBCirriculumSearchTest.java`) that are well-documented and seem to function as expected. Some of these appear to have been created by the last class as part of the effort to break up `UCSBCirriculumSearch.java`. This might be a good idea for us to continue. 


j) The test coverage appears to be a little sparse. The main file (`UCSBCurriculumSearch.java`) is be far the largest file and contains the most code, yet its test file, `UCSBCurriculumSearch.java`, contains only two test functions contained in 60 lines. For a class that is well over 800 lines, it seems it deserves more test coverage, especially being such an integral part of the project. There are several other test files as mentioned in the previous answer and they are well documented and clear what they are testing, however they too are relatively sparse. `UCSBLectureTest.java` and `UCSBSectionTest.java` are the two most extensive test classes and they correspond directly with respective `Lecture` and `Section` classes. It would be a good idea to add more test cases and break up the `UCSBCurriculumSearchTest.java` into several new test classes. This would be beneficial especially because we have to begin by deciding what to change and how to handle the new website updates. 
