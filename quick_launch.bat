@echo off
echo ========================================
echo    EventJar Mod - Quick Launch
echo ========================================
echo.

echo [INFO] Быстрый запуск клиента (без пересборки)...
echo [INFO] Если мод не работает, используйте launch_client.bat для полной пересборки
echo.

REM Запускаем клиент напрямую с оптимизированными параметрами
call gradlew runClient -Dorg.gradle.jvmargs="-Xmx6G -Xms2G -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/sun.util.calendar=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.java2d=ALL-UNNAMED -Djava.awt.headless=false"

echo.
echo [INFO] Клиент завершил работу
echo.
pause
