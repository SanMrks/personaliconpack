@echo off
setlocal
echo ==========================================
echo    Building Ravenshade Icon Pack APK
echo ==========================================

set "ROOT=%~dp0"
set "TOOLS=%ROOT%tools"
set "OUT=%ROOT%output"
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "JAVA=%JAVA_HOME%\bin\java.exe"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAR=%JAVA_HOME%\bin\jar.exe"
set "PYTHON=C:\Users\sanil\AppData\Local\Programs\Python\Python312\python.exe"

if not exist "%PYTHON%" (
    set "PYTHON=python"
)

if not exist "%JAVA%" (
    set "JAVA=java"
    set "JAVAC=javac"
    set "JAR=jar"
)

echo [1/7] Syncing icons, wallpapers, and appfilter...
xcopy /Y /Q "%ROOT%icons\*.png" "%ROOT%app\src\main\res\drawable-nodpi\" >nul 2>&1
xcopy /Y /Q "%ROOT%app\src\main\res\xml\*.*" "%ROOT%app\src\main\assets\" >nul 2>&1
if not exist "%ROOT%app\src\main\assets\wallpapers" mkdir "%ROOT%app\src\main\assets\wallpapers"
xcopy /Y /Q "%ROOT%wallpapers\*.*" "%ROOT%app\src\main\assets\wallpapers\" >nul 2>&1
if exist "%ROOT%google_drive_links.txt" (
    copy /Y "%ROOT%google_drive_links.txt" "%ROOT%app\src\main\assets\google_drive_links.txt" >nul 2>&1
)

echo [2/7] Compiling Java code to DEX...
if not exist "%TOOLS%\bin" mkdir "%TOOLS%\bin"
del /Q "%TOOLS%\bin\com\ravenshade\iconpack\*.class" >nul 2>&1
dir /s /b "%ROOT%app\src\main\java\com\ravenshade\iconpack\*.java" > "%TOOLS%\sources.txt"
"%JAVAC%" -encoding UTF-8 -cp "%TOOLS%\android.jar" -d "%TOOLS%\bin" @"%TOOLS%\sources.txt" --release 8
set JAVAC_ERR=%ERRORLEVEL%
del "%TOOLS%\sources.txt" >nul 2>&1
if %JAVAC_ERR% neq 0 (
    echo [ERROR] Failed to compile Java code.
    pause
    exit /b 1
)

powershell -NoProfile -Command "& '%JAVA%' -cp '%TOOLS%\d8.jar' com.android.tools.r8.D8 --min-api 26 --output '%TOOLS%\bin' (Get-ChildItem -Recurse '%TOOLS%\bin\com\*.class')"
if errorlevel 1 (
    echo [ERROR] Failed to generate DEX bytecode.
    pause
    exit /b 1
)

echo [3/7] Compiling and linking Android resources (uncompressed ARSC)...
if not exist "%OUT%" mkdir "%OUT%"
"%TOOLS%\aapt2.exe" compile --dir "%ROOT%app\src\main\res" -o "%TOOLS%\compiled_res.zip"
if errorlevel 1 (
    echo [ERROR] Failed to compile resources.
    pause
    exit /b 1
)

"%TOOLS%\aapt2.exe" link -I "%TOOLS%\android.jar" --manifest "%ROOT%app\src\main\AndroidManifest.xml" -A "%ROOT%app\src\main\assets" -0 arsc -o "%TOOLS%\app-unsigned.apk" "%TOOLS%\compiled_res.zip" --auto-add-overlay --min-sdk-version 26 --target-sdk-version 34 --version-code 29 --version-name "5.0"
if errorlevel 1 (
    echo [ERROR] Failed to link APK.
    pause
    exit /b 1
)

echo [4/7] Adding classes.dex to APK...
"%JAR%" -uf "%TOOLS%\app-unsigned.apk" -C "%TOOLS%\bin" classes.dex
if errorlevel 1 (
    echo [ERROR] Failed to add classes.dex to APK.
    pause
    exit /b 1
)

echo [5/7] Normalizing zip path separators for Android...
"%PYTHON%" "%TOOLS%\fix_slashes.py" "%TOOLS%\app-unsigned.apk"
if errorlevel 1 (
    echo [ERROR] Failed to normalize zip slashes.
    pause
    exit /b 1
)

echo [6/7] 4-byte ZipAligning APK...
"%TOOLS%\zipalign.exe" -f -p 4 "%TOOLS%\app-unsigned.apk" "%TOOLS%\app-aligned.apk"
if errorlevel 1 (
    echo [ERROR] Failed to zipalign APK.
    pause
    exit /b 1
)

echo [7/7] Signing APK with Android signature scheme (v1, v2, v3)...
"%JAVA%" -jar "%TOOLS%\uber-apk-signer.jar" -a "%TOOLS%\app-aligned.apk" -o "%OUT%" --skipZipAlign --allowResign
if errorlevel 1 (
    echo [ERROR] Failed to sign APK.
    pause
    exit /b 1
)

copy /Y "%OUT%\app-aligned-debugSigned.apk" "%OUT%\MyIconPack.apk" >nul 2>&1
del "%TOOLS%\compiled_res.zip" >nul 2>&1
del "%TOOLS%\app-unsigned.apk" >nul 2>&1
del "%TOOLS%\app-aligned.apk" >nul 2>&1

echo.
echo ==========================================
echo  SUCCESS! Raven Shade Liquid Glass Customization Suite v5.0 is ready:
echo  %OUT%\MyIconPack.apk
echo ==========================================
echo.
