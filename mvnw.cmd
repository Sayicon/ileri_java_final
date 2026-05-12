@echo off
setlocal

set MAVEN_VERSION=3.9.9
set WRAPPER_DIST_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin
set MAVEN_HOME=%WRAPPER_DIST_DIR%\apache-maven-%MAVEN_VERSION%
set MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [mvnw] Maven %MAVEN_VERSION% bulunamadi, indiriliyor...
    if not exist "%WRAPPER_DIST_DIR%" mkdir "%WRAPPER_DIST_DIR%"
    powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%TEMP%\maven-wrapper.zip' -UseBasicParsing"
    powershell -Command "Expand-Archive -Path '%TEMP%\maven-wrapper.zip' -DestinationPath '%WRAPPER_DIST_DIR%' -Force"
    del "%TEMP%\maven-wrapper.zip"
    echo [mvnw] Maven %MAVEN_VERSION% hazir.
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
