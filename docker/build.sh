#!/bin/bash
harborAddress=$1
harborNamespace=$2 
appName=$3
version=$4
scriptPath=$(cd "$(dirname "$0")";pwd)
dbPath=$(dirname $scriptPath)
mkdir -p $scriptPath/app
#cd $dbPath && mvn package -DskipTests
yes|cp -r $dbPath/target/app/*  $scriptPath/app/

if [ $appName == "ofm-da-core" ]; then
    #替换 bin/dbweb||dbwrite $CLASSPATH ,APOLLO
#    sed -i 's!CLASSPATH=\"$BASEDIR\"/conf:\"$REPO\"\/\*!CLASSPATH=\"$BASEDIR\"/conf:\"$REPO\"\/\*\:$CLASSPATH!g' $scriptPath/app/bin/Application
#    sed -i 's!-Dapp.name="Application" \\!-Dapp.name="Application" $JAVA_OPTS_APOLLO \\!g' $scriptPath/app/bin/Application
#    #lib /pi-linux-driver-1.0.0.jar 删除
#    rm -rf $scriptPath/app/lib/pi-linux-driver-1.0.0.jar
    chmod +x $scriptPath/entrypoint.sh
    cd ${scriptPath} && docker build -t  ${harborAddress}/${harborNamespace}/${appName}:${version} .
else
    echo "app name $appName not exist!"
    exit 1
fi