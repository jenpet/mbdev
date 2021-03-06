# API Authentication Module (alpha)<sup>1</sup>
## Preamble
**THIS MODULE IS A PLAIN JAVA IMPLEMENTATION<sup>2</sup> OF THE MERCEDES ME DEVELOPER AUTHORIZATION FLOW - NO SEPARATE BACKEND NEEDED.**

All of the code and the authorization sequences in this package are based on the article 
[Using APIs secured with OAuth 2.0](https://developer.mercedes-benz.com/content-page/oauth-documentation). If any of the documentation (prose or code) uses the term OAuth. It will always be in the context of the OAuth 2.0 authorization sequence. 

## Background
This module is the result of a native login requirement for an Android app without relying on annoying web views accompanied by dozens of redirects. It only requires the relevant configuration based on the registered app in the [Mercedes Benz Developer Console](https://developer.mercedes-benz.com/console). The main goal was to simplify the usage of the token mechanism(s) which is required to interact with mbdev APIs. 

Check the [Usage section](#usage) for a detailed description of how to kick this thing off. The authorization sequence described in the official documentation (see above) results in the following simplified steps:

1. Authorization Endpoint call with app information
2. Get redirected to the login form
3. "Log in" using the form data
4. Get redirected to (another form to) grant a user's consent
5. Get another redirect ( (╯°□°）╯︵ ┻━┻ ) which contains the authorization token
6. Receive the access and refresh tokens using the authorization token
7. (subsequently) Refresh tokens using the refresh token

*Side note: The implementation of step #5 is might be the ugly duckling of this. The redirect has to happen to `http://localhost` or to a failing URI scheme which is valid for feign. Abusing a failed redirect will eventually provide the authorization code.*

The module itself returns a component which holds and provides the access token and handles the expiry by refreshing it in time with a certain buffer.

## <a id="usage"></a>Usage
This section covers the detailed usage of the module. A bit more flexible example (config file based) implementation is shown in [mbdev-samples](../mbdev-samples).

### PKCE Support ###
By enabling the PKCE ([Proof Key for Code Exchange](https://tools.ietf.org/html/rfc7636)) option (see below) in the config the client credentials have to explicitly support this.
Only a client id is required when using this flow. The secret won't be used at any time. The default supported mode returned by the developer console will be non-PKCE compatible client credentials.

### Create an OAuth Config ###
Create an OAuth configuration for your application using the relevant information from your personal [Mercedes Benz Developer Console](https://developer.mercedes-benz.com/console). Most of the values are defined with a reasonable value.

```java
OAuthConfig config = OAuthConfig.builder()
    .authorizationBaseUrl("https://api.secure.mercedes-benz.com")   // set by default
    .loginBaseUrl("https://login.secure.mercedes-benz.com")         // set by default
    .clientId("client-id")                                          // get from mdev console
    .clientSecret("client-secret")                                  // get from mbdev console
    .redirectUri("http://localhost")                                // set by default (@see side note)
    .setTokenExpirybuffer(300)                                      // set by default to five minutes
    .scopes(Arrays.asList("scope1", "scope2"))                      // desired scopes
    .usePKCE(false)                                                 // set by default
    .build();
```

The `TokenExpiryBuffer` describes the time which should be used upfront to refresh a (possibly still valid) access token. Using default settings will refresh all tokens after 55 minutes to avoid having stale data when working with
the access token.

Check the `scopes` you want to use. Since the current API only provides two scopes it is included in the config. To be more flexible it might also be a valid option to pass this along with the user credentials.
In case a user wants to get a token for a client he or she never granted the scopes before the option `defaultApproveMissingScopes` (if set to true) will accept the scopes by default. This is a huge security risk since the user can't decide which scopes
he wants to give access to. A more detailed user interaction will be provided in the future. The option is set to true by default. If set to false and there are missing scopes
the authorization process will stop with an appropriate exception. 

### Trigger Initial Authorization Flow ###
Use the `AuthorizationFlowHandler` to setup the respective feign clients and call it with the user's credentials to retrieve a `TokenProvider` for further API access.

```java
// use in-memory token repository
AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config);

// use a custom token repository to realize persistent session management
AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config, new TokenRepository({...}));
TokenProvider tokenProvider = handler.authorize("username", "password");

// do fancy stuff
tokenProvider.getAccessToken();
...
```

That's basically as easy as it (currently) gets.

### Remember Me Use Case ###
In case an initial authorization flow was already performed and a custom repository implementing the `TokenRepository` interface was used the `AuthorizationFlowHandler` provides a method to instantly retrieve a `TokenProvider`. There is a check whether the data in the repository is valid. In case it is not an exception will be thrown and the authentication should be done again using the initial flow.

```java
// non-empty repository containing previously retrieved token information
TokenRepository repository = new TokenRepository({...});
TokenProvider tokenProvider = AuthorizationFlowHandler.fromRepository(config.getOAuthConfig(), repository);

// and again - do fancy stuff
tokenProvider.getAccessToken();
```

## Future Work / ToDos
* Enhance other API modules to rely on the `TokenProvider` interface to simplify the overall usage
* Improve error handling and introduce fallback mechanisms for failing requests
* Simplify the domain model objects (e.g. session information and consent information)
* Work on the visibility of certain classes to only expose necessary public interfaces / functions

<sup>1</sup> ... barely

<sup>2</sup> One could say nasty workaround which basically mimes a user accepting every precondition the API requires.