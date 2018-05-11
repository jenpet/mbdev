# MBDev Samples
This module holds some sample implementations of other MBDev modules to provide an easy introduction into.  
Create your customized copy of the `mbdev-config.json.templ` configuration file in `src/main/resources/` and name it `mbdev-config.json`. The
test cases will automatically pick it up targeting the production API.

* **./authorization/** `TokenRetrieval.java` uses the [API Auth Module](../api-auth/README.md) to retrieve a valid access token.
* **./connectedvehicle/** `VehicleOperations.java` demonstrates the easy usage of the [Connected Vehicle API Adapter](../connected-vehicle/README.md).