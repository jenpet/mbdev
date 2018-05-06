package pet.jen.mbdev.samples.connectedvehicle;

import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.AuthorizationFlowHandler;
import pet.jen.mbdev.connectedvehicle.ConnectedVehicleAdapter;
import pet.jen.mbdev.connectedvehicle.Vehicle;
import pet.jen.mbdev.samples.SampleBase;
import pet.jen.mbdev.samples.config.ConfigParser;
import pet.jen.mbdev.samples.config.SampleConfig;

import java.util.List;

public class VehicleOperations extends SampleBase {
    public static void main(String args[]) throws Exception {
        trustAllHosts();
        SampleConfig config = ConfigParser.getConfig();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config.getOAuthConfig());
        TokenProvider tokenProvider = handler.authorize(config.getUsername(), config.getPassword());
        ConnectedVehicleAdapter vehicleAdapter = new ConnectedVehicleAdapter(tokenProvider);
        List<Vehicle> vehicles = vehicleAdapter.vehicles();
        System.out.println("Retrieved list of vehicles: " + vehicles);
        if(!vehicles.isEmpty()) {
            System.out.println("Retrieved single vehicle: " + vehicleAdapter.vehicle(vehicles.get(0).getId()));
        }
    }
}
