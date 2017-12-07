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
   
   0. Create the following tables:
      ```
      CREATE TABLE user_credentials (
        user_id  VARCHAR(32) NOT NULL,
        -- here youe need to put one or more columns identyfying the user eg.
        -- username VARCHAR(50) NOT NULL,
        -- or
        -- email            VARCHAR(50) NOT NULL,
        -- application_name VARCHAR(50) NOT NULL,
        password VARCHAR(82) NOT NULL,
        enabled BOOLEAN NOT NULL,
        deleted BOOLEAN NOT NULL,
        PRIMARY KEY (user_id),
        -- you might add foreign key to your user table eg.
        -- FOREIGN KEY (user_id) REFERENCES uzer(id),
        -- you should also add unique contraint:
        -- UNIQUE (username)
        -- or
        -- UNIQUE (email, application_name)
        --
      );

      CREATE TABLE user_role (
         user_id VARCHAR(32) NOT NULL,
         role    VARCHAR(50) NOT NULL,
         PRIMARY KEY (user_id, role),
         FOREIGN KEY (user_id) REFERENCES uzer (id)
           ON DELETE CASCADE
      );
       
      CREATE TABLE user_session (
         auth_token  VARCHAR(32) NOT NULL,
         user_id     VARCHAR(32) NOT NULL,
         creation_ts BIGINT      NOT NULL,
         deleted_ts  BIGINT,
         PRIMARY KEY (auth_token),
         FOREIGN KEY (user_id) REFERENCES uzer (id)
      );
      ```
      * user_id column should match user id in a table in which you store users. It should be defined as: "_id VARCHAR(32) NOT NULL_"
      * Only hashes of passwords will be kept in the database.
      * Column identifying the user (eg. username) should be _varchars_
      
      You can configure the name of the tables and columns (you can only change the _user_ prefix) by setting the
      _sparkbit.security.user-entity-name_ configuration parameter.
      For example, if you will add to your _application.properties_ 
      ```
      sparkbit.security.user-entity-name=driver
      ```
      The table with roles should look like:
      ```
            CREATE TABLE driver_role (
               driver_id VARCHAR(32) NOT NULL,
               role    VARCHAR(50) NOT NULL,
               PRIMARY KEY (driver_id, role),
               FOREIGN KEY (driver_id) REFERENCES driver (id)
                 ON DELETE CASCADE
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
      	"type" : "authType",
      	// other fields described below
      }
      ```
      Attributes inside authnAttributes must match these configured in _application.properties_.
      _type_ field is optional. Default authentication method is _password_. The chapters below describes this with details.  
      The response will have the following body:
      ```
      {
        "authToken": "11e73e4c9cf04d47aacca0481cdbcc40"
      }
      ```
      0. Password
      Password is the default authorization method.
      Login with password does not require additional configuration.
      Example JSON:
      ```
      {
      	"authnAttributes": {
      	  "email": "user0@foo.bar",
      	  "applicationName": "go11"
      	},
      	"password": "****"
      }
      ```
      Where _password_ is a user's password.
      0. Social - Google
      Login with Google does not require addition columns in database.
      _email_ field in _authnAttributes_ object is mandatory.
      
      To configure Google create bean of type _GoogleResolver_ (you can use DefaultGoogleResolver)
      
      Example JSON:
      ```
      {
      	"authnAttributes": {
      	  "email": "user0@foo.bar",
      	  "applicationName": "go11"
      	},
      	"type" : "google",
      	"googleIdToken": "eyJhbGci...hQhgyUoQ"
      }
      ```
      Where _googleIdToken_ is a Google Id Token from Google response
      
      0. Social - Twitter
      Login with Twitter does not require addition columns in database.
      _email_ field in _authnAttributes_ object is mandatory.

      To configure Twitter create bean of type _TwitterResolver_ (you can use DefaultTwitterResolver)
      
      Example JSON:
      ```
      {
        "authnAttributes": {
      	  "email": "user0@foo.bar",
      	  "applicationName": "go11"
        },
        "type" : "twitter",
	    "oauthToken" : "YxIeEwAAAAAA1S8EAAABXglyneY",
	    "oauthTokenSecret" : "eQIz2zAafTdbvQ9XubVNBIZmE6dMOITD"
      }
      ```
      Where _oauthToken_ is a Twitter access token
      _oauthTokenSecret_ is a Twitter secret
      
      0. Social - Facebook
      Login with Facebook does not require addition columns in database.
      _email_ field in _authnAttributes_ object is mandatory.

      To configure Facebook create bean of type _FacebookResolver_ (you can use DefaultFacebookResolver)
      
      Example JSON:  
      ```
      {
        "authnAttributes": {
          "email": "user0@foo.bar",
          "applicationName": "go11"
        },
        "type" : "facebook",
        "code": "AQAvudrFG...bVSfeNg"
      }
      ```
      OR
      ```
      {
        "authnAttributes": {
          "email": "user0@foo.bar",
          "applicationName": "go11"
        },
        "type" : "facebook",
        "accessToken": "EAAOIS...V4ZCRi5u5"
      }
      ```
      Where _accessToken_ is a Facebook auth token
      _code_ is a code that allows to fetch auth code
      Sparkbit Security supports two flows to auth with Facebook
      
      0. Example configuration
      ```
      @Configuration
      public class SocialLoginConfiguration {
      
          @Bean
          public DefaultGoogleResolver defaultGoogleResolver() {
              // googleClientIds
              return new DefaultGoogleResolver(Collections.singletonList("sdfdfhfgjyht54eytg5rhs"));
          }
      
          @Bean
          public DefaultFacebookResolver defaultFacebookResolver() {
              // appKey, appSecret, redirectUri, verifyUrl
              return new DefaultFacebookResolver("994269", "33e65a5e3fc3", "http://localhost:8080/public/facebook/", "https://graph.facebook.com/me?fields=email");
          }
      
          @Bean
          public DefaultTwitterResolver defaultTwitterResolver() {
              // appKey, appSecret, verifyUrl
              return new DefaultTwitterResolver("RvmepO3iG", "ybnUrBAww", "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true");
          }
          
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
   
0. Removing old sessions
   Never delete old sessions, instead of update _delete_ts_ column.
   If you want to run job that will remove old rows for you - add the following lines to configuration file
   ```
   sparkbit.security.session.deleter.removeOld=true
   # one hour
   sparkbit.security.session.deleter.runEveryMillis=3600000
   # one week
   sparkbit.security.session.deleter.olderThanMinutes=10080
   ```
   You have to also enable scheduling in your Spring context (_@EnableScheduling_).