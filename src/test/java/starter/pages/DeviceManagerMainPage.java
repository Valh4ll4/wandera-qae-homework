package starter.pages;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.annotations.findby.By;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import starter.devices.Device;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
public class DeviceManagerMainPage extends BasePage
{
    private static final String XPATH_TABLE_ROWS = "//table[contains(@class,'mdl-data-table')]/tbody/tr";
    private static final int AWAITILITY_TIMEOUT_IN_SECONDS = 3;
    private static final String FEATURE_RUN_ID = new SimpleDateFormat("yyMMddhhmmss").format(new Date());
    private List<Device> lastLoadedDeviceTable;

    private enum TableWaitingStrategy
    {
        POPULATED, SIZE_CHANGED, SIZE_UNCHANGED
    }

    public static String generateDeviceName(String name) {
        return name.replace("#Date", FEATURE_RUN_ID);
    }

    public DeviceManagerMainPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    private List<WebElement> getDeviceRecordTableElement() {
        return driver.findElements(By.xpath(XPATH_TABLE_ROWS));
    }

    private void clickAddDeviceButton() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        org.openqa.selenium.By addButtonById = By.id("btn-add-device");
        wait.until(ExpectedConditions.elementToBeClickable(addButtonById));
        driver.findElement(addButtonById).click();
    }

    private WebElement getDeviceCreationModalCloseButton() {
        return driver.findElement(By.id("btn-cancel-device"));
    }

    private void waitForDeviceCreationModalToClose(boolean expectClosure, boolean closeExplicitly) {
        try {
            Awaitility.given()
                    .await("Waiting for device creation modal to close.")
                    .atMost(Duration.ofSeconds(2))
                    .until(() -> !getDeviceCreationModalCloseButton().isDisplayed());
        } catch (ConditionTimeoutException cte) {
            // When expecting the closure we can even fail on the exception but this needs to be specified
            // in documentation and/or classified by product owner regarding severity. Alternatively this can
            // be in scope of usability testing.
            if (expectClosure) {log.warn("Modal window for creating device did not close as expected.");}
            if (closeExplicitly) {getDeviceCreationModalCloseButton().click();}
            return;
        }

        if (!expectClosure) {
            log.warn("Expected the modal window for creating device to stay open, but the window closed already.");
        }
    }

    private Device parseTableRowElement(WebElement rowElement) throws ParseException {
        long start = System.currentTimeMillis();
        String deviceName = rowElement.findElement(By.xpath("./td[2]")).getText();
        String countryCode = rowElement.findElement(By.xpath("./td[3]")).getText();
        String createdDateString = rowElement.findElement(By.xpath("./td[4]")).getText();
        String osType = rowElement.findElement(By.xpath("./td[5]")).getText();

        SimpleDateFormat createdDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        Date createdDate = createdDateFormatter.parse(createdDateString);
        long end = System.currentTimeMillis();
        log.debug("parseTableRowElement() took {} millis", end - start);

        return new Device(deviceName, osType, countryCode, createdDate);
    }

    private List<Device> parseDeviceTable() {
        List<WebElement> rowElements = getDeviceRecordTableElement();

        return rowElements.stream().map(element -> {
            try {
                Device parsedDevice = parseTableRowElement(element);
                log.debug("Parsed device: {}", parsedDevice);
                return parsedDevice;
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toList());
    }

    private void waitForDeviceTableSizeUpdate(TableWaitingStrategy waitStrategy) {
        Callable<Boolean> isDeviceTablePopulated = () -> getDeviceRecordTableElement().size() > 0;
        Callable<Boolean> hasDeviceTableSizeChanged =
                () -> lastLoadedDeviceTable.size() != getDeviceRecordTableElement().size();
        Callable<Boolean> waitingCondition;
        String message;
        String warning;

        if (waitStrategy == TableWaitingStrategy.POPULATED) {
            message = "Waiting for the device record table to be populated with at least 1 record.";
            warning = "Device record table was not populated. This may indicate problems with network.";
            waitingCondition = isDeviceTablePopulated;
        } else if (waitStrategy == TableWaitingStrategy.SIZE_CHANGED) {
            message = "Waiting for the device record table to change size.";
            warning = "Size of the device record table has not changed, it may indicate slow async load " +
                    "or concurrent access to application. Tests MAY FAIL!";
            waitingCondition = hasDeviceTableSizeChanged;
        } else {
            message = "Waiting for the device record table to change size (negative path).";
            warning = "Size of the device record table has changed unexpectedly.";
            // This condition should never be true. We are using Awaitility to detect potential changes if otherwise.
            waitingCondition = hasDeviceTableSizeChanged;
        }

        if (waitStrategy != TableWaitingStrategy.SIZE_UNCHANGED) {
            try {
                Awaitility.given()
                        .await(message)
                        .atMost(Duration.ofSeconds(AWAITILITY_TIMEOUT_IN_SECONDS))
                        .until(waitingCondition);
            } catch (ConditionTimeoutException cte) {
                log.warn(warning);
            }
        } else {
            // Negative usage of Awaitility (TableWaitingStrategy.SIZE_CHANGED).
            try {
                Awaitility.given()
                        .await(message)
                        .atMost(Duration.ofSeconds(AWAITILITY_TIMEOUT_IN_SECONDS))
                        .until(waitingCondition);
                // This is unhappy path since table size has changed.
                log.warn(warning);
            } catch (ConditionTimeoutException ignored) {
                // This is the happy path via negative usage since we want the condition to be unmet.
                log.debug("Size of device record table has not changed as expected.");
            }
        }
    }

    public void updateLoadedDeviceTable(boolean tableChanged) {
        Integer previousLoadedTableSize = null;
        if (lastLoadedDeviceTable != null) {
            previousLoadedTableSize = lastLoadedDeviceTable.size();
            log.info("Previously stored table contained {} records.", previousLoadedTableSize);
        } else {
            log.info("There was no stored table before this point.");
        }

        if (tableChanged) {
            if (previousLoadedTableSize == null || previousLoadedTableSize == 0) {
                waitForDeviceTableSizeUpdate(TableWaitingStrategy.POPULATED);
            } else {
                waitForDeviceTableSizeUpdate(TableWaitingStrategy.SIZE_CHANGED);
            }
        } else {
            waitForDeviceTableSizeUpdate(TableWaitingStrategy.SIZE_UNCHANGED);
        }
        lastLoadedDeviceTable = parseDeviceTable();
        int currentLoadedTableSize = lastLoadedDeviceTable.size();
        log.info("Actual stored table contains {} records.", currentLoadedTableSize);
    }

    private void updateLoadedDeviceTable() {
        updateLoadedDeviceTable(true);
    }

    public void assertThatDeviceNameExistsInTableOnce(String deviceName) {
        if (lastLoadedDeviceTable == null) {log.warn("Table was not parsed even once. Test will result in failure.");}
        List<Device> filteredList = lastLoadedDeviceTable.stream()
                .filter(deviceInTable -> deviceInTable.getDeviceName()
                        .equals(deviceName))
                .collect(Collectors.toList());
        Assertions.assertThat(filteredList)
                .as(String.format("Table (filtered with device name) does not contain " +
                        "exactly one device with given name '%s'.", deviceName))
                .hasSize(1);
    }

    public void assertThatDeviceExistsInTableOnce(Device device) {
        if (lastLoadedDeviceTable == null) {log.warn("Table was not parsed even once. Test will result in failure.");}
        Assertions.assertThat(lastLoadedDeviceTable)
                .as(String.format("Table does not contain the record (%s) it is supposed to.", device.toString()))
                .containsOnlyOnce(device);
    }

    public void assertThatDeviceDoesNotExistInTable(Device device) {
        if (lastLoadedDeviceTable == null) {log.warn("Table was not parsed even once. Test will result in failure.");}
        Assertions.assertThat(lastLoadedDeviceTable)
                .as(String.format("Table contains a (%s) record it should not be.", device.toString()))
                .doesNotContain(device);
    }

    public void assertThatDeviceExistInTable(Device device, int times) {
        if (lastLoadedDeviceTable == null) {log.warn("Table was not parsed even once. Test will result in failure.");}
        List<Device> filteredList = lastLoadedDeviceTable.stream()
                .filter(deviceInTable -> deviceInTable.equals(device))
                .collect(Collectors.toList());
        Assertions.assertThat(filteredList)
                .as(String.format("Device list does not contain the device (%s) %d times " +
                        "as expected.", device.toString(), times))
                .hasSize(times);
    }

    public void assertThatDeviceNameIs(Device device, String expectedNameFromScenario) {
        Assertions.assertThat(device.getDeviceName()).
                as("Device name was not stored as expected in the scenario.")
                .isEqualTo(generateDeviceName(expectedNameFromScenario));
    }

    private String nullToEmpty(String parameter) {
        return parameter == null ? "" : parameter;
    }

    public Device createDevice(@Nullable String deviceName,
                               @Nullable String osType,
                               @Nullable String countryCode,
                               boolean expectModalClosure,
                               boolean closeModalExplicitly) {
        Device lastAttemptedCreatedDevice = new Device(
                nullToEmpty(deviceName),
                nullToEmpty(osType),
                nullToEmpty(countryCode),
                DateUtils.truncate(new Date(), Calendar.DATE));
        log.info("Updating internal device records list before creating new device.");
        updateLoadedDeviceTable();
        clickAddDeviceButton();

        if (deviceName != null && !deviceName.isEmpty()) {
            driver.findElement(By.id("textfield-Devicename")).sendKeys(deviceName);
        }
        if (osType != null && !osType.isEmpty()) {
            WebElement osTypeSelectorElement = driver.findElement(By.id("mdl-selectfield-0"));
            String osTypeSelectItemXPath = String.format("//*[contains(@class, 'mdl-menu__item') " +
                    "and contains(text(), '%s')]", osType);
            osTypeSelectorElement.click();
            osTypeSelectorElement.findElement(By.xpath(osTypeSelectItemXPath)).click();
        }
        if (countryCode != null && !countryCode.isEmpty()) {
            driver.findElement(By.id("textfield-Countrycode")).sendKeys(countryCode);
        }
        driver.findElement(By.id("btn-save-device")).click();

        waitForDeviceCreationModalToClose(expectModalClosure, closeModalExplicitly);
        return lastAttemptedCreatedDevice;
    }
}
