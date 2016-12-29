# cybersecuritybase-project
This is a vulnerable web application coded as a course project for [F-Secure's Cybersecurity MOOC](https://cybersecuritybase.github.io/project/)

**Warning:** if you run this, make sure port 8080 is **not** accessible from the internet.

This project has 1 cool implementation flaw, which allows users to delete expenses from other users' books. See if you can find it? In total it has 3 distinct vulnerabilities that I'm aware of. At least 4 items on [OWASP top10 list](https://www.owasp.org/index.php/Top_10_2013-Top_10)are violated.

When you run it, the db is populated with some data and 2 users:

| Username      | Password      |
| ------------- | ------------- |
| atte          | 1             |
| mikko         | masa          |

---

**A5,A8**: CSRF protection is disabled in config\SecurityConfiguration.java line 23. This should have been the easiest to find and fix just by removing "csrf().disable()" from the code. Here is an article on how to test for CSRF vulnerabilities: https://www.owasp.org/index.php/Testing_for_CSRF_(OTG-SESS-005) This vulnerability violates:

**A6**: Sensitive Data Exposure. All categories for expenses are shared across users. For example, if Tommy writes down "$800 for AIDS medication", other users will be able to see that some user has AIDS. Fixing this would require some work. Categories could be implemented as properties of a book. That way they do not leak to users who don't have read access to that book.

**A7**: Missing Function Level Access Control. Users can delete expenses from other users' books even when they don't have read/write access. This can be achieved by sending the server a POST request to modify an extisting expense entry. Modifications are implemented by creating a new expense and marking the old entry as "not current". A modification request contains the id of the book where the new entry is to be created, and the id of the old entry. Server checks that the user has write access to the book of the new entry. It doesn't check if the old entry belongs to that book! This means that an adversary can provide a book id where they have write access and an expense id from another book. The old expense will be marked as "not current" so it will be treated as a deleted entry.
	