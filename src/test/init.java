package test;

import Login.AndroidDevice;
import Login.CollectingLogOutputStream;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.exec.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by chip on 11/4/16.
 */
public class init {

    //boolean isDone = false;
    public static ExtentHtmlReporter htmlReporter;
    public static ExtentReports extent;
    public static ExtentTest test;
    public static AndroidDriver driver;
    public static WebDriverWait wait;
    public static WebElement element;
    private boolean isConfigurated = false;

    @BeforeTest
    public void Cau_hinh() throws Exception {
//        final JFrame frame = new JFrame("Android Settings");
//        JPanel panel = new JPanel(new MigLayout());
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JTextField APKpath = new JTextField(30);
//        APKpath.setText(Files.readAllLines(Paths.get("src/test/appium_caps.txt")).get(0));
//
//        final JButton browseAPK = new JButton("Choose");
//        browseAPK.addActionListener((e) -> {
//            final JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("APK Files", "apk", "APK");
//            fc.setFileFilter(filter);
//            int returnVal = fc.showOpenDialog(fc);
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//                APKpath.setText(file.getPath());
//            }
//        });
//
//        List<AndroidDevice> devices = loadDevices();
//        String[] options = new String[devices.size()];
//        for (int i = 0; i < options.length; i++) {
//            options[i] = devices.get(i).getName();
//        }
//
//        JComboBox comboBox = new JComboBox(options);
//
//        final JButton clickRun = new JButton("Set");
//        clickRun.addActionListener((e) -> {
//            try {
//                File appium_caps = new File("src/test/appium_caps.txt");
//                PrintWriter writer = new PrintWriter(appium_caps);
//                String deviceName = String.valueOf(comboBox.getSelectedItem());
//
//                for (AndroidDevice d : devices) {
//                    if (d.getName() == deviceName) {
//                        writer.println(APKpath.getText());
//                        writer.println(d.getVersion());
//                        writer.println(d.getName());
//                        writer.close();
//                        isDone = true;
//                        frame.dispose();
//                        break;
//                    }
//                }
//            } catch (FileNotFoundException e1) {
//                e1.printStackTrace();
//            }
//        });
//
//        JButton clickRefresh = new JButton("Refresh");
//        clickRefresh.addActionListener((e) -> {
//            try {
//                List<AndroidDevice> newDevices = loadDevices();
//                devices.clear();
//                comboBox.removeAllItems();
//                for (AndroidDevice d : newDevices) {
//                    comboBox.addItem(d.getName());
//                    devices.add(d);
//                }
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        });
//
//
//
//        panel.add(APKpath);
//        panel.add(browseAPK, "wrap");
//        panel.add(comboBox);
//        panel.add(clickRefresh, "wrap");
//        panel.add(clickRun, "span, align center, w 100!");
//
//        frame.setContentPane(panel);
//        frame.pack();
//        frame.setVisible(true);
//
//        while (!isDone) {
//            Thread.sleep(100);
//        }

        try {
            startAppium();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("automationName", "Appium");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("platformVersion", Files.readAllLines(Paths.get("src/test/appium_caps.txt")).get(1));
            capabilities.setCapability("deviceName", Files.readAllLines(Paths.get("src/test/appium_caps.txt")).get(2));
            capabilities.setCapability("app", Files.readAllLines(Paths.get("src/test/appium_caps.txt")).get(0));
            capabilities.setCapability("appPackage", "com.mservice");
            capabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, "true");

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!isConfigurated) Thread.sleep(100);

        htmlReporter = new ExtentHtmlReporter("screenshots/report.html");
        htmlReporter.loadXMLConfig(new File("src/test/extent-config.xml"));
        htmlReporter.config().setChartVisibilityOnOpen(false);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    private List<AndroidDevice> loadDevices() throws IOException, InterruptedException {
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

    @AfterTest
    public static void stopTest() throws Exception {
        extent.flush();
        extent.close();
        driver.quit();
        stopAppium();
    }

    private void startAppium() throws Exception {
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
                        isConfigurated = true;
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

}
