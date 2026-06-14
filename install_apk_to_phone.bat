@echo off
setlocal

cd /d "%~dp0"

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\install_apk_to_phone.ps1" -UseLastPort

echo.
pause
