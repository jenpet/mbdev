# Connected Vehicle API Adapter
## Usage
To use the connected vehicle API you need to instantiate the adapter first.
```java
ConnectedVehicleAdapter adapter = new ConnectedVehicleAdapter();
```

After that you can access all the methods provided by the adapter to retrieve simple POJOs to work with e.g. vehicles which
call the `/vehicles` REST resource. Keep in mind that you have to pass the access-token received via the OAuth 2.0 flow as mentioned in the 
main [README.md  file](../README.md).
```java
List<Vehicle> myVehicles = adapter.vehicles("2a99ba20-a9ae-42cf-a00b-657ed24aad3b");
```

All of the methods might throw a `MBDevApiException` or a derivate of it. Check the `api-common` module for details regarding 
HTTP API response to exception mappings. 