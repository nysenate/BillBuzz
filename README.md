BillBuzz
===============

BillBuzz is web based subscription service that sends regular digest email of Disqus comments on
Senate legislation to active users. Instead of user accounts, BillBuzz uses emailed confirmation
links to verify all user actions.


Requirements
--------------------

* Maven - Maven2 required.
* Tomcat - Tomcat7 required.
* Java - Java7 required.


Installation
----------------

* Clone the repository.
* Run ``bin/setup.sh`` to do a local maven install of the ``nysenate-java-utils`` dependency.
* Create a new database and load the ``src/main/resources/schema.sql`` file.
* In ``src/main/resources/`` copy ``example.properties`` to ``app.properties`` and fill in blanks as documented.
* ``mvn clean package``
* bin/run.sh UpdateSenators -e src/main/resource/app.properties --year 2009 // repeat for 2011, 2013, etc
* bin/run.sh Setup -e src/main/resource/app.properties
* Copy target/BillBuzz-1.0.war to the Tomcat docRoot and deploy.
* Set up ``UpdateSenators``, ``UpdatePosts``, and ``SendDigests`` on daily cron jobs running in that order.


The Data Process
-------------------

billbuzz_senator has a unique entry for each senator per 2 year session. This table is maintained via the
UpdateSenators script which grabs the latest senator data from NYSenate.gov. Any senator that is currently
active in the current session but not in the latest senator info is marked as inactive and removed from the
subscription listings on the website.

The UpdatePosts script is responsible for keeping our local database of disqus comments and authors up to
date and creating new approvals and updates as necessary. Each bill on OpenLegislation that has been viewed
in a browser has a corresponding Disqus thread. Every post by every author to these threads is tracked in
the database under the corresponding tables. When we pull new posts down they are all saved to the database.
If any comments were approved a new update record is created and linked approval records are created for each
approved comment. After pulling new comments, we fetch updates for all older comments that have no recorded
action taken (marked approved, spam, or deleted) and see if any actions were taken since last time we checked.
All updated comments are saved to the database, any new approvals have corresponding records created.

The SendDigests script combines the approvals and subscription entries for active billbuzz users to generate
individual mailings for each user.


User Workflows
-------------------

The primary workflow for registration and account modification is as follows below:

* User Signup Form -> create inactive user and send automated confirmation email with link to
  the user confirmation page.
* User Confirmation Page -> activate user on page load.
* Update Request Form -> send automated update authorization email with link to the user update
  form.
* Update Form -> update user preferences and (re)activate user on submit.
* Unsubscribe Request Form -> send automated unsubscribe authorization email with link to the
  unsubscribe confirmation page.
* Unsubscribe Confirmation Page -> deactivate user on page load

Covered edge case scenarios:

* A user signs up repeatedly without confirmation: the same confirmation code is used so that
  prior confirmation links remain valid.
* A user attempting to update an unconfirmed account will be asked to first confirm/activate
  their account and try again.
* A user attempting to signup on a deactivated account will be asked to update their existing
  account instead.
* A user attempting to deactivate an unconfirmed account will get an "already inactive" response.


Admin Workflows
--------------------

Sometimes a digest mailing will die. Using the logs, determine the last user that recieved an email.
Then run the SendDigest script with the --startat option with the next user's id. --dryrun can be
used to make sure that the mailing will have the desired effect.


Project Organization
------------------------

* ``gov.nysenate.billbuzz.controller``: Each web page has its own dedicated controller here.
* ``gov.nysenate.billbuzz.disqus``: This package is a fully contained Disqus API client. Only the
  required functionality was implemented at time of writing.
* ``gov.nysenate.billbuzz.model``: All BillBuzz data objects implemented as Beans for a simple 
  data persistence layer via commons-dbutils.
* ``gov.nysenate.billbuzz.scripts``: A collection of java scripts for setting up, running, and
  maintaining a BillBuzz installation.
* ``gov.nysenate.billbuzz.util``: A collection of utilities for common BillBuzz tasks.


Data Model
---------------

* Users have many subscriptions and a single confirmation per action taken (signup, update,
  unsubscribe) with at most one unresolved confirmation per action type.
* When the ``UpdatePosts`` script finds newly approved comments a BillBuzz update is created with
  1 or more BillBuzz approvals.
* Threads have many Posts which each have a single Author.
* Threads represent a single bill with a single sponsoring senator. Not all threads have known or
  "real" senators as sponsors though; sometimes sponsor will be BUDGET or RULES.


Miscellaneous Notes
--------------------------

* Disqus changes their API pretty frequently which could possibly break the XML parser. Make sure to setup
  alerts on cronjob errors.
* DisqusAuthors don't always have ids due to anonymity. As such, BillBuzzAuthor ids can be either
  a unique DisqusID or the Disqus email hash value. This is possibly incorrect behavior but shouldn't
  affect our mailings at all. It just means our data might be poorly normalized.
* The Disqus2BillBuzz conversion can be pretty messy. Any data issues probably be traced back to that
  step somehow.
* Heavy use of maps and inconsistent casing has lead to mailing errors in the past. Be careful.
