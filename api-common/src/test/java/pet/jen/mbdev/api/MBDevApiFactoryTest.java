package pet.jen.mbdev.api;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MBDevApiFactoryTest {

    @Test
    public void testCreate_shouldCreateFeignClientForInputClass() throws Exception {
        assertThat(MBDevApiFactory.create(TestClient.class, "/", null)).isInstanceOf(TestClient.class);
    }

    private interface TestClient {}
}