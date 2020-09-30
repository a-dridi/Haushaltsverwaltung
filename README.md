# Haushaltsverwaltung

This is an MVC web application that is written in JSF and it is an online database application for commercial or personal usage with many features. You can document your expenses, earnings, wealth, savings, books and much more. It has functions to allow you to calculate your budget and to display graphs about e.g. your earnings. This application can be also connected to your Nextcloud instance (or other web applications that use webdav) to allow you the upload of attachments for your data entry.

![Screenshot of Application Haushaltsverwaltung](https://raw.githubusercontent.com/a-dridi/Haushaltsverwaltung/master/img/screenshot1.PNG)

This application is in German. There is also an unpublished version with a newsticker. If you want to have a translation of this application or you have any other questions/requests, then feel free to contact me. 
A more customized version of this application for production use is also available.

IMPORTANT: Before you install/deploy this application on your server you have to do the configuration.

## Configuration

Please use an IDE (preferably Netbeans) for these configuration steps. 
Firstly download or clone this repository to your computer.

### 1. Edit file hibernate.cfg.xml (located in src/main/resources - in Netbeans in "Other Sources")
Configure the url, username and password according to your Postgresql server settings. You have to create a database with the credentials that you configure in this file.
If your database contains tables that are used in this application (check the package/folder model for that), then change hbm2ddl.auto to create-drop. If you did run the this application and your tables were created then change hbm2ddl.auto to validate. This allows that created tables are validated and no update or new creation of tables are performed in your database. 

### 2. Change the value of the variables baseUrl and downloadurl in all classes in the package controller
Please set up a nextcloud instance to use allow the upload of attachments to your nextcloud instance. Create also the folders with the exact name that is used in the variable "tabellenname". Adjust the variables baseUrl and downloadurl to the domain and url that you use for your nextcloud instance. Change this part "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud" to your nextcloud instance url. Repeat this step for all classes in the package controller. Some classes do not have these variables. 

### 3. Change the value of the variables cloudUsername and cloudPassword in the classes of the package controller to the credentials of your cloud. 
Located after the variables baseUrl and downloadurl (of Step 2). Repeat this step for all the classes in the package controller. Some classes do not have these variables. 

### 4. Create a username and password for the only user/useraccount that can use this application.
In the class AuthentifizierungController (located in package controller): Change the value of the variables username and echtesPasswort to the username and password that you want to use for this application. 

### 5. Now you have the recompile this application project. 
You can find the war file (which is the runnable application) in the target folder of the application project. 

## Installation (Deployment)

This application was tested on Apache Tomee Plume 7 (Tomcat Server Special Edition)
It is preferably if you use the webserver Apache Tomee Plume, because the pom.xml (dependencies) is configured for the usage of this server and server environment. Application was tested on Apache Tomee Plumee Version 7.0.1.

Upload or copy the .war file (you find in the target folder) to your server and move it to the directory webapps (in root directory of your Tomee server). The server will automatically deploy the application on the relative path: /programm_haushaltsverwaltung-1.0
If you want to undeploy this application, then delete the .war file in the webapps folder.
To deploy manually please check the settings of your Tomee server (folder conf).

## Docker 
You can also use Docker to host this application. There is a Dockerfile and a docker-compose.yaml that can be used for that. 
Please recreate the files and folders that are mentioned in the docker-compose.yaml.
You have to build the project with the "hibernate.cfg.xml" of the folder Github repository folder "Docker" and according to the Dockerfile in the folder "webapp" and "db". It should have the name "haushaltsverwaltung.war".

## Authors

* **A. Dridi** - [a-dridi](https://github.com/a-dridi/)
* See also License file

## Video about the application
https://www.youtube.com/watch?v=XlOqgEFX-UI

## Screenshots of the application
![Screenshot showing how to add new data](https://raw.githubusercontent.com/a-dridi/Haushaltsverwaltung/master/img/screenshot2.PNG)
![Screenshot showing data displayed in a graph](https://raw.githubusercontent.com/a-dridi/Haushaltsverwaltung/master/img/screenshot3.PNG)
![Screenshot showing savings function](https://raw.githubusercontent.com/a-dridi/Haushaltsverwaltung/master/img/screenshot4.PNG)
