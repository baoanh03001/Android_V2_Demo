package Login;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ThreadGuard;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luongle on 10/27/16.
 */

public class LoginthenLogout implements Runnable {

    @Override public synchronized void run() {
        try {
            startAppium();
//            int i = 0;
//            while (true) {
//                Thread.sleep(500);
//                System.out.println(i++);
//            }
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("automationName", "Appium");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("platformVersion", device.getVersion());
            capabilities.setCapability("deviceName", device.getName());
            //capabilities.setCapability("deviceName", "Chip's S7");
            //capabilities.setCapability("app","/Users/luongle/Downloads/EU_v3464_17.7.apk");
            capabilities.setCapability("app", APKpath.getText());
            //capabilities.setCapability("app", System.getProperty("user.home") + "/Downloads/EU_v3464_17.7.apk");
            capabilities.setCapability("appPackage", "com.mservice");
            capabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, "true");

            String sdt = "01231231236";
            String pass = "111111";
            String OTP = "";

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 30);
            element = driver.findElement(By.id("com.mservice:id/main_content"));
            takeScreenShot();
            wait.until(ExpectedConditions.visibilityOf(element));
//        swipeScreen("right", 1);
//        Thread.sleep(2000);
//        swipeScreen("left", 1);
//        Thread.sleep(2000);
            driver.findElement(By.id("com.mservice:id/button_continue_text")).click();
            typingText(sdt, "com.mservice:id/tv_phone_number");
            driver.findElement(By.id("com.mservice:id/button_continue_text")).click();
            driver.findElement(By.id("com.mservice:id/btnConfirm")).click(); //btnCancel
            while (OTP == "") {
                OTP = getOTP(sdt);
            }
            typingText(OTP, "com.mservice:id/et_sms_code_enter");
            takeScreenShot();
            driver.findElement(By.id("com.mservice:id/button_confirm_sms_code_text")).click();
            typingText(pass, "com.mservice:id/txtPass1");
            driver.findElement(By.id("com.mservice:id/button_confirm_text")).click();
            //driver.findElement(By.xpath("//android.widget.ImageView[3]")).click();
            element = driver.findElement(By.id("com.mservice:id/root"));
            wait.until(ExpectedConditions.visibilityOf(element));
            driver.findElement(MobileBy.xpath("//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.FrameLayout[1]/android.support.v4.view.ViewPager[1]/android.widget.FrameLayout[1]/android.view.ViewGroup[1]/android.view.ViewGroup[1]/android.view.ViewGroup[4]/android.widget.ImageView[1]")).click();
            takeScreenShot();
            driver.findElement(MobileBy.xpath("//android.widget.TextView[@text='ĐỒNG Ý']")).click();
            driver.navigate().back();
            driver.navigate().back();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AndroidDriver driver;
    private static WebDriverWait wait;
    private static WebElement element;
    private static JTextField APKpath;
    private static boolean isConfigurated = false;
    private static AndroidDevice device;
    private static Thread t;

    public static void main(String[] args) throws Exception {

        setAndroidsettings();

        while (true) {
            if (!isConfigurated) Thread.sleep(100);
            else {
                LoginthenLogout l = new LoginthenLogout();
                t = new Thread(l);
                t.start();
                isConfigurated = false;
            }
        }
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
        stdout.close();
    }

    private static void stopAppium() throws Exception {
        Runtime.getRuntime().exec("killall node");
    }

    private static String getOTP(String sdt) throws Exception{
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.home") + "/Downloads/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("http://172.16.43.132:9091/#/streams/MoMo%20Auth%20Code");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement element = driver.findElement(By.className("stream-lines"));
        wait.until(ExpectedConditions.textToBePresentInElement(element, sdt));
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

    private static void typingText(String txt, String eid) {

        int[] txtKeyCode = new int[txt.length()];
        for (int i = 0; i < txt.length(); i++) {
            String k = "KEYCODE_" + txt.charAt(i);
            int keyEvent = 0;
            try {
                Field f = AndroidKeyCode.class.getField(k);
                keyEvent = f.getInt(null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            txtKeyCode[i] = keyEvent;
        }

        driver.findElement(By.id(eid)).click();
        element = driver.findElement(By.id(eid));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        for (int i: txtKeyCode) {
            driver.pressKeyCode(i);
        }
    }

    private static void swipeScreen(String direction, int times) throws InterruptedException{
        Dimension size = driver.manage().window().getSize();

        switch (direction) {
            case "left":
                for (int i = 0; i < times; i++) {
                    driver.swipe((int)(size.width*0.001), size.height/2, (int)(size.width*0.99), size.height/2, 150);
                }
            case "right":
                for (int i = 0; i < times; i++) {
                    driver.swipe((int)(size.width*0.99), size.height/2, (int)(size.width*0.001), size.height/2, 150);
                }
            case "up":
                for (int i = 0; i < times; i++) {
                    driver.swipe(size.width/2, (int)(size.height*0.99), size.width/2, (int)(size.height*0.001), 150);
                }
            case "down":
                for (int i = 0; i < times; i++) {
                    driver.swipe(size.width/2, (int)(size.height*0.001), size.width/2, (int)(size.height*0.99), 150);
                }
        }
    }

    private static void takeScreenShot() {
        // Set folder name to store screenshots.
        String destDir = "screenshots";
        // Capture screenshot.
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Set date format to set It as screenshot file name.
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
        // Create folder under project with name "screenshots" provided to destDir.
        new File(destDir).mkdirs();
        // Set file name using current date time.
        String destFile = dateFormat.format(new Date()) + ".png";

        try {
            // Copy paste file at destination folder location
            FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setAndroidsettings() throws IOException, InterruptedException {
        final JFrame frame = new JFrame("Android Settings");
        JPanel panel = new JPanel(new MigLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        APKpath = new JTextField(30);
        APKpath.setText(new String(Files.readAllBytes(Paths.get("src/Login/apkpath.txt"))));

        final JButton browseAPK = new JButton("Choose");
        browseAPK.addActionListener((e) -> {
            final JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("APK Files", "apk", "APK");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(fc);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                APKpath.setText(file.getPath());
                try {
                    Files.write(Paths.get("src/Login/apkpath.txt"), file.getPath().getBytes());
                } catch (IOException ex) {

                }
            } else {

            }
        });

        List<AndroidDevice> devices = loadDevices();
        String[] options = new String[devices.size()];
        for (int i = 0; i < options.length; i++) {
            options[i] = devices.get(i).getName();
        }

        JComboBox comboBox = new JComboBox(options);
        comboBox.addActionListener((e) -> {

        });



        final JButton clickRun = new JButton("Run!");
        clickRun.addActionListener((e) -> {
            String deviceName = String.valueOf(comboBox.getSelectedItem());
            for (AndroidDevice d: devices) {
                if (d.getName() == deviceName) {
                    device = d;
                    break;
                }
            }
            isConfigurated = true;
        });

        JButton clickRefresh = new JButton("Refresh");
        clickRefresh.addActionListener((e) -> {
            try {
                List<AndroidDevice> newDevices = loadDevices();
                devices.clear();
                comboBox.removeAllItems();
                for (AndroidDevice d: newDevices) {
                    comboBox.addItem(d.getName());
                    devices.add(d);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JButton clickStop = new JButton("Stop");
        clickStop.addActionListener((e) -> {
            try {
                t.stop();
                driver.quit();
                stopAppium();
                JOptionPane.showMessageDialog(null, "Appium server stopped.");
                System.out.println("Appium server stopped.");
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "The Appium server is not started yet.", "Appium warning", JOptionPane.WARNING_MESSAGE);
                System.out.println("The Appium server is not started yet.");
            }
        });


        panel.add(APKpath);
        panel.add(browseAPK, "wrap");
        panel.add(comboBox);
        panel.add(clickRefresh, "wrap");
        panel.add(clickRun, "span, align center, w 100!, split 2, gapright 60");
        panel.add(clickStop, "align center, w 100!");

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static List<AndroidDevice> loadDevices() throws IOException, InterruptedException {
        String line;
        String cmd = "adb devices";

        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);

        pr.waitFor();
        List<AndroidDevice> devices = new ArrayList();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = buf.readLine())!=null) {
            if (line.contains("\t")) {
                AndroidDevice device = new AndroidDevice();
                device.setIP(line.substring(0, line.indexOf("\t")).trim());
                devices.add(device);
            }
        }

        for (AndroidDevice d: devices) {
            try {
                cmd = "adb -s " + d.getIP() + " shell getprop ro.build.version.release";
                pr = run.exec(cmd);
                buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                d.setVersion(buf.readLine().trim());
                cmd = "adb -s " + d.getIP() + " shell getprop ro.product.model";
                pr = run.exec(cmd);
                buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                d.setName(buf.readLine().trim());
            } catch (Exception e) {
                System.out.println("Connected devices are offline. Please reconnect the devices.");
            }



        }

        buf.close();

        return devices;
    }

}

