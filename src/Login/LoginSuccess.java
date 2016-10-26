package Login;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by anhnguyen on 8/6/16.
 */
public class LoginSuccess {

        AndroidDriver wd;

        String fileName = "EU_v3506_17.7.apk";
        File momoAppV2 = new File("src/main/resources/" + fileName);
        String platForm = "Android";
        String platVersion = "5.1";
        String devicename = "AndroidTest";
        String appPack = "com.mservice";
        String appActi = "";

        @BeforeTest
        public void beforeTest() throws MalformedURLException {
            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability("app", momoAppV2);
            cap.setCapability("platformName", platForm);
            cap.setCapability("platformVersion", platVersion);
            cap.setCapability("deviceName", devicename);
            cap.setCapability("appPackage",appPack);

            wd = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        }

        @Test(priority = 1)
        public void LoginSuccess() {
            List<WebElement> li = wd.findElementsByClassName("android.widget.TextView");
            li.get(0).click();

        }



        @AfterTest
        public void afterTest() {
//        iOSDriver.quit();

        }

}
