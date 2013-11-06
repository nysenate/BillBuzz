BillBuzz
===============

BillBuzz is web based subscription service that sends regular digest email of Disqus comments on
Senate legislation to active users. Instead of user accounts, BillBuzz uses emailed confirmation
links to verify all user actions.


Requirements
--------------------

* Maven - mvn2 required.
* Tomcat - tomcat7 required.
* Java - java7 preferred, java6 compatible.


Installation
----------------

* Clone the repository.
* Run ``bin/setup.sh`` to do a local maven install of the ``nysenate-java-utils`` dependency.
* Create a new database and load the ``src/main/resources/schema.sql`` file.
* Copy ``src/main/resource/example.properties`` to ``app.properties`` and fill in blanks as documented.
* ``mvn clean package``
* Copy target/BillBuzz-1.0.war to the tomcat docRoot and deploy.
* Set up ``UpdateSenators``, ``UpdatePosts``, and ``SendDigests`` on daily cron jobs running in that order.


User Workflows
-------------------

The primary workflow for registration and account modification is as follows below:

* User Signup Form -> create inactive user and send automated confirmation email.
* User Confirmation Page -> activate user.
* Update Request Form -> send automated update authorization email.
* Update Confirmation Form -> update user preferences and (re)activate user.
* Unsubscribe Request Form -> send automated unsubscribe authorization email.
* Unsubscribe Confirmation Page -> deactivate User

Covered edge case scenarios:

* A user signs up repeatedly without confirmation: the same confirmation code is used so that
prior confirmation links remain valid.
* A user attempting to update an unconfirmed account will be asked to first confirm/activate
their account and try again.
* A user attempting to signup on a deactivated account will be asked to update their existing
account instead.
* A user attempting to deactivate an unconfirmed account will get an "already inactive" response.


Project Organization
------------------------

* ``gov.nysenate.billbuzz.controller``: Each web page has its own dedicated controller here.
* ``gov.nysenate.billbuzz.disqus``: This package is a fully contained Disqus API client. Only the
required functionality was implemented at time of writing.
* ``gov.nysenate.billbuzz.model``: All BillBuzz data objects implemented as Beans for a simple 
data persistence layer.
* ``gov.nysenate.billbuzz.scripts``: A collection of java scripts for setting up, running, and
maintaining a BillBuzz installation.
* ``gov.nysenate.billbuzz.util``: A collection of utilities for common BillBuzz tasks.


Data Model
---------------

* Users have many subscriptions and a single confirmation per unresolved action (signup, update, unsubscribe).
* When the ``UpdateSenators`` script finds new Senators they are created with 1 or more affiliations.
* When the ``UpdatePosts`` script finds newly approved comments a BillBuzz update is created with 1 or more BillBuzz approvals.
* Threads have many Posts which each have a single Author.
* Threads represent a single bill with a single sponsoring senator. Not all threads have known or "real" senators as sponsors though.


Miscellaneous Notes
--------------------------

* DisqusAuthors don't always have ids due to anonymity. As such, BillBuzzAuthor ids can be either
a unique DisqusID or the Disqus email hash value.
* The Disqus2BillBuzz conversion can be pretty messy. Any data issues probably came from here somehow.