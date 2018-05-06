package pet.jen.mbdev.connectedvehicle;

import pet.jen.mbdev.api.MBDevApiFactory;
import pet.jen.mbdev.api.TokenProvider;

import java.util.List;

/**
 * Wraps and abstracts the connected vehicle API via a Feign client.
 * The key aspect is creating an API via the {@link MBDevApiFactory} which creates it accordingly.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class ConnectedVehicleAdapter {

    private static final String BASE_PATH = "/experimental/connectedvehicle/v1";

    private final ConnectedVehicleApi api;

    public ConnectedVehicleAdapter(TokenProvider tokenProvider) {
        this.api = MBDevApiFactory.create(ConnectedVehicleApi.class, BASE_PATH, tokenProvider);
    }

    public ConnectedVehicleAdapter(TokenProvider tokenProvider, String hostname) {
        this.api = MBDevApiFactory.create(ConnectedVehicleApi.class, hostname, BASE_PATH, tokenProvider);
    }

    public List<Vehicle> vehicles() {
        return this.api.vehicles();
    }

    public Vehicle vehicle(String id) {
        return this.api.vehicle(id);
    }
}
