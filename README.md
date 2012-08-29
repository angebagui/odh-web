The Open Docs Hub Project
=========================

Open Docs Hub is an open source project that leverages Google Drive to create platforms for social sharing of documents and data.

Open Docs Hub is currently under heavy development and quite unstable, but you can already see a working demo at http://www.civdocs.com

Open Docs Hub is powered by the excellent Play! framework and thus will require an environment supporting java for deployment.


Requirements for local development
----------------------------------

* Play! framework 1.2.5
* A database (PostgreSQL, MySQL...)
* Google API client login & secret

Installation
------------

First clone the repo

    git clone repo

Then create or update the following environment variables :

* **ODH_APP_NAME** (Your application name, ex : opendocshub)
* **ODH_FULL_APP_NAME** (Your application full name, ex : The Open Docs Hub Project)
* **ODH_DATABASE_URL** (The url to your database, ex : postgres://user:pwd@host/database)
* **ODH_JPA_DIALECT** (The JPA dialect, ex : org.hibernate.dialect.PostgreSQLDialect)
* **ODH_GOOGLE_OAUTH_CLIENT** (The Google OAuth client id, ex : 123456789.apps.googleusercontent.com)
* **ODH_GOOGLE_OAUTH_SECRET** (The Google OAuth client secret)

Then change the redirect url in the Google Api console to http://**your_server_url**/auth/google/token

Then you can run

    play deps --sync
    play evolutions:apply
    play run
