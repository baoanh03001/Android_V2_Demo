package test;

import org.testng.annotations.Test;

/**
 * Created by luongle on 11/5/16.
 */
public class testB {
    @Test
    public void tc1() {
        init.test = init.extent.createTest("Test Case 2");

        init.test.pass("12312312312");
    }
}
