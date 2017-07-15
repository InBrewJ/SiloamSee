#!/bin/bash

# Set up the android build environment variables

/bin/bash /root/setup_env.bash

if [ $# -ne 1 ]; then
    echo "build_apks.bash android_install_prefix   # Example: build_apks.bash /opt/android"
    exit 1
fi

prefix=$1

# don't need to get rtabmap in this script

# tango neither

# resource tool should already be available

# rtabmap (just build the 64 bit version, don't bother with armeabi-v7a)
cd
mkdir rtabmap-tango/build/arm64-v8a
cd rtabmap-tango/build/arm64-v8a
cmake -DCMAKE_TOOLCHAIN_FILE=../../cmake_modules/android.toolchain.cmake -DANDROID_ABI=arm64-v8a -DBUILD_SHARED_LIBS=OFF -DBUILD_EXAMPLES=OFF -DBUILD_TOOLS=OFF -DCMAKE_BUILD_TYPE=Release -DOpenCV_DIR=$prefix/arm64-v8a/sdk/native/jni -DCMAKE_INSTALL_PREFIX=$prefix/arm64-v8a ../..
make

# package with binaries of both architectures
cp -r ../armeabi-v7a/app/android/libs/armeabi-v7a app/android/libs/.
make
