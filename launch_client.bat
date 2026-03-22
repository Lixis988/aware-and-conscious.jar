@echo off
echo ========================================
echo    EventJar Mod - Client Launcher
echo ========================================
echo.

REM Проверяем наличие Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java не найдена! Убедитесь, что Java установлена и добавлена в PATH
    pause
    exit /b 1
)

echo [INFO] Java найдена, версия:
java -version

echo.
echo [INFO] Очистка предыдущей сборки...
call gradlew clean

echo.
echo [INFO] Сборка мода...
call gradlew build

if %errorlevel% neq 0 (
    echo [ERROR] Ошибка сборки! Проверьте логи выше
    pause
    exit /b 1
)

echo.
echo [INFO] Запуск клиента Minecraft с EventJar модом...
echo [INFO] JVM параметры оптимизированы для производительности
echo.

REM Запускаем клиент с оптимизированными JVM аргументами
call gradlew runClient -Dorg.gradle.jvmargs="-Xmx6G -Xms2G -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+UseCompressedClassPointers --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/sun.util.calendar=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.java2d=ALL-UNNAMED -Djava.awt.headless=false"

echo.
echo [INFO] Клиент завершил работу
echo.
pause
