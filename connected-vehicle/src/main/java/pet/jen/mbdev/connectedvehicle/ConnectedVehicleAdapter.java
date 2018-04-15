package pet.jen.mbdev.connectedvehicle;

import pet.jen.mbdev.api.MBDevApiFactory;
import pet.jen.mbdev.connectedvehicle.domain.Vehicle;

import java.util.List;

public class ConnectedVehicleAdapter {

    private static final String BASE_PATH = "/experimental/connectedvehicle/v1";

    private final ConnectedVehicleApi api;

    public ConnectedVehicleAdapter() {
        this.api = MBDevApiFactory.create(ConnectedVehicleApi.class, BASE_PATH);
    }

    ConnectedVehicleAdapter(String hostname) {
        this.api = MBDevApiFactory.create(ConnectedVehicleApi.class, hostname, BASE_PATH);
    }

    public List<Vehicle> vehicles(String token) {
        return this.api.vehicles(token);
    }


    public Vehicle vehicle(String token, String vin) {
        return this.api.vehicle(token, vin);
    }
}
