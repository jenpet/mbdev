package pet.jen.mbdev.connectedvehicle;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * Represents the REST API for the connected vehicle
 * (https://developer.mercedes-benz.com/apis/connected_vehicle_experimental_api)
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Headers("Accept: application/json")
public interface ConnectedVehicleApi {
    @RequestLine("GET /vehicles")
    List<Vehicle> vehicles();

    @RequestLine("GET /vehicles/{id}")
    Vehicle vehicle(@Param("id") String id);
}
