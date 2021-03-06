package Login;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luongle on 10/31/16.
 */
public class testNapTienVaoVi implements Runnable {

    private static AndroidDriver driver;
    private static WebDriverWait wait;
    private static WebElement element;
    private static JTextField APKpath;
    private static boolean isConfigurated = false;
    private static AndroidDevice device;
    private static Thread t;
    private static testNapTienVaoVi l;
    private static ExtentHtmlReporter htmlReporter;
    private static ExtentReports extent;
    private static ExtentTest test;
    private static final String pass = "111111";

    //Khởi chạy và cấu hình cho Appium
    @Override
    public synchronized void run() {
        try {
            startAppium();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("automationName", "Appium");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("platformVersion", device.getVersion());
            capabilities.setCapability("deviceName", device.getName());
            capabilities.setCapability("app", APKpath.getText());
            capabilities.setCapability("appPackage", "com.mservice");
            capabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, "true");

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    public static void chuan_bi_moi_truong_test() throws Exception {

        //Tạo win form để chọn APK và máy test
        setAndroidsettings();

        while (!isConfigurated) {
            Thread.sleep(100);
        }

        l = new testNapTienVaoVi();
        t = new Thread(l);
        t.start();
        t.join();

        //Cấu hình cho report
        htmlReporter = new ExtentHtmlReporter("screenshots/report.html");
        htmlReporter.loadXMLConfig(new File(".idea/extent-config.xml"));
        htmlReporter.config().setChartVisibilityOnOpen(false);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    //@Test
    public static void testCase1() throws Exception {
        test = extent.createTest("Test Case 1");
        wait = new WebDriverWait(driver, 30);
        try {
            //click Nạp tiền vào ví
            stepClick("//android.widget.TextView[@text='NẠP TIỀN\nVÀO VÍ']", "Click Vào ví của tôi");

            //nhập số tiền
            element = driver.findElement(By.xpath("//android.widget.EditText[@text='Nhập số tiền']"));
            typingText("10000", element);
            test.addScreenCaptureFromPath(takeScreenShot());
            test.log(Status.PASS, "Nhập số tiền");

            //click Nạp tiền
            stepClick("//android.widget.TextView[@text='Nạp tiền']", "Click nạp tiền");

            //click Xác nhận
            stepClick("//android.widget.TextView[@text='Xác nhận']", "Click Xác nhận nạp tiền");

            //nhập mật khẩu
            typingPasscode(pass);
            test.addScreenCaptureFromPath(takeScreenShot());
            test.log(Status.PASS, "Nhập mật khẩu");

            //Xác nhận
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='Số dư trong ví']")));
            if (driver.findElement(By.xpath("//android.widget.TextView[@text='Giao dịch thành công!']")).isDisplayed())
                test.pass("Giao dịch thành công");
            else test.fail("Giao dịch thất bại");
            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        } catch (Exception e) {
            test.error(shortenExceptionMessage(e.getMessage()));
            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        }
    }

    //@Test
    public static void testCase2() throws Exception {
        test = extent.createTest("Test Case 2");
        wait = new WebDriverWait(driver, 30);
        try {
            //click Nạp tiền vào ví
            stepClick("//android.widget.TextView[@text='NẠP TIỀN\nVÀO VÍ']", "Click Vào ví của tôi");

            //nhập số tiền
            element = driver.findElement(By.xpath("//android.widget.EditText[@text='Nhập số tiền']"));
            typingText("1000", element);
            test.addScreenCaptureFromPath(takeScreenShot());
            test.log(Status.PASS, "Nhập số tiền");

            //click Nạp tiền
            stepClick("//android.widget.TextView[@text='Nạp tiền']", "Click nạp tiền");

            if (driver.findElement(By.xpath("//android.widget.TextView[contains(@text, 'Số tiền tối thiểu là ')]")).isDisplayed())
                test.fail("Sai giới hạn min");
            else test.pass("Đúng giới hạn min");

            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        } catch (Exception e) {
            test.error(shortenExceptionMessage(e.getMessage()));
            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        }
    }

    @Test
    public static void testCase3() throws Exception {
        test = extent.createTest("Test Case 3");
        wait = new WebDriverWait(driver, 30);
        try {
            //click Nạp tiền vào ví
            stepClick("//android.widget.TextView[@text='NẠP TIỀN\nVÀO VÍ']", "Click Vào ví của tôi");

            //nhập số tiền
            element = driver.findElement(By.xpath("//android.widget.EditText[@text='Nhập số tiền']"));
            typingText("10000", element);
            test.addScreenCaptureFromPath(takeScreenShot());
            test.log(Status.PASS, "Nhập số tiền");

            //click Nạp tiền
            stepClick("//android.widget.TextView[@text='Nạp tiề']", "Click nạp tiền");

            //click Xác nhận
            stepClick("//android.widget.TextView[@text='Xác nhận']", "Click Xác nhận nạp tiền");

            //nhập mật khẩu
            typingPasscode(pass);
            test.addScreenCaptureFromPath(takeScreenShot());
            test.log(Status.PASS, "Nhập mật khẩu");

            //Xác nhận
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='Số dư trong ví']")));
            if (driver.findElement(By.xpath("//android.widget.TextView[@text='Giao dịch thành công!']")).isDisplayed())
                test.pass("Giao dịch thành công");
            else test.fail("Giao dịch thất bại");
            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        } catch (Exception e) {
            test.error(shortenExceptionMessage(e.getMessage()));
            test.addScreenCaptureFromPath(takeScreenShot());
            driver.navigate().back();
        }
    }

    @AfterTest
    public static void stopTest() throws Exception {
        extent.flush();
        extent.close();
        driver.quit();
        stopAppium();
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
        command.addArgument("--log");
        command.addArgument(System.getProperty("user.home") + "/appiumLogs.txt");
        ExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        CollectingLogOutputStream stdout = new CollectingLogOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        boolean isAppiumStarted = false;
        executor.setExitValue(1);
        try {
            executor.setStreamHandler(psh);
            executor.execute(command, resultHandler);
            System.out.println("Appium server is starting...");
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

    private static void typingText(String txt, WebElement ele) {

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

        wait.until(ExpectedConditions.elementToBeClickable(ele));
        element.click();

        for (int i: txtKeyCode) {
            driver.pressKeyCode(i);
        }
    }

    private static void typingPasscode(String txt) {

        if (txt != null || txt.length() > 0) {
            for (char c: txt.toCharArray()) {
                driver.findElement(By.xpath("//android.widget.TextView[@text='" + c + "']")).click();
            }
        }

    }

    private static String takeScreenShot() {
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

        return destFile;
        //return "file://" + System.getProperty("user.home") + "/Android_V2_Demo/" + destDir + "/" + destFile;
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
//                Thread currentT = Thread.currentThread();
//                currentT.interrupt();
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

    private static String shortenExceptionMessage(String mes) {
        String a = mes.substring(0, mes.indexOf(".") + 1);
        a += "<br>";
        a += mes.substring(mes.indexOf("***") + 3);
        return a;
    }

    private static void stepClick(String xpath, String logMessage) throws Exception {
        driver.findElement(By.xpath(xpath)).click();
        test.addScreenCaptureFromPath(takeScreenShot());
        test.pass(logMessage);
    }

}
