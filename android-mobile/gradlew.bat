@echo off
setlocal

set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk

set GRADLE_VERSION=8.11.1
set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set GRADLE_DISTS=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin

for /d %%d in ("%GRADLE_DISTS%\*") do (
    if exist "%%d\gradle-%GRADLE_VERSION%\bin\gradle.bat" (
        "%%d\gradle-%GRADLE_VERSION%\bin\gradle.bat" %*
        goto :eof
    )
)

echo [gradlew] Gradle %GRADLE_VERSION% bulunamadi.
exit /b 1
