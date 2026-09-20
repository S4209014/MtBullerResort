@echo off
if not exist out mkdir out
javac -encoding UTF-8 -cp "lib\sqlite-jdbc-3.47.1.0.jar" -d out src\com\resort\*.java
if errorlevel 1 (
  echo Compile failed. Make sure a JDK is installed and javac is on PATH.
  exit /b 1
)
java -cp "out;lib\sqlite-jdbc-3.47.1.0.jar" com.resort.MtBullerAdmin
