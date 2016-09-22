# runner grid

Steps for connecting a Node to Grid & Running Tests:

1. Download selenium jar (current support: selenium-server-standalone-2.41.0.jar), chromedriver.exe & keep in a common directory. eg \jar
2. Download browser-connect.bat file (download link on server dashboard right side menu)
3. Run batch file.
4. Enter your username. - > your chrome browser node is connected now
5. Launch grid console to check connected browser. (Navigate from right side menu)
6. Run test with matching configurations as of registered browser node.
7. Logs will be displayed on dashboard & test will run on client machine browser.

Examples:
For Chrome:
java -jar selenium-server-standalone-2.41.0.jar -role webdriver -hub http://localhost:4444/grid/register -browser browserName="chrome",version=ANY,platform=WINDOWS,maxInstances=5,applicationName=abhi_chrome -Dwebdriver.chrome.driver=chromedriver.exe -port 6666

For Firefox:
java -jar selenium-server-standalone-2.41.0.jar -role webdriver -browser "browserName=firefox,version=ANY,platform =WINDOWS",maxInstances=5,applicationName=abhi_firefox -port 5555 -hub http://192.168.1.200:4444/grid/register;
