# Mercedes Benz Developer API (MBDev)
## Background
**MBDev** builds a Java abstraction layer on top of the [Mercedes Benz Developer APIs](https://developer.mercedes-benz.com/apis) which are available
since mid-end of 2017. Due to the latest enhancements of 2018 it now seems to be reasonable to use the APIs; 
or at least one of them. The most beneficial domain for vehicle addicted developers is the [Connected Vehicle API](https://developer.mercedes-benz.com/apis/connected_vehicle_experimental_api)
which is currently in an _experimental_ state but already provides some insights on how the upcoming API might behave.

This library can be used in Java front-ends and in clients (target version 1.8) to access the API. It is based on 

## Authentication
All of the APIs are secured using [OAuth 2.0](https://tools.ietf.org/html/rfc6749) for which quite a lot of detailed information is described [here](https://developer.mercedes-benz.com/content-page/oauth-documentation).
The currently used authentication mechanisms need a lot of web browser involvement to authenticate applications for certain user accounts which are build using the developer portal.
If you want to access the library using a spring web application check the [Spring Projects / Spring Security OAuth module](https://github.com/spring-projects/spring-security-oauth).

An easy to use implementation for the login / authorization sequence is available in the [API Authentication Module](./api-auth/README.md).
The related pitfalls are described within the corresponding README.md. *Enjoy with care.* ¯\\\_(ツ)\_/¯

## Usage
To see a proper usage of the library please check the corresponding submodule(s) and the [MBDev Samples](./mbdev-samples/README.md).
* [Connected Vehicle API Adapter](./connected-vehicle/README.md)

* [API Authentication Module](./api-auth/README.md)

* [MBDev Samples](./mbdev-samples/README.md)

## Contribution
Feel free to enhance or improve the modules and create pull-requests. Please introduce / modify file headers where significant changes were made.

## License
MBDev is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).