#Sparkbit Security Module User Guide

0. Overview

    The purpose of Sparkbit Security is to provide ready to use authentication module for REST APIs implemented using Spring Boot. This document lists steps that API developers must follow to use this module.
     
0. Authentication Scheme

   To access REST API secured with Sparkbit Security consumer of the API must:

   0. Obtain authentication token (authToken) - it can be done by:
     0. calling _/login_ endpoint
     0. Authenticating with social media account (not yet supported)
   0. Send authToken in X-Sparkbit-Auth-Token header in all subsequent requests to the API.

0. Maven Dependency

   To start using Sparkbit Security add the following dependency to your pom file:
   ```xml
    <dependency>
      <groupId>pl.sparkbit</groupId>
      <artifactId>sparkbit-security</artifactId>
      <version>XYZ</version>
    </dependency>
    ```
0. Spring Boot Configuration

   Your SpringBootApplication must be configured like this:
   ```
   @SpringBootApplication(scanBasePackages = "pl.sparkbit")
   ```
   If for some reason you don't want to scan the entire _pl.sparkbit_ package you can alternatively do:
   ```
   @SpringBootApplication(scanBasePackages = {"pl.sparkbit.security", "pl.sparkbit.commons")
   ```
 
0. Authentication with Password (_/login_ endpoint)

   One way to obtain authToken is to call authenticate with password. In this solution credentials are being kept in the same MySQL database that is used by REST API.
   
   0. In your database you must have table _uzer_ with:
      0. Id column: "_id VARCHAR(32) NOT NULL_"
      0. Password column: "_password VARCHAR(82)_" (only hashes of passwords will be kept in the database)
      0. One or more varchar columns identifying the user (eg. _username_, _email_ or _email_ + _application_name_)

   0. Create the following tables:
      ```apple js
       CREATE TABLE user_role (
         user_id VARCHAR(32) NOT NULL,
         role    VARCHAR(50) NOT NULL,
         PRIMARY KEY (user_id, role),
         FOREIGN KEY (user_id) REFERENCES uzer (id)
           ON DELETE CASCADE
       );
       
       CREATE TABLE session (
         auth_token  VARCHAR(32) NOT NULL,
         user_id     VARCHAR(32) NOT NULL,
         creation_ts BIGINT      NOT NULL,
         PRIMARY KEY (auth_token),
         FOREIGN KEY (user_id) REFERENCES uzer (id)
       );
       ```
       
   0. In _application.properties_ configure which attributes identify the user. For example:
      ```
      sparkbit.security.expected-authn-attributes=email,applicationName
      ```
       with such entry Sparkbit Security will assume presence of _email_ and _application_name_ columns in the database.
       
   0. _/login_ endpoint expects the following request to be sent with POST method:
      ```
      {
      	"authnAttributes": {
      		"email": "user0@foo.bar",
      		"applicationName": "go11"
      	},
      	"password": "1"
      }
      ```
      Attributes inside authnAttributes must match these configured in _application.properties_. The response will have the following body:
      ```
      {
        "authToken": "11e73e4c9cf04d47aacca0481cdbcc40"
      }
      ```
0. Using authToken

   Having obtained _authToken_ you can now access REST API resources.
   
   0. _X-Sparkbit-Auth-Token_ header with value set to _authToken_ should be added to each request to prove caller identity.
   0. Resources matching _/public/**_ pattern are open for everyone and don't require sending _X-Sparkbit-Auth-Token_ header
   0. Resources matching _/admin/**_ pattern require that caller has role "ROLE_ADMIN" (see Spring Security and table _user_role_)
   0. All other resources require role "ROLE_USER"
   0. A special endpoint _/logout_ can be called to invalidate _authToken_. After calling _/logout_ new _authToken_ must be obtained to use the REST API.
   0. When user is authenticated with _authnToken_, security context in Spring application (_SecurityContextHolder.getContext().getAuthentication().getPrincipal()_) contains object of the following class:
   ```
   public class RestUserDetails implements UserDetails {
   
       private final String authToken;
       private final String userId;
       private Collection<GrantedAuthority> roles;
   ```
   
0. Building and running tests

   If you want to build and run tests of Sparkbit Security module you need to create a following MySQL database:
   * URL: system property/env variable _security_test_db_url_ (default _jdbc:mysql://localhost:3306/security_)
   * User system property/env variable _security_test_db_username_ (default _security_)
   * Password system property/env variable _security_test_db_password_ (default _security_)