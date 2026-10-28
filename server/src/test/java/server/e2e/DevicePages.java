package server.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

final class DevicePages {

    private final Page page;

    DevicePages(Page page) {
        this.page = page;
    }

    Locator row(String deviceName) {
        return page.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHasText(deviceName));
    }

    Locator successMessage(String deviceName) {
        return page.getByText("Пристрій «" + deviceName + "» додано");
    }

    Locator commandsHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Команди").setExact(true));
    }

    Locator addCommandHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Додати команду"));
    }

    Locator commandTitle(String commandName) {
        return page.getByText(commandName, new Page.GetByTextOptions().setExact(true));
    }

    void addDevice(String name, String type) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Додати пристрій")).first().click();
        page.getByLabel("Назва").fill(name);
        page.getByLabel("Тип").selectOption(type);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Додати").setExact(true)).click();
    }

    void openDetails(String deviceName) {
        row(deviceName).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Деталі")).click();
    }

    void addCommand(String name, String argsSchema, String role) {
        page.getByLabel("Назва").fill(name);
        page.getByLabel("Схема аргументів (JSON)").fill(argsSchema);
        page.getByLabel("Доступна роль").selectOption(role);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Додати команду")).click();
    }
}
