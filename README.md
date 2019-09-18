# SAP Employee Benefits Management sample application

*SAP Employee Benefits Management* is a sample extension application for *SAP SuccessFactors Employee Central*. The application can be used by employees to make orders in specific benefit campaigns and by HR managers to manage employee benefits and set up new benefit campaigns.

The purpose of this sample application is to show you how to deploy and configure a Java extension application for SAP SuccessFactors on the SAP Cloud Platform.

## System requirements

Before you proceed with the rest of this tutorial, be sure you have access to:
* SAP SuccessFactors test company - you need Administrator permissions for SAP SuccessFactors
* SAP SuccessFactors provisioning
* SAP Cloud Platform subaccount in the Neo environment. **Note that SAP Cloud Platform _Trial is not supported_ by the extension scenario.**

## Development environment requirements

Your environment shall include the following:
* JDK (Java SE Development Kit), version 7 or greater
* [Apache Maven](https://maven.apache.org)
* [Git](https://git-scm.com) client
* [SAP Cloud Platform Tools](https://tools.hana.ondemand.com)
* optionally, a recent version of [Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/eclipse-packages/) instead of Apache Maven and Git
* recent browser

## Configure integration between SAP Cloud Platform and SAP SuccessFactors

:information_source: You can skip this step if you already have a subaccount integrated with your SAP SuccessFactors company.

Before you proceed with the next steps, you have to integrate your SAP SuccessFactors company with a subaccount on the SAP Cloud Platform. Integration is a two steps process:

1. First you have to create an _integration token_. This thing identifies the SAP Cloud Platform subaccount that you will intregrate with your SAP SuccessFactors company. To create a token, open the SAP Cloud Platform Cockpit and select a subaccount that you want to integrate. Of course you can always create a new one. It costs nothing. Then navigate to _Integration Tokens_ and create a new _SAP SuccessFactors Token_. 
2. Once ready, use the token to initiate the integration in SAP SuccessFactors provisioning. The whole process is described in details in the [here](https://help.sap.com/viewer/09c960bc7676452f9232eebb520066cd/1805/en-US/09bb734cf5614896a4cdb66f3e1528ec.html).
Once integration is completed, your subaccount becomes an _extension subaccount_. The two most noticeable things about it are:

First, the extension subaccount's Application Identity Provider (IdP) is configured to be the SuccessFactors IdP. From now on, whenever authentication is required by an application deployed in this subaccount, the end user will be redirected to the SuccessFactors IdP to authenticate himself.  Same is true when the extension subaccount is subscribed for an application running in another, regular subaccount.

And the second important thing is the fact the SAP Cloud Platform has stored metadata describing the pairing. For all the subsequent operations in the extension subaccount you do not need to specify which is your SuccessFactors company. It's already known, and the SAP Cloud Platform will use this knowledge to assist you with the configuration and the lifecycle management of your extension applications.

## Landscape setup
Now it might be tempting to start developing your extension directly in the extension subaccount. But it's worth to consider the following:
* You might already have resources like database and java quotas assigned to another subaccount. Yes, SCP allows you to share or distribute those resources between your subaccounts, but as you will see that's not necessary at all.
* The extension subaccount contains sensitive data - the Service Provider primary key used in backend API authentication flows; the connectivity configurations to your SAP SuccessFactors or third-party systems. In production, only a limited number of people should have access to these. Even the support guys shall not be able to see them.
* If you have extensions from different providers deployed in one and the same subaccount then for maintenance and support all of them shall have access to it. Eventually they will be able to see each other's logs or even change some configurations. 

Fortunately, the SAP Cloud Platform multitenancy concept can help you tackle the above challenges even if your application is not multitenant!
What you need to do is:
* use the extension subaccount only as a configuration container 
* restrict the access to it
* deploy your applications in regular subaccounts 
* and eventually subscribe the extension subaccount to your applications

No matter if your applications are multitenant or not, they will automatically have access to the configurations stored on subscription level. Furthermore, you will not have to share resources between subaccounts; access to sensitive configurations will be restricted and different providers will not be able to see each other's applications. 
 

To experience this model in action we will use two subaccounts to "develop" the sample extension application:
* **dev** subaccount - a regular subaccount with a database and at least one Java VM. It will be used to deploy the application and bind it to a datasource.
* **ext** subaccount - the extension subaccount integrated with your SAP SuccessFactors company. This subaccount will be subscribed to the application deployed in the dev subaccount. All further configurations will be executed on this subscription level. 

## Develop and run the sample application

The first step is to clone this git repository and build the application.

```
git clone https://github.com/SAP/cloud-sfsf-benefits-ext.git
mvn clean install -f cloud-sfsf-benefits-ext
```

### Deploy and start the application
The build process generates a web application archive `ROOT.war` under the `cloud-sfsf-benefits-ext/target` folder. Deploy this archive to your dev subaccount using a tool of your choice - SAP Cloud Platform Cockpit, SDK Tools, maven or Eclipse. The command line for the SDK tools looks like this:

```
neo deploy --account <dev_account_name> --application benefits --host <hana.ondemand.com> --user <SAP_user_id> --source cloud-sfsf-benefits-ext/target/ROOT.war 
```

Before you start the application, you have to bind it to a data source. You can do this via the SAP Cloud Platform Cockpit or in the console using the SDK tools:

```
neo bind-db --account <dev_account_name> --application benefits --host <hana.ondemand.com> --id <db_id> --db-user <db_user> --db-password <db_pwd> --user <SAP_user_id>
```

Now you can start your application:

```
neo start --synchronous --account <dev_account_name> --application benefits --host <hana.ondemand.com> --user <SAP_user_id>
```

### Subscribe the extension subaccount to your application
After the application is up and running you can proceed to configure it to use your SAP SuccessFactors tenant (company). 
Since all the SuccessFactors related configurations will be stored on subscription level to your extension subaccount, the first step is to subscribe the extension subaccount to the application. You have to be `Administrator` or have the `manageSubscriptions` platform scope in both accounts in order to setup the subscription: 

```
neo subscribe --account <ext_account_name> --application <dev_account_name>:benefits --host <hana.ondemand.com> --user <SAP_user_id>
```

Note that the account parameter here points to your extension subaccount, and the application parameter is composed of the name of your dev subaccount, semicolon and the application name.
In all remaining configurations, wherever the application must be referred into a command, you have to use the same pattern with dev account, semicolon and application name.

### Configure Assertion Consumer Service
As already explained, SAP SuccessFactors is configured to be the default IdP for the extension subaccount. If you now access the application, you will be redirected to SuccessFactors for authentication. Assuming you have valid user credentials, the authentication will succeed, but after that you will not be redirected back to the application, because the application is not registered as a trusted Service Provider (SP) in SuccessFactors. To register it, you have to execute the `hcmcloud-enable-application-access` command:

```
neo hcmcloud-enable-application-access --account <ext_account_name> --application <dev_account_name>:benefits --application-type java --host <hana.ondemand.com> --user <SAP_user_id>
```

### Configure backend connectivity
The sample application uses backend connectivity to SAP SuccessFactors to fetch the logged in user details. To configure this connectivity, you use the `hcmcloud-create-connection` SDK command. The result of its execution will be a destination named `sap_hcmcloud_core_odata` on subscription level in the SAP Cloud Platform and a corresponding OAuth client in your SAP SuccessFactors tenant. The destination is configured with `OAuth2SAMLBearerAssertion` authentication and at runtime the end user will be propagated with the backend API calls to SAP SuccessFactors.

```
neo hcmcloud-create-connection --account <ext_account_name> --application <dev_account_name>:benefits --host <hana.ondemand.com> --user <SAP_user_id>
```

### Configure authorization
The next step is to configure the authorization. The sample application relies on the standard Java EE security model for Web Applications - it uses declarative authorization to protect resources and programmatic authorization checks to present dynamic content to the end user according to his roles.
By default, user roles are managed in the SAP Cloud Platform. Although this model is convenient for many use cases, it imposes some challenges when a system with its own role model like SAP SuccessFactors is being extended
* the SAP HR administrator shall have access to the SAP Cloud Platform to manage user permissions
* to do so, he must be trained accordingly
* and last but probably most important - he has to double maintain those permission configurations

To help you overcome those challenges, SAP Cloud Platform provides you the capability to configure you Java applications to use the `SAP SuccessFactors Role Provider` instead of the platform's default. Whenever an authorization check is required by your application, the underlying Java runtime uses the already configured connectivity to execute it against the SAP SuccessFactors system. Of course, there are caches in place to minimize the impact on the application's performance. This allows you to continue using SAP SuccessFactors as a single place to manage user permissions.
Role Provider can be switched in the SAP Cloud Platform Cockpit (under the Roles section) or using the `hcmcloud-enable-role-provider` SDK command:

```
neo hcmcloud-enable-role-provider --account <ext_account_name> --application <dev_account_name>:benefits --host <hana.ondemand.com> --user <SAP_user_id>
```

Once the platform is configured to read the user roles from the SAP SuccessFactors system it is convenient to be able to provision those roles automatically so that SuccessFactors HR administrators do not need to create them manually, but instead just make the necessary user assignments. This is achieved using the `hcmcloud-import-roles` SDK command. It takes as a parameter a JSON file describing the roles required by your application and creates them in the SAP SuccessFactors tenant. There are two important things to notice about this command:
* it does not modify any existing roles 
* it is executed in the scope of subaccount, not application. This means roles can be shared between applications.

The sample application's JSON file with role definitions is [resources/roles.json](resources/roles.json). To create the roles defined in it you use the `hcmcloud-import-roles` SDK command:

```
neo hcmcloud-import-roles --account <ext_account_name> --host <hana.ondemand.com> --user <SAP_user_id> --location cloud-sfsf-benefits-ext/resources/roles.json
```

### Configure UI integration
The last step before you are able to try the application is to integrate it visually in your SAP SuccessFactors tenant. You can achieve this by creating a home page tile - rectangle in the SuccessFactors home page which links to your application or embeds it. Tile capabilities are different, depending on the version of the home page. More details you can find in the [SAP SuccessFactors home page tiles documentation](https://help.sap.com/viewer/DRAFT/59f821da545a4bdb94f1eb8fa22e4b36/LATEST/en-US/00c8674252d1461691bec004be68f425.html)

You can define the home page tiles for your application in a JSON file and import it into the target SuccessFactors system with the `hcmcloud-register-home-page-tiles` SDK command:

```
neo hcmcloud-register-home-page-tiles --account <ext_account_name> --application <dev_account_name>:benefits --application-type java --host <hana.ondemand.com> --user <SAP_user_id> --location cloud-sfsf-benefits-ext/resources/tiles.json
```

The sample application's JSON file with tile definitions is [resources/tiles.json](resources/tiles.json). As you can see, it defines tiles for both home page versions - V12 and the new home page. In this way you can use the same JSON file no matter what is the SuccessFactors home page version.

### Open the application

Now you are ready to open the application. 
Login to your SAP SuccessFactors tenant and on the home page under the News section there shall be a new tile _SAP Corporate Benefits_. Click on it to open the application in a new window. If you click on the _About Me_ tile you will see the user details fetched with a backend call to SAP SuccessFactors.

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
