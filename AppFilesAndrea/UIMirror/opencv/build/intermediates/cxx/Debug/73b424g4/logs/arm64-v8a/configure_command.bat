@echo off
"C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=arm64-v8a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=arm64-v8a" ^
  "-DANDROID_NDK=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\build\\intermediates\\cxx\\Debug\\73b424g4\\obj\\arm64-v8a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\build\\intermediates\\cxx\\Debug\\73b424g4\\obj\\arm64-v8a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\.cxx\\Debug\\73b424g4\\arm64-v8a" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
