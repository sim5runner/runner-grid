package testcase.ppt;
import org.testng.annotations.Test;
import runner.TestRunner;
public class Test_SKL16_PPT_01_01_03_T1 extends TestRunner {
    @Test(groups = {
        "Acceptance",
        "Primary"
    }) public void SKL16_PPT_01_01_03_T11() throws Exception {
        System.out.println("START..");
        executeItem("SKL16.PPT.01.01.03.T1", "T1", "1", "1");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }
}