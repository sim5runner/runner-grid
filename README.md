# runner grid

Steps for connecting a Node to Grid:

1. Download selenium jar (current support: selenium-server-standalone-2.41.0.jar), chromedriver.exe & keep in a common directory. eg \jar
2. Open terminal & move to directory \jar
3. Run command to connect browser node.

Examples:
For Chrome:
java -jar selenium-server-standalone-2.41.0.jar -role webdriver -hub http://localhost:4444/grid/register -browser browserName="chrome",version=ANY,platform=WINDOWS,maxInstances=5,applicationName=abhi_chrome -Dwebdriver.chrome.driver=chromedriver.exe -port 6666

For Firefox:
java -jar selenium-server-standalone-2.41.0.jar -role webdriver -browser "browserName=firefox,version=ANY,platform =WINDOWS",maxInstances=5,applicationName=abhi_firefox -port 5555 -hub http://192.168.1.200:4444/grid/register;
