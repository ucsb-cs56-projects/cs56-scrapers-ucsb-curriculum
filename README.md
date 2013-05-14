# cs56-scrapers-ucsb-curriculum

This is a prototype (incomplete) for an object for screen scraping the Curriculum Search page at this link: https://my.sa.ucsb.edu/public/curriculum/coursesearch.aspx

The long term goal is to be add UCSB Courses directly into a Calendar program (e.g. iCal or Google Calendar). The classes in this package are a first step towards that long-term goal.

* [Archive link](https://foo.cs.ucsb.edu/cs56/issues/0000660/)
* Relevant Mantis links
	* [660](https://foo.cs.ucsb.edu/56mantis/view.php?id=660)
	* [396](https://foo.cs.ucsb.edu/56mantis/view.php?id=396)

## Usage

To run the course scraper, do:

	ant run

If this is the first time running it, it will automatically download the course curriculum search page's SSL certificate. You should verify that this is the correct SSL certificate. After it has installed the SSL certificate, the course scraper will run and show a course listing.
