package testcase.word;
import org.testng.annotations.Test;
import runner.TestRunner;
public class Test_EXP16_WD_01_01_01_A1 extends TestRunner {@Test(groups = {
        "Primary"
    }) public void EXP16_WD_01_01_01_A1_1() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "1");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "1");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "1");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Keyboard"
    }) public void EXP16_WD_01_01_01_A1_2() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "2");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "2");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "2");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon"
    }) public void EXP16_WD_01_01_01_A1_3() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "3");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "3");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "3");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Right - Click"
    }) public void EXP16_WD_01_01_01_A1_4() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "4");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "4");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "4");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Right - Click"
    }) public void EXP16_WD_01_01_01_A1_5() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "5");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "1");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "5");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Right - Click"
    }) public void EXP16_WD_01_01_01_A1_6() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "1", "6");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "2", "1");
        executeItem("EXP16.WD.01.01.01.A1", "A1", "3", "6");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }
}