package pet.jen.mbdev.test;

import org.junit.Test;
import pet.jen.mbdev.api.exception.UnauthorizedException;
import pet.jen.mbdev.connectedvehicle.ConnectedVehicleAdapter;

// Attention: targets the real api
public class ConnectedVehicleAdapterIntegrationTest {

    @Test(expected = UnauthorizedException.class)
    public void testGetVehicles_shouldFailDueToInvalidAccessToken() throws Exception {
        new ConnectedVehicleAdapter().vehicles("invalid-token");
    }
}
