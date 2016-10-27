package Login;

import org.apache.commons.exec.LogOutputStream;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luongle on 10/27/16.
 */
public class CollectingLogOutputStream extends LogOutputStream {
    private final List<String> lines = new LinkedList<String>();
    @Override protected void processLine(String line, int level) {
        lines.add(line);
    }
    public List<String> getLines() {
        return lines;
    }
}