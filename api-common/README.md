# API Common Package
## Information
This package represents a common base layer regarding the API communication with the Mercedes Benz Developer API(s).
All feign clients which target the API(s) for other modules should be initialized using the `MBDevApiFactory`.

The provided error decoder normalizes the error behavior of the calls which are made. Check the package `exceptions` to 
get a gist of the mapping from HTTP status codes to use case exceptions. 