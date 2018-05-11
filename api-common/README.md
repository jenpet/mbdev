# API Common Module
## Information
This module represents a common abstraction layer regarding the API communication with the Mercedes Benz Developer API(s).
All feign clients which target the API(s) for other modules should be initialized using the `MBDevApiFactory`.

Required tokens will automatically be added due to interceptors and (if expired) updated forcefully. Check the `ApiClientProxy`
for details of the refresh conditions and mechanism. 

The provided error decoder normalizes the error behavior of the calls which are made. Check the package `exceptions` to 
get a gist of the mapping from HTTP status codes to use case exceptions.

If the provided API adapters of other modules are not sufficient you can create customized ones using this module by just
providing an implementation of the `TokenProvider` interface.
```java
yourApi = MBDevApiFactory.create(
        CustomInterface.class,          // valid feign annotated interface 
        BASE_PATH,                      // base path of the target api (host should stay the same)
        tokenProvider                   // token provider for access tokens
        );
``` 