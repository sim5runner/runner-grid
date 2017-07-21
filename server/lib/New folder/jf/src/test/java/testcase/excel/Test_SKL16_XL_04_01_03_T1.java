package testcase.excel;    import org.testng.annotations.Test;    import runner.TestRunner;    public class Test_SKL16_XL_04_01_03_T1 extends TestRunner {    
    @Test (groups = {"Primary"})public void SKL16_XL_04_01_03_T1_1() throws Exception {            System.out.println("START..");            executeItem("SKL16.XL.04.01.03.T1", "T1", "1","1");Thread.sleep(3000);            System.out.println("DONE.");        }   

    @Test (groups = {"Primary"})public void SKL16_XL_04_01_03_T1_2() throws Exception {            System.out.println("START..");            executeItem("SKL16.XL.04.01.03.T1", "T1", "1","4");Thread.sleep(3000);            System.out.println("DONE.");        }   
 }