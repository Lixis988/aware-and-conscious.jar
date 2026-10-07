cmake_minimum_required(VERSION 3.15)
project(meat_liminal LANGUAGES CXX)

if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release CACHE STRING "Build type" FORCE)
endif()

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

if(NOT DEFINED CMAKE_TOOLCHAIN_FILE)
    set(CMAKE_TOOLCHAIN_FILE "C:/Users/Lev/vcpkg/scripts/buildsystems/vcpkg.cmake" CACHE FILEPATH "vcpkg toolchain file")
endif()

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_RELEASE ${CMAKE_BINARY_DIR}/bin/Release)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_DEBUG ${CMAKE_BINARY_DIR}/bin/Debug)

find_package(OpenGL REQUIRED)
find_package(glfw3 CONFIG REQUIRED)
find_package(GLEW CONFIG REQUIRED)

add_executable(meat_liminal
    src/main_liminal.cpp
    src/shader.cpp
    src/texture.cpp
    src/camera.cpp
    src/liminal.cpp
    src/bunny.cpp
    src/eye.cpp
    src/physics.cpp
    src/audio.cpp
    src/stb_impl.cpp
)

target_include_directories(meat_liminal PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/src)
target_link_libraries(meat_liminal PRIVATE OpenGL::GL glfw GLEW::GLEW)

if(MSVC)
    target_compile_options(meat_liminal PRIVATE /W4 /permissive-)
    add_definitions(/D_CRT_SECURE_NO_WARNINGS)
else()
    target_compile_options(meat_liminal PRIVATE -Wall -Wextra -Wpedantic)
endif()
