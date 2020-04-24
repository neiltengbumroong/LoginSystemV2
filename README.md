# LoginSystemV2

A second, improved version of LoginSystemV1 that writes to an external SQLite database using JDBC. <br> 
Core functionality includes: <br>
* user creation and user login
* unique user ID 
* password encryption
* external storage via JDBC database
* CSV file parsing for existing data

To run, fork and clone repository to your local machine. <br>
Compile files with: <br> 
```
javac *.java
```
And run with: <br>
```
java -cp .:sqlite-jdbc-3.30.1.jar Program
```
Data is stored in the following schema with example data. 
![Sample Database](/database_functionality.jpg?raw=true "Database")
