@echo off
"C:\\Users\\nicog\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\nicog\\AndroidStudioProjects\\IIP-Repository\\spiegelapp-blanktemplate\\AppFilesNico\\OpenCV\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86_64" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86_64" ^
  "-DANDROID_NDK=C:\\Users\\nicog\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\nicog\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\nicog\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\nicog\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\nicog\\AndroidStudioProjects\\IIP-Repository\\spiegelapp-blanktemplate\\AppFilesNico\\OpenCV\\build\\intermediates\\cxx\\Debug\\5n312735\\obj\\x86_64" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\nicog\\AndroidStudioProjects\\IIP-Repository\\spiegelapp-blanktemplate\\AppFilesNico\\OpenCV\\build\\intermediates\\cxx\\Debug\\5n312735\\obj\\x86_64" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\nicog\\AndroidStudioProjects\\IIP-Repository\\spiegelapp-blanktemplate\\AppFilesNico\\OpenCV\\.cxx\\Debug\\5n312735\\x86_64" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
