SAP HANA Cloud Platform Extension Package
=========================================

SAP Employee Benefits Management Sample Application
-----------------------------------------------

The *SAP HANA Cloud Platform extension package* makes it quick and easy for companies to adapt and integrate SuccessFactors cloud applications to their existing business processes.

*SAP Employee Benefits Management* is a sample extension application for *SuccessFactors Employee Central*. The application can be used by employees to make orders in specific benefit campaigns and by HR managers to manage employee benefits and set up new benefit campaigns.

The purpose of the application is to showcase the advantages of *SAP HANA Cloud Platform extension package* for development and hosting of applications that extend the *SuccessFactors* capabilities.

The sample application relies on and integrates the following SAP HANA Cloud Platform capabilities:
* *SAP HANA Cloud Platform extension package* that provides SuccessFactors connectivity on top of the standard based OData API [Details] (http://scn.sap.com/docs/DOC-49540)
* Persistency - JPA on top of [SAP HANA Database technology](http://www.saphana.com/welcome) 
* User interface technology - SAPUI5 (sap.m) libraries [Details](https://sapui5.hana.ondemand.com/sdk/test-resources/sap/m/demokit/explored/index.html) 
* Backend logic implemented using OData services (Apache OlingoTM and Google Gson for JSON serialization/deserialization)

Abbreviations
-------------

* **SFSF** - *SuccessFactors*
* **HCP** - *SAP HANA Cloud Platform*

Application Scenario
-------------------

The purpose of the SAP Employee Benefits sample application is to ease the process of benefits management, both for employees and HR managers.

Employees are rewarded with different non-monetary benefits, for example concert tickets, food vouchers, and alike. The application uses abstract "*currency*", called benefit points to evaluate the benefits' worth. In accordance with the company's policy, each employee is entitled a particular amount of benefits' points for a certain period of time. Each rewarding period is called a "*campaign*" and is characterized by specific start and end dates, as well as a particular amount of benefit points. 

The HR manager is responsible for managing the campaigns and providing the employees with the rewards they ordered for a particular campaign.

As an HR *manager*, you can manage benefits from three panes:
* Employees
View all managed employees and details about each employee’s used and total benefit points for the current campaign. In addition, you can see order history of the employee for previous campaigns.
* Benefits
See a list and manage all available benefits in the benefits' portfolio.
* Campaigns
Add and edit benefits campaigns and their details.
* Orders
Edit your active campaign benefit orders and view your past orders

As an *employee*, you can edit your active campaign benefit orders and view your past orders. Also you can see a list of available benefits.

By default, the application has a benefits list that is hard-coded and no campaigns are added. We recommend that you first log in to the application with the HR Administrator user first and to add a benefits campaign. Next, log in as an employee so you can view and add benefits to your order. 

Get the Source Code
-------------------

Clone the Git repository `git clone https://github.com/sap/cloud-sfsf-benefits-ext.git`, or [download the latest release](https://github.com/sap/cloud-sfsf-benefits-ext/zipball/master).

In Eclipse import as *Existing Maven Project* and point to the *pom.xml* file located in *com.sap.hana.cloud.samples.benefits* folder.

Architecture Overview
-------------------

The SAP Employee Benefits Management extension application is split into two main components - backend and frontend.

The backend is implemented in the following packages:

* *com.sap.hana.cloud.samples.benefits.odata.** - OData backend services and model for the user interface
* *com.sap.hana.cloud.samples.benefits.connectivity.** - OData connectivity to SuccessFactors
* *com.sap.hana.cloud.samples.benefits.persistence.** - contains JPA entities and DAO objects.
* *com.sap.hana.cloud.samples.benefits.services.** - web integration logic

The frontend is located in WebContent folder. The structure is the following:
* *view* - UI logic implemented using SAP UI5 XML views following MVC pattern
* *css* - application theming
* *img* - images

The application supports three roles:

* **Administrator** - the company HR manager that administers and manages employees benefits and benefits' campaigns
* **Everyone** - mapped to the regular company employees. You do not have to explicitly assign this role to users
* **Analyzer** – provides access to data for all the campaigns

When configuring application security, assign the appropriate role to the users accessing the application.

The following diagram provides an overview of the SAP Employee Benefits Management architecture:
![alt tag](https://github.com/SAP/cloud-sfsf-benefits-ext/blob/master/com.sap.hana.cloud.samples.benefits/diagrams/application_architecture.png)

 
Project Setup
-------------

SAP Employee Benefits Management application can be run either locally or on SAP HCP.

**Prerequisites**
* Access to SAP HANA Cloud Platform extension package or SAP HANA Cloud Platform trial account
* SAP HANA Cloud Platform development environment:
 - SAP HANA Cloud Platform Tools 
 - SAP JVM version 7.x or JDK 7
 - SAP HANA Cloud Platform SDK for Java Web

You can access the SAP HANA Cloud Platform development environment download page, at: [https://tools.hana.ondemand.com/#cloud](https://tools.hana.ondemand.com/#cloud)
For more information about installing the Java Tools and SDK, see: [Details] (https://help.hana.ondemand.com/help/frameset.htm?e815ca4cbb5710148376c549fd74c0db.html)

SuccessFactors OData API Test system
------------------------------------

We provide a SuccessFactors OData API test system with a predefined set of users and data configured in *READ ONLY* mode. You can access the test system using the following URL:

`https://sfsfbizxtrial.hana.ondemand.com/odata/v2`

That is the Root URL to OData API and it returns ServerErrorException. You need to make some valid OData query like `https://sfsfbizxtrial.hana.ondemand.com/odata/v2/User('bmays1')` to get some response. The authentication method is BASIC and you should use your SAP HANA Cloud Platform trial user and password to authenticate. The application contains pre-delivered HTTP Destination (*sap_hcmcloud_core_odata*) to the SuccessFactors OData API Test system which is stored in the /resources application folder. In SFSF Extension Package accounts the destination would be preconfigured to the corresponding SFSF live instance.
For demo purposes, the request to the test system may contain an **X-Proxy-User-Mapping** header in the **<user|SFSF_user>** format. This header allows you to map a user to one of the following SFSF API test system users: **mbarista1** and **nnnn**. For example, if you execute the following request:

`https://sfsfbizxtrial.hana.ondemand.com/odata/v2/User('i123456')`
with header: *X-Proxy-User-Mapping: i123456|nnnn*
the response will contain results for the *nnnn* SFSF API test system user.

If you specify an SFSF API test system user different than *mbarista1* or *nnnn*, the user will be mapped to *nnnn*.
The following diagram provides an overview of the process flow for the user mapping described above:
 
 ![alt tag](https://github.com/SAP/cloud-sfsf-benefits-ext/blob/master/com.sap.hana.cloud.samples.benefits/diagrams/mapping_architecture.png)

In SAP Employee Benefits Management application, the *X-Proxy-User-Mapping* header is used to map the logged-in user to one of the two predefined SFSF OData API test system users. Depending on whether the role of the logged-in user is assigned the Administrator role or not, the value of the header is as follows:
* For the Administrator role: *&lt;logged_in_user_ID&gt;|mbarista1*
* For non-administrators: *&lt;logged_in_user_ID>&gt;|nnnn*

This allows you to use one and the same user for testing all the available features by changing the Benefits application user roles of the user with which you are logging in. 

Each user assigned with the Administrator role is mapped to the mbarista1 SFSF OData API user. All other users are mapped to the *nnnn* SFSF OData API user.

If the **X-Proxy-User-Mapping** header is missing in the request, the *SuccessFactors OData API* endpoint will automatically map your SAP HANA Cloud Platform user ID to the predefined user *Nancy Nash ('nnnn')* inside SuccessFactors. This would allow you to log in with your SAP HANA Cloud Platform user even though it does not exist in SuccessFactors.

**Note!** The *X-Proxy-User-Mapping* header is used for requests to the test system for demo purposes only and cannot be used in a productive environment. Furthermore, if you are using your own SFSF system, there is no mapping. The requests are processed as they are.

Run on Local Server
-------------------

You can run the application on local server against public SuccessFactors OData API Test system using the predefined HTTP destination *sap_hcmcloud_core_odata*. 

**Configure Connectivity to the SFSF OData API**

1. Import the SuccessFactors OData access destination *sap_hcmcloud_core_odata* from **/resources** application folder into the local server destinations. Follow the procedure [here](https://help.hana.ondemand.com/help/frameset.htm?0334aa5dbb304deb83a30503967b6f8d.html).
2. Fill your SAP HCP user and password in the destination configuration.
3. Proxy server setup (optional) - if you work behind a proxy server, configure the proxy settings of the local server. [Details](https://help.hana.ondemand.com/help/frameset.htm?0334aa5dbb304deb83a30503967b6f8d.html).

**Create Local Users**

To enable local users to access the application, you need to define user IDs in the local user store. We have already defined a pair of users and roles and pre-delivered them with the application in **/resources/neousers.json** file.

Copy the *neousers.json* file into the Local Server */config_master/com.sap.security.um.provider.neo.local* folder and restart the Server in order to load them.

After the user import you will have the following users defined in the system with default password 'sap':

<table>
  <tr>
    <th>ID</th><th>Name</th><th>Role</th>
  </tr>
  <tr>
    <td>mbarista</td><td>Marcia Barista</td><td>Administrator</td>
  </tr>
  <tr>
    <td>nnash</td><td>Nancy Nash</td><td>Employee</td>
  </tr> 
</table>

Each user assigned with the *Administrator* role is mapped to the SFSF user *mbarista1*. All other users are mapped to the SFSF user *nnnn*.

The local user *mbarista* is assigned with the *Administrator* role and is mapped to the SFSF OData API test system user *mbarista1*. The local user *nnash* is assigned with the *Everyone* role and is mapped to the SFSF OData API test system user *nnnn*.

Deploy and run the application on the local server. You should be able to login with the listed users and explore the application.

Run the Application on the SAP HANA Cloud Platform Extension Package Account
----------------------------------------------------------------------------

 1. Deploy the application on your SAP HANA Cloud extension package account: [Details](https://help.hana.ondemand.com/help/frameset.htm?e5dfbc6cbb5710149279f67fb43d4e5d.html)
 2. Configure the application role assignments from the cockpit: [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html)
	
**Note!** If you deploy with the console client, make sure to specify the **--java-version** parameter with value **7**.

**Note!** The application name **must** be **benefits**, otherwise the site autodiscovery will not work properly.

Run the Application on the SAP HANA Cloud Platform Trial Account
----------------------------------------------------------------

It is possible to deploy and run the application on *SAP HANA Cloud Platfrom Trial* account. You need to follow the following steps:

 1. Deploy the application in your *SAP HANA Cloud Platfrom Trial* account.
**Note!** If you deploy with the console client, make sure to specify the **--java-version** parameter with value **7**.
**Note!** The application name **must** be **benefits**, otherwise the site autodiscovery will not work properly.
 2. Import the *SuccessFactors OData API* destination *sap_hcmcloud_core_odata* from **/resources** application folder in the application destinations tab. Follow the procedure: [here](https://help.hana.ondemand.com/help/frameset.htm?a2550c3fcf2b430f94f99072677bf9ec.html).
 3. Fill your SAP HANA Cloud Platform user and password in the destination user/password fields.
 4. Configure the application role assignments from the cockpit: [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html). You basically can add "Administrator" Role to your SAP HANA Cloud Platform user.

Now that you have *SuccessFactors OData API* connectivity configured you should be able to login to the application, with your *SAP HANA Cloud Platform* user and password.

Access the Application
----------------------

After deployment, the application is accessible on the following URL:

`http(s)://host:port/com.sap.hana.cloud.samples.benefits/index.html`

Based on the role of the user with which you are logged in, you will see a page designed for HR manager or for a regular employee (user).

Autodiscovery
-------------

*benefits.spec.xml* file is used to provide the *"Benefits Widget"* OpenSocial widget for the portal. It defines required dependencies and enables the rendering of the widget by Shindig (the OpenSocial container). The file is located in the project root directory. You can find more information about the OpenSocial widgets [here](https://help.hana.ondemand.com/cloud_portal/frameset.htm?d9f9f0bfbc594dbdbb01a0753d522d60.html).

The *BenefitsSite.site.zip* describes the *SAP Corporate Benefits site*, which is automatically discovered by the SAP HANA Cloud Portal and imported into it, when the *benefits* extension is deployed. The site is previously created in the portal, exported as a zip and placed into the project root directory. You can find more information [here](https://help.hana.ondemand.com/cloud_portal/frameset.htm?bcd487ecfe4d4dbb9e4a5cf168e62d71.html).

Important Disclaimers on Security and Legal Aspects
---------------------------------------------------

This document is for informational purposes only. Its content is subject to change without notice, and SAP does not warrant that it is error-free. SAP MAKES NO WARRANTIES, EXPRESS OR IMPLIED, OR OF MERCHANTABILITY, OR FITNESS FOR A PARTICULAR PURPOSE.

**Information Security**

Your SuccessFactors system may (and most probably will) contain personal and confidential data. Make sure to connect ONLY trusted extension applications which deal with such data accordingly and comply to the security requirements of your organization. NEVER connect random or untrusted extension applications neither to your productive, nor to your TEST SuccesFactors system.

**Coding Samples**

Any software coding and/or code lines / strings ("Code") included in this documentation are only examples and are not intended to be used in a productive system environment. The Code is only intended to better explain and visualize the syntax and phrasing rules of certain coding. SAP does not warrant the correctness and completeness of the Code given herein, and SAP shall not be liable for errors or damages caused by the usage of the Code, unless damages were caused by SAP intentionally or by SAP's gross negligence.

Copyright and License
---------------------

Copyright 2013 SAP AG

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
