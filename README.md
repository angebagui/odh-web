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

    git clone https://github.com/regisbamba/opendocshub.git

Then open the configuration file **/conf/application.conf** and change the following properties :

    application.name=OpenDocsHub
    application.fullName=Open Docs Hub
    application.secret=YOUR_APPLICATION_SECRET

    db=postgres://user:pass@host/db
    jpa.dialect=org.hibernate.dialect.PostgreSQLDialect

    google.oAuth.client_id=YOUR_GOOGLE_OAUTH_CLIENT_ID (ex : 123456789.apps.googleusercontent.com)
    google.oAuth.client_secret=YOUR_GOOGLE_OAUTH_CLIENT_SECRET


Then change the redirect url in the Google Api console to http://**your_server_url**/auth/google/token

Then you can run

    play deps --sync
    play evolutions:apply
    play run


Point your browser to **http://your_server_url** if everything goes right you should see the homepage.

If you are developping locally you should point your browser to **http://localhost:9000**

Setting up your platform
-------------------------

By default the first user to login will be set as an admin. So on the home page, click on the **Sign in with Google +** and you will be set as an admin.

Then you can go to **http://your_server_url/admin** to add new categories and set up other users as admin.

