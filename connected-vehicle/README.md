# Connected Vehicle API Adapter
## Usage
To use the connected vehicle API you need to instantiate the adapter first.
```java
ConnectedVehicleAdapter adapter = new ConnectedVehicleAdapter(
        tokenProvider   // check the examples for the api-auth module usage or implement a custom one
);
```

After that you can access all the methods provided by the adapter to retrieve simple POJOs to work with e.g. vehicles which
call the `/vehicles` REST resource.
```java
List<Vehicle> myVehicles = adapter.vehicles();
```

All of the methods might throw a `MBDevApiException` or a derivate of it. Check the [API Common Module](../api-common/README.md) module for details regarding 
HTTP API response to exception mappings. 