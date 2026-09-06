@echo off
setlocal
cd /d "%~dp0"
where javaw.exe >nul 2>nul
if errorlevel 1 (
  echo DesktopPet requires JDK 25 or newer. Set JAVA_HOME or add Java to PATH.
  pause
  exit /b 1
)
start "DesktopPet" javaw.exe -jar DesktopPet.jar
