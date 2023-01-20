package starter.pages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.WebDriver;

@AllArgsConstructor
public abstract class BasePage
{
    @Getter
    WebDriver driver;
    @Getter
    String baseUrl;
}
