@echo off
setlocal
cd /d "%~dp0"

set "VCPKG_TOOLCHAIN=C:\Users\Lev\vcpkg\scripts\buildsystems\vcpkg.cmake"
set "GENERATOR=Visual Studio 17 2022"
set "CONFIG=Release"

if exist "build\CMakeCache.txt" (
    findstr /C:"CMAKE_HOME_DIRECTORY" "build\CMakeCache.txt" | findstr /I /C:"%CD%" >nul
    if errorlevel 1 (
        echo Stale CMake cache detected, cleaning build\ ...
        rmdir /s /q build
    )
)

echo [1/2] Configuring (%GENERATOR%, %CONFIG%) ...
cmake -S . -B build -G "%GENERATOR%" -A x64 -DCMAKE_TOOLCHAIN_FILE="%VCPKG_TOOLCHAIN%" -DCMAKE_BUILD_TYPE=%CONFIG%
if errorlevel 1 (
    echo Configure failed, retrying with a clean build\ ...
    rmdir /s /q build 2>nul
    cmake -S . -B build -G "%GENERATOR%" -A x64 -DCMAKE_TOOLCHAIN_FILE="%VCPKG_TOOLCHAIN%" -DCMAKE_BUILD_TYPE=%CONFIG%
    if errorlevel 1 (
        echo Configure failed.
        exit /b 1
    )
)

echo [2/2] Building ...
cmake --build build --config %CONFIG% --parallel
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

echo.
echo Build OK: build\bin\%CONFIG%\meat_liminal.exe
exit /b 0
