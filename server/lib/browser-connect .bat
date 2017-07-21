@echo off
echo enter username:
set /p username=""
echo connecting browser node to grid..
java -jar selenium-server-standalone-2.44.0.jar -role webdriver -hub http://34.209.150.28:4444/grid//register -browser browserName="chrome",version=ANY,platform=WINDOWS,maxInstances=5,applicationName=%username% -Dwebdriver.chrome.driver=chromedriver.exe -port 6666