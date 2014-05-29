SAP HANA Cloud Platform Extension Package 
=========================================

SAP Employee Benefits Management Sample Application
-----------------------------------------------

The *SAP HANA Cloud Platform extension package* makes it quick and easy for companies to adapt and integrate SuccessFactors cloud applications to their existing business processes.

*SAP Employee Benefits Management* is a sample extension application for *SuccessFactors Employee Central*. The application can be used by employees to make orders in specific benefit campaigns and by HR managers to manage employee benefits and set up new benefit campaigns.

The purpose of the application is to showcase the advantages of *SAP HANA Cloud Platform extension package* for development and hosting of applications that extend the SuccessFactors capabilities.

The sample application relies on and integrates the following SAP HANA Cloud Platform capabilities:

* *SAP HANA Cloud Platform extension package* that provides SuccessFactors connectivity  on top of the standard based OData API [Details] (http://scn.sap.com/docs/DOC-49540)
* Persistency - JPA on top of [SAP HANA Database technology](http://www.saphana.com/welcome)
* User interface technology - SAPUI5 (sap.m) libraries [Details](https://sapui5.hana.ondemand.com/sdk/test-resources/sap/m/demokit/explored/index.html)
* Backend logic implemented using JAX-RS Services (Apache CXF and Google Gson for JSON serialization/deserialization)

Abbreviations
-------------

* **SFSF** - *SuccessFactors*
* **HCP** - *SAP HANA Cloud Platform*

Get the Source Code
-------------------

Clone the Git repository `git clone https://github.com/sap/cloud-sfsf-benefits-ext.git`, or [download the latest release](https://github.com/sap/cloud-sfsf-benefits-ext/zipball/master).

In Eclipse import as *Existing Maven Project* and point to the *pom.xml* file located in *com.sap.hana.cloud.samples.benefits* folder.

Architecture Overview
---------------------

The SAP Employee Benefits Management extension application is split into two main components - backend and frontend. 

The backend is implemented in the following packages:

  - *com.sap.hana.cloud.samples.benefits.odata.** - OData backend services and model for the user interface
  - *com.sap.hana.cloud.samples.benefits.connectivity.** - OData connectivity to SuccessFactors  
  - *com.sap.hana.cloud.samples.benefits.persistence.** - contains JPA entities and DAO objects.
  - *com.sap.hana.cloud.samples.benefits.services.** - web integration logic

The frontend is located in WebContent folder. The structure is the following:

  - *view* - UI logic implemented using SAP UI5 XML views following MVC pattern
  - *css* - application theming
  - *img* - images
  
The application supports two roles:

- **Administrator** - the company HR manager that administers and manages employees benefits and benefits' campaigns
- **Everyone** - mapped to the regular company employees. You do not have to explicitly assign this role to users

When configuring application security, assign the appropriate role to the users accessing the application.

Project Setup
-------------

SAP Employee Benefits Management application can be run either locally or on *SAP HCP*.

1. Prerequisites

  * Access to *SAP HANA Cloud Platform extension package* or *SAP HANA Cloud Platform trial* account
  * *SAP HANA Cloud Platform* development environment [Details](https://help.hana.ondemand.com/help/frameset.htm?e815ca4cbb5710148376c549fd74c0db.html)

Run on Local Server
-------------------

You can run the application on local server against public SuccessFactors OData API Test system. The system have predefined set of users and data configured in *READ ONLY* mode.

The URL to the OData API Test system is:

    https://sfsfbizxtrial.hana.ondemand.com/odata/v2
	
That is the Root URL to OData API and it returns ServerErrorException.
You need to make some valid OData query like https://sfsfbizxtrial.hana.ondemand.com/odata/v2/User('bmays1') to get some response.
The authentication method is BASIC and you should use your SAP HANA Cloud Platform trial user and password to authenticate.
The application contains pre-delivered HTTP Destination (*sap_hcmcloud_core_odata*) to the SuccessFactors OData API Test system, that is contained in */resources* application folder.
In SFSF Extension Package accounts the destination would be preconfigured to the corresponding SFSF live instance.

**Configure Connectivity to the SFSF OData API**

 1. Import the SuccessFactors OData access destination *sap_hcmcloud_core__odata_* from **/resources** application folder into the local server destinations. 
 Follow the procedure [here](https://help.hana.ondemand.com/help/frameset.htm?0334aa5dbb304deb83a30503967b6f8d.html).
 2. Fill your *SAP HCP* user and password in the destination configuration.
 3. Proxy server setup (optional) - If you work behind a proxy server, configure the proxy settings of the local server. [Details](https://help.hana.ondemand.com/help/frameset.htm?e592cf6cbb57101495d3c28507d20f1b.html).

**Create Local Users**

To enable local users to access the application, you need to define user IDs in the local user store. The user IDs should match existing users in the corresponding SFSF Test System.
We have already defined a set of users and roles and pre-delivered them with the application in **/resources/neousers.json** file.

Copy the *neousers.json* file into the Local Server */config_master/com.sap.security.um.provider.neo.local* folder and restart the Server in order to load them.

After the user import you will have the following users defined in the system with default password **'sap'**:

<table>
  <tr>
    <th>ID</th><th>Name</th><th>Role</th>
  </tr>
  <tr>
    <td>bmays1</td><td>Bonnie Mays</td><td>Administrator</td>
  </tr>
  <tr>
    <td>cccc</td><td>Casey Weimer</td><td>Employee</td>
  </tr> 
  <tr>
    <td>dwong1</td><td>Daniel Wong</td><td>Employee</td>
  </tr>
</table>

The role *Administrator* is assigned to the real HR manager in Employee Central (for example, **"bmays1"**). The role *Everyone* is applied to all other users. You will be seeing a bit more users being managed by Bonnie Mays - but you will not be able to login with them.

Deploy and run the application on the local server. You should be able to login with the listed users and explore the application.

Run the Application on the SAP HANA Cloud Platform Extension Package Account
----------------------------------------------------------

Deploy the application on your SAP HANA Cloud extension package account: [Details](https://help.hana.ondemand.com/help/frameset.htm?e5dfbc6cbb5710149279f67fb43d4e5d.html)

Configure the application role assigments from the cockpit: [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html)

Run the Application on the SAP HANA Cloud Platform Trial Account
----------------------------------------------------------

It is possible to deploy and run the application on *SAP HANA Cloud Platfrom Trial* account. You need to follow the following steps:

 1. Deploy the application in your *SAP HANA Cloud Platfrom Trial* account. 
    **Note!** The application name **must** be **benefits**, otherwise the site autodiscovery will not work properly.
 2. Import the *SuccessFactors OData API* destination *sap_hcmcloud_core_odata* from **/resources** application folder in the application destinations tab. 
 Follow the procedure: [here](https://help.hana.ondemand.com/help/frameset.htm?a2550c3fcf2b430f94f99072677bf9ec.html).
 3. Fill your *SAP HANA Cloud Platform* user and password in the destination user/password fields.
 4. Configure the application role assignments from the cockpit: [Details](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html). You basically can add "Administrator" Role to your *SAP HANA Cloud Platform* user.

Now that you have *SuccessFactors OData API* connectivity configured you should be able to login to the application, with your *SAP HANA Cloud Platform* user and password.

**Note!**
The *SuccessFactors OData API* endpoint would automatically map your SAP HANA Cloud Platform user ID used in the destination to predefined HR Administrator user Nancy Nash ('nnnn') inside SuccessFactors. This would allow you to login with your SAP HANA Cloud Platform user regardless it is not existing in SuccessFactors.

**Running against Local IDP (Optional)**

The aforementioned method have one severe limitation that it would always allow single predefined user Nancy Nash ('nnnn') mapped to your HCP user to login in the application and explore it. In order to explore the application from the view of multiple SuccessFactors users you would have to configure Local IDP and configure it in your SAP HCP Trial account.

You can use the set of users delivered within the application (see Running on Local Server section) and provide them with Local IDP Provider to the cloud instance. Follow he process described [here](https://help.hana.ondemand.com/help/frameset.htm?754818ea63874ea38843ab0ed1928765.html).



Access the Application 
----------------------

After deployment, the application is accessible on the following URL:

`http://host:port/com.sap.hana.cloud.samples.benefits/index.html`

Based on the role of the user with which you are logged in, you will see a page designed for HR manager or for a regular employee(user).

Explore the Application
-----------------------

The purpose of the SAP Employee Benefits sample application is to ease the process of benefits management, both for employees and HR managers.

Employees are rewarded with different non-monetary benefits, for example concert tickets, food vouchers, and alike. The application uses abstract *"currency"*, called *benefit points* to evaluate the benefits' worth. In accordance with the company's policy, each employee is entitled a particular ammount of benefits' points for a certain period of time. Each rewarding period is called a *"campaign"* and is characterized by specific start and end dates, as well as a particular amount of benefit points. 

The HR manager is responsible for managing the campaigns and providing the employees with the rewards they ordered for a particular campaign.

As an *HR manager*, you can manage benefits from three panes:

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

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
