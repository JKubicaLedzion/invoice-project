### Invoice manager.

Project done as a part of Java course at CodersTrust.

Spring boot application with 4 databases implemented:
1. one file database,
2. multi file database,
3. SQL database,
4. Hibernate database.

By default multi file database is chosen.

2 REST web services allowing to do CRUD operations on invoices and taxes.
SOAP web service for CRUD operations on invoices.

Code coverage based on JaCoCo plugin - 83%.

#### How to run:

1. mvn clean verify
2. java -jar target/invoices-justyna-1.0-SNAPSHOT.jar 
