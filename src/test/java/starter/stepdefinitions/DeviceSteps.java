package starter.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.annotations.Managed;
import org.openqa.selenium.WebDriver;
import starter.devices.Device;
import starter.pages.DeviceManagerLoginPage;
import starter.pages.DeviceManagerMainPage;
import starter.starter.config.ConfigProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeviceSteps
{
    @Managed
    WebDriver driver;
    DeviceManagerLoginPage loginPage;
    DeviceManagerMainPage mainPage;
    Device lastAttemptedCreatedDevice;

    @Before
    public void setUp() throws IOException {
        String baseUrl = new ConfigProvider().getConfiguration("config.json").getBaseUrl();
        loginPage = new DeviceManagerLoginPage(driver, baseUrl);
        mainPage = new DeviceManagerMainPage(driver, baseUrl);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @Given("user logged in")
    public void userLogin() throws IOException {
        loginPage.logInUser();
    }

    @When("create device without name")
    public void createDeviceWithoutName() {
        lastAttemptedCreatedDevice = mainPage
                .createDevice(null, "Android", "RO", false, true);
    }

    @When("create device without OS type")
    public void createDeviceWithoutOSType() {
        String deviceName = DeviceManagerMainPage.generateDeviceName("JVTest#Date_2");
        lastAttemptedCreatedDevice = mainPage
                .createDevice(deviceName, null, "SK", false, true);
    }

    @When("create device without country code")
    public void createDeviceWithoutCountryCode() {
        String deviceName = DeviceManagerMainPage.generateDeviceName("JVTest#Date_3");
        lastAttemptedCreatedDevice = mainPage
                .createDevice(deviceName, "WindowsPhone", null, false, true);
    }

    @When("create device with non ISO country code")
    public void createDeviceWithInvalidCountryCode() {
        String deviceName = DeviceManagerMainPage.generateDeviceName("JVTest#Date_4");
        lastAttemptedCreatedDevice = mainPage
                .createDevice(deviceName, "iPhone", "1XYZ9", false, true);
    }

    @When("create device {string} with OS type {string} and country code {string}")
    public void createDevice(String deviceName, String osType, String countryCode) {
        deviceName = DeviceManagerMainPage.generateDeviceName(deviceName);
        lastAttemptedCreatedDevice = mainPage
                .createDevice(deviceName, osType, countryCode, true, false);
    }

    @Then("device exists")
    public void deviceExistsOnce() {
        mainPage.updateLoadedDeviceTable(true);
        mainPage.assertThatDeviceNameExistsInTableOnce(lastAttemptedCreatedDevice.getDeviceName());
        mainPage.assertThatDeviceExistsInTableOnce(lastAttemptedCreatedDevice);
    }

    @Then("device does not exist")
    public void deviceDoesNotExist() {
        mainPage.updateLoadedDeviceTable(false);
        mainPage.assertThatDeviceDoesNotExistInTable(lastAttemptedCreatedDevice);
    }

    @Then("device exist {int} times")
    public void deviceExistMultipleTimes(int times) {
        mainPage.updateLoadedDeviceTable(true);
        mainPage.assertThatDeviceExistInTable(lastAttemptedCreatedDevice, times);
    }

    @And("device name is {string}")
    public void deviceNameIs(String deviceName) {
        mainPage.assertThatDeviceNameIs(lastAttemptedCreatedDevice, deviceName);
    }
}
