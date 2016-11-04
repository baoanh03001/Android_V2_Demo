package test;

import Login.AndroidDevice;
import net.miginfocom.swing.MigLayout;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by chip on 11/4/16.
 */
public class init {

    boolean isDone = false;

    @BeforeTest
    public void Cau_hinh() throws Exception {
        final JFrame frame = new JFrame("Android Settings");
        JPanel panel = new JPanel(new MigLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField APKpath = new JTextField(30);
        APKpath.setText(Files.readAllLines(Paths.get("src/test/appium_caps.txt")).get(0));

        final JButton browseAPK = new JButton("Choose");
        browseAPK.addActionListener((e) -> {
            final JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("APK Files", "apk", "APK");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(fc);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                APKpath.setText(file.getPath());
            }
        });

        List<AndroidDevice> devices = loadDevices();
        String[] options = new String[devices.size()];
        for (int i = 0; i < options.length; i++) {
            options[i] = devices.get(i).getName();
        }

        JComboBox comboBox = new JComboBox(options);

        final JButton clickRun = new JButton("Set");
        clickRun.addActionListener((e) -> {
            try {
                File appium_caps = new File("src/test/appium_caps.txt");
                PrintWriter writer = new PrintWriter(appium_caps);
                String deviceName = String.valueOf(comboBox.getSelectedItem());

                for (AndroidDevice d : devices) {
                    if (d.getName() == deviceName) {
                        writer.println(APKpath.getText());
                        writer.println(d.getVersion());
                        writer.println(d.getName());
                        writer.close();
                        isDone = true;
                        break;
                    }
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        JButton clickRefresh = new JButton("Refresh");
        clickRefresh.addActionListener((e) -> {
            try {
                List<AndroidDevice> newDevices = loadDevices();
                devices.clear();
                comboBox.removeAllItems();
                for (AndroidDevice d : newDevices) {
                    comboBox.addItem(d.getName());
                    devices.add(d);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });



        panel.add(APKpath);
        panel.add(browseAPK, "wrap");
        panel.add(comboBox);
        panel.add(clickRefresh, "wrap");
        panel.add(clickRun, "span, align center, w 100!");

        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
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

    @Test
    public void a() throws Exception {
        while (!isDone) {
            Thread.sleep(100);
        }
    }

}
