package pet.jen.mbdev.connectedvehicle;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import pet.jen.mbdev.connectedvehicle.domain.Vehicle;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConnectedVehicleAdapterTest {

    private static final String BASE_PATH = "/experimental/connectedvehicle/v1";

    private ConnectedVehicleAdapter provider;

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(
            WireMockConfiguration.options().port(8889));

    @Rule
    public WireMockClassRule apiMock = wireMockClassRule;

    @Before
    public void setup() {
        provider = new ConnectedVehicleAdapter("http://localhost:8889");
    }


    @Test
    public void testVehicles_shouldCallApiAndReturnListOfVehicles() throws Exception {
        apiMock.stubFor(
                get(urlEqualTo(BASE_PATH + "/vehicles"))
                        .willReturn(
                                aResponse().withStatus(HttpStatus.SC_OK).withBodyFile("bodies/vehicle-list.json"))
        );

        assertThat(provider.vehicles("test")).hasSize(2);
        assertThat(provider.vehicles("test").get(0).getFinorvin()).isEqualTo("WDD***********002");
    }

    @Test
    public void testVehicle_shouldCallApiAndReturnSingleVehicle() throws Exception {
        apiMock.stubFor(
                get(urlEqualTo(BASE_PATH + "/vehicles/WDDKowalle"))
                        .willReturn(
                                aResponse().withStatus(HttpStatus.SC_OK).withBodyFile("bodies/vehicle.json")));

        Vehicle vehicle = provider.vehicle("test", "WDDKowalle");
        assertThat(vehicle.getId()).isEqualTo("77-9BO-_vTsH1LoEdu-_vZwpb_Oz4FO0Frkfskuw3uuKCFSSbeQ7Og3sOr3L815f");
        assertThat(vehicle.getLicenseplate()).isEqualTo("S-GG-116");
        assertThat(vehicle.getSalesdesignation()).isEqualTo("Mercedes-AMG CLA 45 4MATIC Shooting Brake");
        assertThat(vehicle.getFinorvin()).isEqualTo("WDDKowalle");
        assertThat(vehicle.getModelyear()).isEqualTo("2017");
        assertThat(vehicle.getColorname()).isEqualTo("mountaingrau metallic");
        assertThat(vehicle.getFueltype()).isEqualTo("Benzin");
        assertThat(vehicle.getPowerhp()).isEqualTo("381");
        assertThat(vehicle.getPowerkw()).isEqualTo("280");
        assertThat(vehicle.getNumberofdoors()).isEqualTo("5");
        assertThat(vehicle.getNumberofseats()).isEqualTo("5");
    }
}
