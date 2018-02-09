Justin Owusu, Nathan Inge

a) The project is for scraping the UCSB curriculum search page, the long term goal of the project is having the ability to add UCSB courses directly into a calendar program like Google Calendar.

b) 

c)

d) Potential user Stories: 
- As a user I can add my class schedule to my calendar app so that I can be more organized.

e)





f) The `build.xml` file is built using `ant`. It appears that all of the targets need descriptions, however the README details several targets and what their functions are. The targets and dependencies themselves appear to be up-to-date and accurate. 

g) After looking through the current issues, yes, there are enough issues to earn 1000 points by working through them. Yes, the issues are clear regarding what their expectations are. The first issue listed clearly states that it must be completed first as it outlines a refactor neccessary to allow the project to run successfully because of recent website updates.

h) The bulk of the code is contained in `UCSBCirriculumSearch.java`. This file is well documented and it is obvious what the function of this file is. However, it is a relatively long file (~800 lines) and has potential to be broken up to make testing and refactoring easier. There are several utility functions that get specific information from a given string of HTML (ie lecture tile, department code, section time). It might be beneficial to split these functions off into a different file so refactoring and adding tests for these methods is simplified. Aside from this large file, the rest of the code base is broken up into smaller, more managable classes and files. For example `UCSBSection.java` and `UCSBLecture.java` are both concise and self-explanatory. In addition, there are several test classes (`UCSBDepartmentTest.java`, `UCSBQuarterAndYearTest.java`, `UCSBCirriculumSearchTest.java`) that are well-documented and seem to function as expected. Some of these appear to have been created by the last class as part of the effort to break up `UCSBCirriculumSearch.java`. This might be a good idea for us to continue. 

i) 

j)

