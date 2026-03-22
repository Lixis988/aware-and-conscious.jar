@echo off
echo ========================================
echo    EventJar Mod - Developer Launch
echo ========================================
echo.

echo [INFO] Запуск в режиме разработчика с отладочной информацией...
echo.

REM Очистка и сборка
echo [INFO] Очистка и пересборка мода...
call gradlew clean build

if %errorlevel% neq 0 (
    echo [ERROR] Ошибка сборки!
    pause
    exit /b 1
)

echo.
echo [INFO] Запуск клиента с отладочными параметрами...
echo [DEBUG] Включены логи отладки и профилирование
echo.

REM Запуск с отладочными параметрами
call gradlew runClient -Dorg.gradle.jvmargs="-Xmx8G -Xms3G -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/sun.util.calendar=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.java2d=ALL-UNNAMED -Djava.awt.headless=false -Dforge.logging.console.level=DEBUG -Dforge.logging.markers=REGISTRIES,LOADING" --debug

echo.
echo [INFO] Клиент завершил работу
echo [DEBUG] Логи GC сохранены в gc.log
echo.
pause
