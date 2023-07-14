REM https://github.com/thachln/sakai/releases/download/22.2.1/sakai22.2.u1_bin.tar.xz
docker build --build-arg release=22.2 -t xlms/sakai:22.3 -f ./Dockerfile.binary .