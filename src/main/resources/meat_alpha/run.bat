@echo off
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if not exist "%JAVA_EXE%" set JAVA_EXE=java
"%JAVA_EXE%" -Xmx1G -jar "%~dp0meat-alpha.jar"
