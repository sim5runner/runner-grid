package testcase.word;
import org.testng.annotations.Test;
import runner.TestRunner;
public class Test_GO16_WD_04_4A_01_T1 extends TestRunner {@Test(groups = {
        "Primary"
    }) public void GO16_WD_04_4A_01_T1_1() throws Exception {
        System.out.println("START..");
        executeItem("GO16.WD.04.4A.01.T1", "T1", "1", "1");
        executeItem("GO16.WD.04.4A.01.T1", "T1", "2", "1");
        executeItem("GO16.WD.04.4A.01.T1", "T1", "3", "1");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }
}