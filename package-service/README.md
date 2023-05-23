Package Challenge Assignment: Create a microservice that allows to perform their packing result. 

Prerequisites

Java 11
Maven 3.8+
Quick Start Clone this repository

Run mvn clean package

Run mvn spring-boot:run

Alternative Import the project in your favourite IDE

Run the file src/main/java/com/mobiquity/PackageServiceApplication.java

Quick run The API can be reached at

1. http://localhost:8080/api/packageFilepath. Send request with filePath as parameter which reads the file from file system and perform packaging operations and return desired output.

2. http://localhost:8080/api/packageFile. Send request with file in body which reads the file from request and perform packaging operations and return desired output.

