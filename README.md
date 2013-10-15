SAP Employee Benefits Management
==========================================

SAP Employee Benefits Management is a sample extension application to the SuccessFactors Employee Central, showing how to create an extension to the SuccessFactors EC. Simultaneously it can be used as a desktop application as well as a mobile application.

Quick start
-----------

Clone the repo, `git clone https://github.com/sap/***.git`, or [download the latest release](https://github.com/sap/***/zipball/master).

Project Overview
----------------

Here is a basic description of the project. 
Basicly the project contains two main modules - backend and frontend modules. 

The backend module consists of:

  - com.sap.hana.cloud.sample.benefits - the main package of the backend 
  - com.sap.hana.cloud.sample.benefits.persistence - contains persistency logic where JPA technology is used
  - com.sap.hana.cloud.sample.benefits.connectivity - SuccessFactors connectivity - HTTP destinations provided by SAP HANA Cloud Platform consume Success Factors OData API
  - com.sap.hana.cloud.sample.benefits.api - backend services implementation – JAX-RS Services (Apache CXF & Google Gson for JSON serialization/deserialization)
  - com.sap.hana.cloud.sample.benefits.api.frontend - datatype structures suitable for frontend usage

The frontend module consists of:

  - mobile/view - the main directory available at WebContent of the application. It holds the User Interface logic. Used UI technology - UI5 (sap.m) libraries
  - mobile/css - Cascading Style Sheets
  - mobile/img - images

Application startup
-------------------

You can run SAP Employee Benefits Management either locally, or on the Cloud.

Prerequisites: 
An user should have an account to SuccessFactors Employee Central. This means he/she has an user and password for accessing SF OData API



1) Running locally

  - go to pom.xml of the application. At properties node find (or add if missing) the element <sap.cloud.sdk.location> and set the path to the directory where you have the downloaded the SAP HANA Cloud SDK to. The same way find or add the property sap.cloud.sdk.version and set the version of downloaded SAP HANA Cloud SDK. Finally the properties sholuld look something like this: 
		
	    <sap.cloud.sdk.location>_local_path_to_HANA_Clould_SDK_</sap.cloud.sdk.location>
		<sap.cloud.sdk.version>_version_</sap.cloud.sdk.version>
	
  - you have to create a new local SAP HANA Cloud server. In order to do that you have the downloaded the SAP HANA Cloud SDK.
  - double-click on it, Connectivity tab, create a new Destination, named "[sap_hcmcloud_core_odata] and paste the following URL to the URL field:
  
https://SF host name/odata/v2 ; 

Setup the username and password. The username is constructed from username of your account and the company name:
username@company name .

  - if you work behind a proxy server, then you should configure your proxy settings (host and port). Double click on the server,
 go to Overview tab and press the Open launch configuration. In the tab (x)= Arguments, VM Arguments copy this:
 -Dhttp.proxyHost=<yourproxyHost> -Dhttp.proxyPort=<yourProxyPort> -Dhttps.proxyHost=<yourproxyHost> -Dhttps.proxyPort=<yourProxyPort> 
 and set your proxy hosts and ports 
  - create Local users - The users you create should have the same names as the ones persistent on the Employee Central. Double-click on the created server, go to User tab and create new users with the properties required. Set a role for every of your users. 
 Role with name "Everyone" is mandatory and if you want to use the admin UI, then add one more role, named "admin".
 
  - Run the application:

For HR user go to http://<localhost:port>com.sap.hana.cloud.sample.benefits/mobile/index.html

For Employee user go to http://<localhost:port>com.sap.hana.cloud.sample.benefits/mobile/employee.html
 
 2) Running on the Cloud
 
  - go to pom.xml of the application. At properties node find (or add if missing) the element <sap.cloud.sdk.location> and set the path to the directory where you have the downloaded the SAP HANA Cloud SDK to. The same way find or add the property sap.cloud.sdk.version and set the version of downloaded SAP HANA Cloud SDK. Finally the properties sholuld look something like this:
  	
	    <sap.cloud.sdk.location>_local_path_to_HANA_Clould_SDK_</sap.cloud.sdk.location>
		<sap.cloud.sdk.version>_version_</sap.cloud.sdk.version>
	
  - create a server on the SAP HANA Cloud Platform
  - deploy the application
  - double-click on it, Connectivity tab, create a new Destination, named "sap_hcmcloud_core_odata" and paste the following URL to the URL field:
https://SF host name/odata/v2 ; Setup the username and password. The username is constructed from username of your account and the company name: username@company name .
  - assign your user a specific role - go to the accounts page, Authorizations tab, select application from the combo box and the available roles will appear on the right.
 Choose Users from the combo box below and assign the desired role.
  - Run the application


Explore the Application
-----------------------
 The application can be used by employees to make orders in specific benefits campaigns or by HR managers to manage employees’ benefits and set up new benefits campaigns. 
 Each campaign grants the employees with a certain amount of points that they can use to make orders for benefits from the benefits portfolio for the campaign. 
 Different benefits are worth different amount of points.

 For instance you can use Carla Grant's username: cgrant1 as an employee, and Nancy Nash's username: nnnn as Carla's HR manager. 

Note: the users(usernames) that you create on you local/cloud environment should exists on the SuccessFactors Employee Central, so you could connect successfully to the EC.

 When you login for the first time, you should use the HR Manager username. One could goes to "Benefit" page and find out what benefits are available. Next you should go to "Campaign" page and create an active campaign to be used by the employees.
 As an employee one has an access only to "Employee" page. From there the user can creates orders for the active campaign.
  

Authors
-------

**Chavdar Baikov**,
**Marin Hadzhiev**,
**Tsvetelina Marinova**

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
