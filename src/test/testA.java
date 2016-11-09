package test;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

/**
 * Created by luongle on 11/5/16.
 */
public class testA {
    @Test
    public void tc1() {
        init.wait = new WebDriverWait(init.driver, 30);
        init.test = init.extent.createTest("Test Case 1");
        init.wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mservice:id/main_content")));
        init.driver.findElement(By.id("com.mservice:id/button_continue_text")).click();
        init.test.pass("test case 1");
    }
}
