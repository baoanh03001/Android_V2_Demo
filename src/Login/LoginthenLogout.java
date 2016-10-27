package Login;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.exec.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luongle on 10/27/16.
 */

public class LoginthenLogout {
    public static void main(String[] args) throws Exception {

        try {
            startAppium();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DesiredCapabilities capabilities=new DesiredCapabilities();

        capabilities.setCapability("automationName", "Appium");

        capabilities.setCapability("platformName", "Android");

        capabilities.setCapability("platformVersion","5.1");

        capabilities.setCapability("deviceName","Samsung Galaxy S6 - 5.1.0 - API 22 - 1440x2560");

        capabilities.setCapability("app","/Users/luongle/Downloads/EU_v3464_17.7.apk");

        capabilities.setCapability("appPackage","com.mservice");

        CharSequence sdt = "01231231236";
        String pass = "111111";
        String OTP = "";

        AndroidDriver driver=new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement element = driver.findElement(By.id("com.mservice:id/main_content"));
        wait.until(ExpectedConditions.visibilityOf(element));
        Dimension size = driver.manage().window().getSize();
        int startx = (int) (size.width * 0.99);
        int endx = (int) (size.width * 0.001);
        int starty = size.height / 2;
        driver.swipe(startx, starty, endx, starty, 150);
        Thread.sleep(2000);
        driver.swipe(endx, starty, startx, starty, 150);
        Thread.sleep(2000);
        driver.findElement(By.id("com.mservice:id/button_continue_text")).click();
        element = driver.findElement(By.id("com.mservice:id/tv_phone_number"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        //driver.findElement(By.id("com.mservice:id/tv_phone_number")).sendKeys(sdt);
        for (int i: convert2KeyCode(sdt)) {
            driver.pressKeyCode(i);
        }
        driver.findElement(By.id("com.mservice:id/button_continue_text")).click();
        driver.findElement(By.id("com.mservice:id/btnConfirm")).click(); //btnCancel
        while (OTP == "") {
            OTP = getOTP(sdt.toString());
        }
        driver.findElement(By.id("com.mservice:id/et_sms_code_enter")).sendKeys(OTP);
        driver.findElement(By.id("com.mservice:id/button_confirm_sms_code_text")).click();
        driver.findElement(By.id("com.mservice:id/txtPass1")).sendKeys(pass);
        driver.findElement(By.id("com.mservice:id/button_confirm_text")).click();
        driver.findElement(By.xpath("//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.FrameLayout[1]/android.support.v4.view.ViewPager[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.view.View[1]/android.view.View[4]/android.widget.ImageView[1]")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='ĐỒNG Ý']")).click();
        driver.navigate().back();
        driver.quit();
        stopAppium();

//        //driver.findElement(By.xpath("android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.EditText[1]"));
//        driver.findElementByAndroidUIAutomator("UiSelector().className(\"android.widget.EditText\")").sendKeys("123");

    }

    private static void startAppium() throws Exception {

        CommandLine command = new CommandLine(
                "/Applications/Appium.app/Contents/Resources/node/bin/node");
        command.addArgument(
                "/Applications/Appium.app/Contents/Resources/node_modules/appium/build/lib/main.js",
                false);
        command.addArgument("--address", false);
        command.addArgument("127.0.0.1");
        command.addArgument("--port", false);
        command.addArgument("4723");
        command.addArgument("--no-reset", false);
        command.addArgument("--dont-stop-app-on-reset", false);
        ExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        CollectingLogOutputStream stdout = new CollectingLogOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        boolean isAppiumStarted = false;
        executor.setExitValue(1);
        try {
            executor.setStreamHandler(psh);
            executor.execute(command, resultHandler);
            while (!isAppiumStarted) {
                Thread.sleep(100);
                for (String line:stdout.getLines()) {
                    if (line.contains("Appium REST http interface listener started")) {
                        isAppiumStarted = true;
                    }
                }
            }
            System.out.println("Appium server started.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopAppium() throws Exception{

        Runtime.getRuntime().exec("killall node");

    }

    private static String getOTP(String sdt) throws Exception{
        System.setProperty("webdriver.chrome.driver", "/Users/luongle/Downloads/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("http://172.16.43.132:9091/#/streams/MoMo%20Auth%20Code");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement element = driver.findElement(By.className("stream-lines"));
        wait.until(ExpectedConditions.visibilityOf(element));
        String[] lines = element.getText().split(System.getProperty("line.separator"));
        for (String a: lines) {
            if (a.contains(sdt)) {
                String pattern = "(?<=,)(.*)(?=\\))";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(a);
                if (m.find()) {
                    driver.close();
                    return m.group(0);
                }
            }
        }
        driver.close();
        return "";
    }

    private static int[] convert2KeyCode(CharSequence txt) {

        int[] txtKeyCode = new int[txt.length()];
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            KeyStroke ks = KeyStroke.getKeyStroke(c);
            txtKeyCode[i] = ks.getKeyCode();
            System.out.println(ks.getKeyCode());
        }

        return txtKeyCode;
    }
}
