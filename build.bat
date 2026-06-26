@echo off
setlocal

set JAVA_HOME=C:\Program Files\Java\jdk-19
set SRC=src\keypresssimulator
set BIN=bin
set LIB=lib\gson-2.10.1.jar
set JAR=AutomationTools.jar
set MAIN=keypresssimulator.KeystrokeSimulator

echo [1/4] Limpiando bin...
if exist %BIN% rmdir /s /q %BIN%
mkdir %BIN%

echo [2/4] Compilando...
"%JAVA_HOME%\bin\javac" --release 17 -encoding UTF-8 -cp "%LIB%" -d %BIN% %SRC%\*.java
if errorlevel 1 ( echo ERROR: fallo la compilacion & exit /b 1 )

echo [3/4] Extrayendo dependencias...
cd %BIN%
"%JAVA_HOME%\bin\jar" xf ..\%LIB%
cd ..

echo [4/4] Empaquetando JAR...
"%JAVA_HOME%\bin\jar" cfm %JAR% MANIFEST.MF -C %BIN% .
if errorlevel 1 ( echo ERROR: fallo la creacion del JAR & exit /b 1 )

echo.
echo Listo: %JAR%
echo Uso:   java -jar %JAR% cred 1134567890
