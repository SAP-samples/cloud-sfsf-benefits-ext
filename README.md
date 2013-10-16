SAP HANA cloud platform extension package 
=========================================

Employee Benefits Management sample application
-----------------------------------------------

The *SAP HANA cloud platform extension package* makes it quick and easy for companies to adapt and integrate SuccessFactors cloud applications to their existing business processes.

*SAP Employee Benefits Management* is a sample extension application for *SuccessFactors Employee Central*. The application can be used by employees to make orders in specific benefits campaigns and by HR managers to manage employees’ benefits and set up new benefits campaigns.

The purpose of the application is to showcase the advantages of *SAP HANA cloud platform extension package* offering as cloud development and hosting platform for applications that extend the Success Factors capabilities.

The sample application relies on and integrates the following SAP HANA Cloud Platform capabilities:

* *SAP HANA Cloud Platform extension package* which provides Success Factors connectivity  on top of standard based OData API 
* Persistency – JPA on top of [SAP HANA Database technology](http://www.saphana.com/welcome)
* User interface technology – SAP UI5 (sap.m) libraries [Details](https://sapui5.hana.ondemand.com/sdk/test-resources/sap/m/demokit/explored/index.html)
*	Backend logic is implemented using JAX-RS Services (Apache CXF & Google Gson for JSON serialization/deserialization)

Get the Source Code
-------------------

Clone the Git repository `git clone https://github.com/sap/successfactors-benefits-extension.git`, or [download the latest release](https://github.com/sap/successfactors-benefits-extension/zipball/master).

Architecture Overview
---------------------

The "cloud-sf-benefits" application is split in two main components - backend and frontend. 

The backend is implemented in the following packages:

  - *com.sap.hana.cloud.sample.benefits.services.** - REST backend services and model for the user inteface
  - *com.sap.hana.cloud.sample.benefits.connectivity.** - OData connectivity to SuccessFactors  
  - *com.sap.hana.cloud.sample.benefits.persistence.** - contains JPA entities and DAO objects.
  - *com.sap.hana.cloud.sample.benefits.web.** - web integration logic

The frontend is located in WebContent folder. The structure is the following:

  - *mobile/view* - UI logic implemented using XML views following MVC pattern
  - *mobile/css* - application theming
  - *mobile/img* - images
  
The application have two roles:

- *Administrator* - is the company HR manager which administers the benefits. Users administering benefits need this role.
- *Everyone* - is mapped to the company employees. It is not required to assign this role explicitely

When configuring application security, assigne the proper role for the users accessing the application.

Project Setup
-----------------

SAP Employee Benefits Management application can be run either locally or on *SAP HANA cloud platform extension package* account.

1. Prerequisites

 * Access to *SAP HANA cloud platform extension package* account
 * *SAP HANA Cloud Platform* development enviroment [Details](https://help.hana.ondemand.com/help/frameset.htm?e815ca4cbb5710148376c549fd74c0db.html)

2. Configure the project build

 Set the following properties in project *pom.xml*:

  - *sap.cloud.sdk.location* - set the path to the directory where you have the downloaded the SAP HANA Cloud SDK
  - *sap.cloud.sdk.version* - set the version of downloaded SAP HANA Cloud SDK

For example:

    <sap.cloud.sdk.location>
      C:\develop\neo-sdk-javaweb-1.37.16.2
    </sap.cloud.sdk.location>   
    <sap.cloud.sdk.version>
      1.37.16.2
    </sap.cloud.sdk.version>

Run on local server
-------------------

**Configure connectivity to Employee Central**

 1. Download the SuccessFactors OData access destination *sap_hcmcloud_core_odata* from  *SAP HANA cloud platform extension package* account to a local file. Follow the process described [here](https://help.hana.ondemand.com/help/frameset.htm?f02a359183c74429a9d82b23feb15243.html)
 2. Import the destination in the local server [Details](https://help.hana.ondemand.com/help/frameset.htm?0334aa5dbb304deb83a30503967b6f8d.html)
 3. Proxy server setup (optional) - If you work behind a proxy server, configure the proxy settings of the local server. [Details](https://help.hana.ondemand.com/help/frameset.htm?e592cf6cbb57101495d3c28507d20f1b.html)

**Create local users**

In order to access the application you would need to have proper users created in local user store. The users you create should have the same user ids as existing users in Employee Central. This is required in order to match to existing Employee Central user profiles.

For example:

- "*cgrant1*" - is the user id of Carla Grant, which is regular employee in Employee Central
- "*nnnn*" - is the user id of Nancy Nash, which is the HR manager of Carla Grant

To create local users, follow the procedure described in the official documentation. [Details](https://help.hana.ondemand.com/help/frameset.htm?fe47e02fd9514ab889c37250ed771c0c.html).

Assign the *Administrator* role to the proper HR manager (e.g. "*nnnn*").

**JPA Configuration for local server**

By default application is configured to run on top of SAP HANA database. Local server runs with derby so you need to comment out the following line in *persistence.xml*

    <!-- 
    <property 
    name="eclipselink.target-database" 
    value="com.sap.persistence.platform.database.HDBPlatform"/> 
    -->

Run on SAP HANA cloud platform extension package account
----------------------------------------------------------

Deploy the application on your SAP HANA Cloud extension package account. [Details](https://help.hana.ondemand.com/help/frameset.htm?e5dfbc6cbb5710149279f67fb43d4e5d.html)

Configure the application role assigments from cloud cockit. [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html)


Access the application 
----------------------

After deployment the applciation is accessible on the following urls:

* For managing benefits and campaigns

`http://host:port/com.sap.hana.cloud.sample.benefits/mobile/admin.html`

* Simplified UI for benefit orders

`http://host:port/com.sap.hana.cloud.sample.benefits/mobile/employee.html`
 
Explore the Application
-----------------------

In the core of the application is the ability to reward employees with different non monetary benefits e.g concert tickets, food vouchers and alike. The application uses abstract *"currency"* called *benefit points* to value the benefits. According to the company policy each employee is entitled particular ammount of benefit point for certain period of time. Each rewarding period is caleed *"campaign"* and is having specific start, end date and benefit points. HR manager is responsible for managing the campaigns and provide the ordered rewards to the employees.

As *HR manager*, you can manage benefits from three panes:

* Employees

  View all managed employees and details about each employee’s used and total benefit points for the current campaign. In addition, you can see order history of the employee for previous campaigns.

* Benefits
 
  Benefits portfolio is managed here.

* Campaigns

  You can see and manage campaign details.

As *employee*, you can edit your active campaign benefit orders and view your past orders.

By default the application has hardcoded benefits list and no campaigns. It is advisable to login with HR Manager user first *("nnnn")* and create some campaign, from the campaign menu. Then login as some managed employee *("cgrant")* to add benefits to your order.  

Authors
-------

**Chavdar Baikov**,
**Marin Hadzhiev**,
**Tsvetelina Marinova**,
**Petra Lazarova**

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
