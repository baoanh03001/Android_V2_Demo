package Login;

/**
 * Created by luongle on 10/28/16.
 */
public class AndroidDevice {
    private String name = "", version = "", ip = "";
    public void setIP(String ip) {
        this.ip = ip;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getIP() {
        return ip;
    }
    public String getName() {
        return name;
    }
    public String getVersion() {
        return version;
    }
}
