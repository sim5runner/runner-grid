package testcase.access;

import org.testng.annotations.Test;

import runner.TestRunner;


public class Test_EXP13_AC13_01_01_02_T1 extends TestRunner {

	@Test (groups = {"Acceptance", "Mouse", "Primary"})
	public void EXP13_AC3_01_01_02_T1() throws Exception {
		
		System.out.println("in EXP13_AC3_01_01_02_T1");
		executeItem("EXP13.AC13.01.01.02.T1", "T1", "1", "1");
		executeItem("EXP13.AC13.01.01.02.T1", "T1", "2", "1");
		executeItem("EXP13.AC13.01.01.02.T1", "T1", "3", "1");
		Thread.sleep(3000);
		System.out.println("DONE.");
		
	}
}
