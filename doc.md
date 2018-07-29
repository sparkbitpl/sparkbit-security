#Sparkbit Security Module User Guide

###Overview

The purpose of Sparkbit Security is to provide ready to use authentication module for REST APIs implemented using Spring Boot. This document lists steps that API developers must follow to use this module.
     
> Many of the parameters values, table/column names are configurable, for simplicity in this document we will use default values. For complete list of configurable parameters see `src/main/resources/defaults.yml`
    
###Authentication Scheme

To access REST API secured with Sparkbit Security consumer of the API must:

0. Obtain authentication token (`authToken`) - it can be done by:
    * calling `/login` endpoint
    * Authenticating with social media account (currently Facebook, Twitter, Google)
0. Send `authToken` in `X-Sparkbit-Auth-Token` header or 'sparkbitAuthToken' cookie in all subsequent requests to the API.

> Header name is configurable via `sparkbit.security.auth-token-header-name` property and cookie name by 'auth-token-cookie-name' property.
###Maven Dependency

   To start using Sparkbit Security add the following dependency to your pom file:
   ```xml
    <dependency>
      <groupId>pl.sparkbit</groupId>
      <artifactId>sparkbit-security</artifactId>
      <version>XYZ</version>
    </dependency>
   ```

###Spring Boot Configuration

Your SpringBootApplication must be configured like this:
   ```
   @SpringBootApplication(scanBasePackages = "pl.sparkbit")
   ```
If for some reason you don't want to scan the entire _pl.sparkbit_ package you can alternatively do:
   ```
   @SpringBootApplication(scanBasePackages = {"pl.sparkbit.security", "pl.sparkbit.commons")
   ```
   
###Schema and database configuration

Credentials are being kept in the same MySQL database that is used by REST API.  However only hashes of passwords will be stored in the database.

> Hashing algorithm is configurable through `sparkbit.security.password-encoder-type`. Currently supported are `BCRYPT` (default), `STANDARD`, `PHPASS`
  
In order to setup the database schema:
    
0. Copy the content of `src/main/resources/schema.sql` to your DDL file
0. Optionally adjust the schema by adding the following entries in `application.yml`:
    
```yaml
sparkbit:
  security:
    database-schema:
      user-entity-name: user #prefix that will be added to all sparkbit security tables and column names
      user-table-name: uzer #table name that holds information uniquely identifying user (eg. email)
      user-table-id-column-name: id #column name within 'uzer' table that acts as a primary key
```
        
For example, `sparkbit.security.user-entity-name. driver` requires table for storing roles with definition: 

```sql
CREATE TABLE driver_role (
    driver_id VARCHAR(32) NOT NULL,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (driver_id, role),
    FOREIGN KEY (driver_id) REFERENCES driver (id)
    ON DELETE CASCADE
);
```
              
0. Make sure your schema meets the following criteria:
* `user_id` column matches user id in table `uzer` and is of type: `id VARCHAR(32) NOT NULL`
* Column identifying the user (eg. email) is of type `VARCHAR`

###Authentication

0. In `application.yml` configure which attributes uniquely identify the user in your system. For example:
```yaml
sparkbit:
  security:
    expected-authn-attributes: email, applicationName
```

with such entry Sparkbit Security will assume presence of `email` and `application_name` columns in table `uzer`.
       
0. `/login` endpoint expects the following request to be sent with POST method:
      
```json
{
	"authnAttributes": {
	  "email": "user0@foo.bar",
	  "applicationName": "my-app-name"
	},
	"type" : "authType"
	// other fields described below
}
```
Attributes inside `authnAttributes` must match these configured in `application.properties`.
`type` field is optional. Default authentication method is `password`. The chapters below describes this with details.
  
The response will have the following body:
```json
{
  "authToken": "11e73e4c9cf04d47aacca0481cdbcc40"
}
```
0. Password

    Password is the default authorization method.
    Login with password does not require additional configuration.
    Example JSON:
    ```json
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
    ```json
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
    ```json
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
    ```json
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
    ```json
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
    Sparkbit Security supports two flows to auth with     Facebook

0. Example configuration
    ```java
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
      
###Using authToken

   Having obtained `authToken` you can now access REST API resources.
   
   0. `X-Sparkbit-Auth-Token` header or `auth-token-cookie-name` cookie with value set to `authToken` should be added to each request to prove caller identity.
   0. Resources matching `/public/**` pattern are open for everyone and don't require sending `X-Sparkbit-Auth-Token` header
   0. Resources matching `/admin/**` pattern require that caller has role `ROLE_ADMIN` (see Spring Security and table `user_role`)
   0. All other resources require role `ROLE_USER`
   0. A special endpoint `/logout` can be called to invalidate `authToken`. After calling `/logout` new `authToken` must be obtained to use the REST API.
   0. When user is authenticated with _authnToken_, security context in Spring application (`SecurityContextHolder.getContext().getAuthentication().getPrincipal()`) contains object of the following class:
   ```java
   public class RestUserDetails implements UserDetails {
   
       private final String authToken;
       private final String userId;
       private Collection<GrantedAuthority> roles;
       }
   ```
   
###Building and running tests

   If you want to build and run tests of Sparkbit Security module you need to create a following MySQL database:
   * URL: system property/env variable `mapperTest.db.url` (default `jdbc:mysql://localhost:3306/security`)
   * User system property/env variable `mapperTest.db.username` (default `security`)
   * Password system property/env variable `mapperTest.db.password` (default `security`)
 
### Additional features
  
#### Removing old sessions
    
Never delete old sessions, instead of update `delete_ts` column. By default we run a job that removes old rows. You can disable or configure the job by adding the following entries:
    
```yaml
sparkbit:
  security:
    deleted-session-purging:
      enabled: true
      older-than: 7d
      run-every: 1h
```
   
> You have to enable scheduling in your Spring context (`@EnableScheduling`) for this functionality to work.
        
#### Email verification
    
In order to enable email verification functionality:
         
   0. Add to your `application.yml`: 
     
      ```yaml
      sparkbit:
        security:
          email-verification:
            challenge-validity: 1h #not needed if default is sufficient
            enabled: true
      ```
        
   0. Call the `initiateEmailVerification(String)` on `EmailVerificationService` instance to initiate the flow
   0. Define a `@Component` implementing `EmailVerificationChallengeCallback` interface to handle `EmailVerificationService` callbacks
        
   After the verification process is triggered the user needs to call the `/public/profile/email` endpoint to finish the verification process
        
#### Additional verification
    
When enabled the user needs to send the verification token after login  to `/extraAuthCheck`. Until that happens the user will receive `403` HTTP status code when attempting to access restricted resources.
        
To enable verification:
        
  0. add to your `application.yaml`:
        
     ```yaml
     sparkbit:
       security:
         extra-authn-check:
           challenge-validity: 1h #not needed if default is sufficient
           enabled: true
     ```
        
  0. Define a `@Component` implementing `ExtraAuthnCheckChallengeCallback` interface to send the token to the user (eg. email or SMS)
            
#### Cookies support

Cookie configuration can be modified by adding the following in `application.yml`:
```yaml
sparkbit:
  security:
    allow-unsecured-cookie: false
    auth-token-cookie-name: sparkbitAuthToken
``` 

By default authentication cookie name is `sparkbitAuthToken`. The cookie is set after successful call to `login` and cleared on calling `logout`. Additionally by default cookie will not be set unless using HTTPS protocol. This behavior can be changed by setting `allow-unsecured-cookie: true`   

####CORS configuration

Default CORS configuration is as follows:
```
sparkbit:
  security:   
    cors:
      allow-credentials: true
      allowed-headers: "*"
      allowed-methods: GET, POST, PUT, DELETE, HEAD
      allowed-origins: "*"
      max-age: 30m
      exposed-headers: X-Sparkbit-Session-Expiration-Timestamp
```

These values are used to configure `CorsRegistration` defined in [Spring framework](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/CorsRegistration.html)
#### Password reset
    
Add to your `application.yml`:

```yaml
sparkbit:
  security:
    password-reset:
      challenge-validity: 1h #not needed if default is sufficient
      inform-not-found: false #not needed if default is sufficient
      enabled: true
```      
#### Password change
    
Functionality available under `/profile/password`. Enabled by default. Disable by setting `sparkbit:security:password-change-enabled: false`

#### Default password policy
    
Password policy is configured with the following properties in `application.yml`:

```yaml
sparkbit:
  security:
    default-password-policy:
      enabled: true
      min-password-length: 8
```

By default the embedded policy is enabled. If this is not sufficient it can be disabled and customized by defining a `@Component` that implements `PasswordPolicy` interface.
        
#### Challenge token requirements
    
Each time Sparkbit Security needs to generate a challenge token for:
- email verification
- extra verification
- set new password
- reset password

it will delegate generation of the token to `SecurityChallenges` component. You can change the token requirements by adding to your `application.yml`:
```yaml
sparkbit:
  security:
    challenge-token:
      allowed-characters: 23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz
      length: 6
```
       
#### Session expiration
    
By default `authToken` is valid until revoked by calling `/logout` endpoint. To enable token expiration add to your `application.yml`:
```yaml
sparkbit:
  security:
    session-expiration:
      enabled: true
      duration: 1h #not needed if default is sufficient
      timestamp-header-name: X-Sparkbit-Session-Expiration-Timestamp #not needed if default is sufficient
```
       
If the token expires the session ends and the user needs to login with her full credentials. Each authenticated REST call will extend the time reset duration to `sparkbit.security.session-expiration` 
