package utils;

public class SimsConstants {
	public static final String TASK_INSTRUCTION_TEXT_ID = "InstructionText";
	public static final String INSTRUCTION_TEXT_SPAN_XPATH = "//*[@id='InstructionText']/span";
	public static final String RIBBON_ELEMENTS_FILEPATH = "src/test/resources/component-class-hierarchy.txt";
	
	public static final String TASK_XML_BASE_PATH = "src/test/resources/taskXML/";
	public static final String CONFIG_PROPERTIES_BASE_PATH = "config/";
	public static final String SIMS_BROWSER_EXE_PATH = "src/test/resources/drivers/simbrowserfocus.exe";
	
	 public static final String TASKID_ATTRIBUTE_NAME = "taskId";
	 public static final String SCREENSHOT_ATTRIBUTE_NAME = "actionScreenShot";
	 public static final String PRACTICE_ATTRIBUTE_NAME = "practice";
	 public static final String MEMEORY_USAGE_TRACKING_PARAM_NAME = "memoryUsageTracking";
	 public static final String MODE_PARAM = "mode";
	 
	 public static final String PRACTICE_CLOSE_XPATH = "//*[@id='closePractice']";

	 
	 public static final String DEFAULT_MEMORY_BROWSER = "chrome";//chrome/firefox
	 
	 public enum MEMEORYTRACKCONFIG {
		 stag_username_value("PerfStudent"), 
		 stag_password_value("password1"),
		 stag_myCourses_value("Chaos Master SIM5 Course"),
		 stag_launch_ppt_assignment("CMP_TEST_PPT_100"),
		 stag_launch_access_assignment("CMP_TEST_ACCESS_100"),
		 stag_launch_word_assignment("CMP_TEST_WORD_100"),
		 stag_launch_excel_assignment("CMP_TEST_EXCEL_100"),
		 prod_username_value("PerfStudent"), 
		 prod_password_value("password1"),
		 prod_myCourses_value("Chaos Master SIM5 Course"),
		 prod_launch_ppt_assignment("CMP_TEST_PPT_100"),
		 prod_launch_word_assignment("CMP_TEST_WORD_100"),
		 prod_launch_excel_assignment("CMP_TEST_EXCEL_100"),
		 prod_launch_access_assignment("CMP_TEST_ACCESS_100"),
		 ;		 	
	    String value;

	    private MEMEORYTRACKCONFIG(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }

    }

}
