package server.e2e;

import org.junit.jupiter.api.Test;
import server.support.E2ETest;
import server.support.SeedData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@E2ETest
class ThymeleafUiE2EIT extends BrowserTestSupport {

    private static final String ARGS_SCHEMA = "{\"type\":\"object\"}";

    @Test
    void owner_ShouldAddDeviceThroughForm() {
        loginAs(SeedData.OWNER_EMAIL);
        DevicePages devices = new DevicePages(page);

        devices.addDevice("Лампа E2E", "LAMP");

        assertThat(devices.successMessage("Лампа E2E")).isVisible();
        assertThat(devices.row("Лампа E2E")).containsText("OWNER");
    }

    @Test
    void owner_ShouldAddCommandAndSeeDuplicateError() {
        loginAs(SeedData.OWNER_EMAIL);
        DevicePages devices = new DevicePages(page);
        devices.openDetails(SeedData.SEED_DEVICE_NAME);

        devices.addCommand("turn_on", ARGS_SCHEMA, "GUEST");

        assertThat(page.getByText("Команду додано.")).isVisible();
        assertThat(devices.commandTitle("turn_on")).isVisible();

        devices.addCommand("turn_on", ARGS_SCHEMA, "GUEST");

        assertThat(page.getByText("Команда з такою назвою вже існує")).isVisible();
    }

    @Test
    void owner_ShouldSeeAddCommandSection_ButGuestDoesNot() {
        loginAs(SeedData.OWNER_EMAIL);
        DevicePages devices = new DevicePages(page);
        devices.openDetails(SeedData.SEED_DEVICE_NAME);

        assertThat(devices.addCommandHeading()).isVisible();

        logout();
        loginAs(SeedData.GUEST_EMAIL);
        devices.openDetails(SeedData.SEED_DEVICE_NAME);

        assertThat(devices.commandsHeading()).isVisible();
        assertThat(devices.addCommandHeading()).hasCount(0);
    }
}
