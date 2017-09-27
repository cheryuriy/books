# books
* WildFly
* OAuth2, JWT;
* Gradle;
* Spring Boot;
* Hibernate;
* Liquibase;
* PostgreSQL;
* TestNG;
* Protobuf.

-Create, edit books, authors etc... There is 1st, help page:
localhost:8080/books/    - When deploying War with Wildfly. Or:
localhost:8080/  - for embedded Tomcat.

-You can switch to Tomcat if "Tomcat" comments are disabled and "Wildfly" enabled in:
build.gradle
server.Start
server.unit.LoadTest
server.unit.Tests

-You can login with:
http://localhost:8080/books/oauth/token?grant_type=password&username=user&password=password
And with Basic Auth:
Username: trusted-app
Password: secret
- Fot Tomcat delete "books/" in link above.

- You can change Database url( //localhost:5432/books1), username and password for your postgresql
in *.properties files in resources.

- You can change profile from production to "testdb"( or "unit") if you write "testdb" in D:\temp\profile_active.txt
For Tomcat: java -jar -Dspring.profiles.active=testdb books.war




