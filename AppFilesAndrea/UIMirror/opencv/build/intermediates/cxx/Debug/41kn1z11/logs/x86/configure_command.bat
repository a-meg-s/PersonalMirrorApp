@echo off
"C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Andrea\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\build\\intermediates\\cxx\\Debug\\41kn1z11\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\build\\intermediates\\cxx\\Debug\\41kn1z11\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Andrea\\OneDrive - Hochschule Luzern\\Desktop\\IIP-Mirror\\try2\\AppFilesAndrea\\UIMirror\\opencv\\.cxx\\Debug\\41kn1z11\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"