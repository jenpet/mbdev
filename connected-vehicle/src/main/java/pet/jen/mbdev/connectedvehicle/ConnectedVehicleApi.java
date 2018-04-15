package pet.jen.mbdev.connectedvehicle;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import pet.jen.mbdev.connectedvehicle.domain.Vehicle;

import java.util.List;

@Headers("Accept: application/json")
interface ConnectedVehicleApi {
    @RequestLine("GET /vehicles")
    @Headers("Authorization: Bearer {token}")
    List<Vehicle> vehicles(@Param("token") String token);

    @RequestLine("GET /vehicles/{vin}")
    @Headers("Authorization: Bearer {token}")
    Vehicle vehicle(@Param("token") String token, @Param("vin") String vin);
}
