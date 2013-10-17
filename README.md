SAP HANA Cloud Platform Extension Package 
=========================================

Employee Benefits Management Sample Application
-----------------------------------------------

The *SAP HANA Cloud Platform extension package* makes it quick and easy for companies to adapt and integrate SuccessFactors cloud applications to their existing business processes.

*SAP Employee Benefits Management* is a sample extension application for *SuccessFactors Employee Central*. The application can be used by employees to make orders in specific benefits' campaigns and by HR managers to manage employees’ benefits and set up new benefits campaigns.

The purpose of the application is to showcase the advantages of *SAP HANA Cloud Platform extension package* that offers cloud development and hosting platform for applications that extend the SuccessFactors capabilities.

The sample application relies on and integrates the following SAP HANA Cloud Platform capabilities:

* *SAP HANA Cloud Platform extension package* that provides SuccessFactors connectivity  on top of the standard based OData API 
* Persistency – JPA on top of [SAP HANA Database technology](http://www.saphana.com/welcome)
* User interface technology – SAPUI5 (sap.m) libraries [Details](https://sapui5.hana.ondemand.com/sdk/test-resources/sap/m/demokit/explored/index.html)
*  Backend logic implemented using JAX-RS Services (Apache CXF and Google Gson for JSON serialization/deserialization)

Get the Source Code
-------------------

Clone the Git repository `git clone https://github.com/sap/successfactors-benefits-extension.git`, or [download the latest release](https://github.com/sap/successfactors-benefits-extension/zipball/master).

Architecture Overview
---------------------

The *"successfactors-benefits-extension"* application is split into two main components - backend and frontend. 

The backend is implemented in the following packages:

  - *com.sap.hana.cloud.sample.benefits.services.** - REST backend services and model for the user inteface
  - *com.sap.hana.cloud.sample.benefits.connectivity.** - OData connectivity to SuccessFactors  
  - *com.sap.hana.cloud.sample.benefits.persistence.** - contains JPA entities and DAO objects.
  - *com.sap.hana.cloud.sample.benefits.web.** - web integration logic

The frontend is located in WebContent folder. The structure is the following:

  - *mobile/view* - UI logic implemented using XML views following MVC pattern
  - *mobile/css* - application theming
  - *mobile/img* - images
  
The application supports two roles:

- *Administrator* - the company HR manager that administers and manages employees benefits and benefits' campaigns
- *Everyone* - mapped to the company employees. You do not have to explicitly assign this role to users

When configuring application security, assign the appropriate role to the users accessing the application.

Project Setup
-----------------

SAP Employee Benefits Management application can be run either locally or on *SAP HANA Cloud Platform extension package* account.

1. Prerequisites

 * Access to *SAP HANA Cloud Platform extension package* account
 * *SAP HANA Cloud Platform* development enviroment [Details](https://help.hana.ondemand.com/help/frameset.htm?e815ca4cbb5710148376c549fd74c0db.html)

2. Configure the project build

In the project *pom.xml*, set the following properties:

  - *sap.cloud.sdk.location* - set the path to the directory where you have the downloaded the SAP HANA Cloud SDK
  - *sap.cloud.sdk.version* - set the version of downloaded SAP HANA Cloud SDK

For example:

    <sap.cloud.sdk.location>
      C:\develop\neo-sdk-javaweb-1.37.16.2
    </sap.cloud.sdk.location>   
    <sap.cloud.sdk.version>
      1.37.16.2
    </sap.cloud.sdk.version>

Run on Local Server
-------------------

**Configure Connectivity to the Employee Central**

 1. Download the SuccessFactors OData access destination *sap_hcmcloud_core_odata* from  *SAP HANA Cloud Platform extension package* account to a local file. 
 Follow the procedure: [here](https://help.hana.ondemand.com/help/frameset.htm?f02a359183c74429a9d82b23feb15243.html). The destination is located on account level.
 2. Import the destination in the local server [Details](https://help.hana.ondemand.com/help/frameset.htm?0334aa5dbb304deb83a30503967b6f8d.html).
 3. Proxy server setup (optional) - If you work behind a proxy server, configure the proxy settings of the local server. [Details](https://help.hana.ondemand.com/help/frameset.htm?e592cf6cbb57101495d3c28507d20f1b.html).

**Create Local Users**

To enable users to access the application, you need to cerate the users in the local user store. The users you create need to have the same user IDs as the existing users in SuccessFactors Employee Central. This is required in order to match to the newly created users to the existing Employee Central user profiles.

For example:

- "*cgrant1*" - user ID of Carla Grant, a regular employee (user) in the Employee Central
- "*nnnn*" - user ID of Nancy Nash, the HR manager of Carla Grant

To create local users, follow the procedure described in the official documentation. [Details](https://help.hana.ondemand.com/help/frameset.htm?fe47e02fd9514ab889c37250ed771c0c.html).

Assign the *Administrator* role to the relevant HR manager (for example, "*nnnn*").

**JPA Configuration for the Local Server**

By default, the application is configured to run on top of the SAP HANA database. The local server runs with derby, so you need to comment out the following line in the *persistence.xml*

    <!-- 
    <property 
    name="eclipselink.target-database" 
    value="com.sap.persistence.platform.database.HDBPlatform"/> 
    -->

Run the Application on the  SAP HANA Cloud Platform Extension Package Account
----------------------------------------------------------

Deploy the application on your SAP HANA Cloud extension package account: [Details](https://help.hana.ondemand.com/help/frameset.htm?e5dfbc6cbb5710149279f67fb43d4e5d.html)

Configure the application role assigments from the cockpit: [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html)


Access the Application 
----------------------

After deployment, the applciation is accessible on the following URLs:

* Managing benefits and campaigns (HR manager role):

`http://host:port/com.sap.hana.cloud.sample.benefits/mobile/admin.html`

* Simplified user interface for benefit orders (Everyone role):

`http://host:port/com.sap.hana.cloud.sample.benefits/mobile/employee.html`
 
Explore the Application
-----------------------

The purpose of the SAP Employee Benefits sample application is to ease the process of benefits management, both for employees and HR managers.

Employees are rewarded with different non-monetary benefits, for exmaple concert tickets, food vouchers, and alike. The application uses abstract *"currency"*, called *benefit points* to evaluate the benefits' worth. In accordance with the company's policy, each employee is entitled a particular ammount of benefits' points for a certain period of time. Each rewarding period is called a *"campaign"* and is characterized by specific start and end dates, as well as a particular amount of benefit points. 

The HR manager is responsible for managing the campaigns and providing the employees with the rewards they ordered for a particular campaign.

As an *HR manager*, you can manage benefits from three panes:

* Employees

  View all managed employees and details about each employee’s used and total benefit points for the current campaign. In addition, you can see order history of the employee for previous campaigns.

* Benefits
 
  See a list and manage all available benefits in the benefits' portfolio.

* Campaigns

  Add and edit benefits campaigns and their details.

As an *employee*, you can edit your active campaign benefit orders and view your past orders.

By default, the application has a benefits list that is hardcoded and no campaigns added. We recommend that you first log in to the application with the HR Manager user first *("nnnn")* and to add a benefits campaign. Next, log in as an employee *("cgrant")* and add benefits to your order.  

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