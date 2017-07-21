package testcase.excel;
import org.testng.annotations.Test;
import runner.TestRunner;
public class Test_EXP16_XL_03_02_02_T1 extends TestRunner {@Test(groups = {
        "Primary"
    }) public void EXP16_XL_03_02_02_T1_1() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "1");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "1");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "1");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon"
    }) public void EXP16_XL_03_02_02_T1_2() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "2");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "2");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "2");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon", "Keyboard"
    }) public void EXP16_XL_03_02_02_T1_3() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "3");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "3");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "3");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon", "Keyboard"
    }) public void EXP16_XL_03_02_02_T1_4() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "4");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "4");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "4");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon", "Keyboard"
    }) public void EXP16_XL_03_02_02_T1_5() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "5");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "5");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "5");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Keyboard", "Toolbar"
    }) public void EXP16_XL_03_02_02_T1_6() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "4");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "6");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "6");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }@Test(groups = {
        "Ribbon", "Right - Click"
    }) public void EXP16_XL_03_02_02_T1_7() throws Exception {
        System.out.println("START..");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "1", "2");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "2", "7");
        executeItem("EXP16.XL.03.02.02.T1", "T1", "3", "7");
        Thread.sleep(3000);
        System.out.println("DONE.");
    }
}