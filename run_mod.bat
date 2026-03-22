@echo off
echo Starting EventJar mod with proper JVM arguments...

REM Переходим в папку run
cd run

REM Запускаем мод с необходимыми JVM аргументами
java --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/java.nio=ALL-UNNAMED ^
     --add-opens java.base/sun.nio.ch=ALL-UNNAMED ^
     --add-opens java.base/java.util=ALL-UNNAMED ^
     --add-opens java.base/java.util.concurrent=ALL-UNNAMED ^
     --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED ^
     --add-opens java.base/sun.util.calendar=ALL-UNNAMED ^
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED ^
     --add-opens java.base/java.text=ALL-UNNAMED ^
     --add-opens java.desktop/java.awt=ALL-UNNAMED ^
     --add-opens java.desktop/java.awt.font=ALL-UNNAMED ^
     --add-opens java.desktop/sun.awt=ALL-UNNAMED ^
     --add-opens java.desktop/sun.java2d=ALL-UNNAMED ^
     -Djava.awt.headless=false ^
     -Xmx4G ^
     -Xms1G ^
     -XX:+UseG1GC ^
     -jar ..\forge-1.20.1-47.2.0.jar ^
     --username Player ^
     --version 1.20.1 ^
     --gameDir . ^
     --assetsDir ..\assets ^
     --assetIndex 1.20.1 ^
     --uuid 00000000-0000-0000-0000-000000000000 ^
     --accessToken 0 ^
     --userType legacy ^
     --versionType release

pause
