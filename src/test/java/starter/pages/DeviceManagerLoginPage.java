package starter.pages;

import net.serenitybdd.core.annotations.findby.By;
import org.openqa.selenium.WebDriver;
import starter.users.credendials.Credentials;
import starter.users.credendials.CredentialsProvider;
import starter.users.credendials.CredentialsProviderFileImpl;

import java.io.IOException;

public class DeviceManagerLoginPage extends BasePage
{
    public DeviceManagerLoginPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public void logInUser() throws IOException {
        CredentialsProvider provider = new CredentialsProviderFileImpl();
        Credentials credentials = provider.getCredentials("secret.json");
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        driver.navigate().to(baseUrl);
        driver.findElement(By.id("textfield-Username")).sendKeys(username);
        driver.findElement(By.id("textfield-Password")).sendKeys(password);
        driver.findElement(By.id("btn-login")).click();
        // We could possibly look for elements here to make sure site is properly loaded if we deem to
        // e.g. driver.findElement(By.id("btn-logout")) or other element / several elements.;
    }
}
