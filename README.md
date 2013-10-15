SAP HANA Cloud Samples - PaulPredicts
==========================================

Paul Predicts is a real-life, productively used app showing how to create a server back-end serving multiple front-end technologies. It is showing all currently available SAP HANA Cloud services. 

Quick start
-----------

Clone the repo, `git clone https://github.com/sap/cloud-paulpredicts.git`, or [download the latest release](https://github.com/sap/cloud-paulpredicts/zipball/master).

Read the how-to blog: http://scn.sap.com/community/developer-center/cloud-platform/blog/2012/12/21/get-ready-for-your-paul-position

Project Overview
----------------

Here is a basic description of the project. The structure is as follows:

com.sap.pto - the main package that holds all other
	adapters - classes for accessing the SAP HANA Cloud Platform's services
	dao - methods for storing, updating and retrieving data from the database
		entities - JPA entities describing the data schema and relations
	importers - classes used for retrieving information from the data provider, which provides XML files
	jobs - background jobs that are being executed repeatedly
	paul - logic for Paul's betting behavior based on a crowd-sourced approach
	services - REST services
		util - code for marshalling and JSON manipulation
	startup - classes related to the initialization of the application
	util - commonly used utility functionalities like constants, user utilities, file uploading utilities etc
		configuration - classes that are used for configuring properties
	
	
Application startup
-------------------

You can run PaulPredicts either locally, or on the Cloud.

1) Running locally
 - go to your computer’s properties, Advanced System Settings, Environment variables and create a new system variable named “NW_CLOUD_SDK_PATH” and 
 enter the path to the directory where you have the downloaded the SAP HANA Cloud SDK to
 - you have to create a new local server
 - double-click on it, Connectivity tab, create a new Destination, named "opta" and paste the following URL to the URL field:
 https://octopuspaul.hana.ondemand.com/ptodata/
 - if you work behind a proxy server, then you should configure your proxy settings (host and port). Double click on the server,
 go to Overview tab and press the Open launch configuration. In the tab (x)= Arguments, VM Arguments copy this:
 -Dhttp.proxyHost=<yourproxyHost> -Dhttp.proxyPort=<yourProxyPort> -Dhttps.proxyHost=<yourproxyHost> -Dhttps.proxyPort=<yourProxyPort> 
 and set your proxy hosts and ports 
 - create Local users - double-click on the created server, go to User tab and create new users with the properties required. Set a role for every of your users.
 Role with name "Everyone" is mandatory and if you want to use the admin UI, then add one more role, named "admin".
 - run MongoDB - it is used for the SAP HANA Cloud's Document Service when running it locally. Download MongoDB from here: http://www.mongodb.org/downloads,
 save the archive, unpack and execute the following command: mongod --dbpath C:\mongodb_data, where "C:\mongodb_data" is an empty directory
 
 Note: when running locally you will receive the mails on your local file system. In your local server's folder/work/mailservice/
 - Run the application
 
 2) Running on the Cloud
 - go to your computer’s properties, Advanced System Settings, Environment variables and create a new system variable named “NW_CLOUD_SDK_PATH” and 
 enter the path to the directory where you have the downloaded the SAP HANA Cloud SDK to.
 - create a server on the SAP HANA Cloud Platform
 - double-click on it, Connectivity tab, create a new Destination, named "opta" and paste the following URL to the URL field:
 https://octopuspaul.hana.ondemand.com/ptodata/
 - for using the Mail Service you have got to complete a put-destination operation with a specially prepared file (you can find one in the current directory,
 named Session.template). Remove the .template extension(!) and fill in data regarding SMTP, username, password etc for you email account.
 The put-destination operation can be completed with the Console Client and with a special properties file, containing information about your SAP HANA Cloud account 
 (you can also find this template in the same folder). Fill in the template and enter the following command in the Console Client: neo put-destination <path to the file with properties>
 - assign your user a specific role - go to the accounts page, Authorizations tab, select application from the combo box and the available roles will appear on the right.
 Choose Users from the combo box below and assign the desired role.
 - Run the application

Versioning
----------

For transparency and insight into our release cycle, and for striving to maintain backward compatibility, the SAP HANA Cloud - Samples project will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the following format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major (and resets the minor and patch)
* New additions without breaking backward compatibility bumps the minor (and resets the patch)
* Bug fixes and misc changes bumps the patch

For more information on SemVer, please visit http://semver.org/

Authors
-------

**John Astill**
**Kiril Dayradzhiev**
**Manjunath Gudisi**
**Petar Ivanov**
**Siyu Liu**
**Tushar Rakesh Saxena**
**Robert Wetzold**

+ http://twitter.com/rwetzold
+ http://github.com/rwetzold

Copyright and license
---------------------

Copyright 2013 SAP AG

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Find the project description at documents/index.html
