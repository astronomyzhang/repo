#! /bin/bash

# 使用maven打成jar包，并且替换掉需要混淆的文件
readonly proguardPath=/com/siemens/dasheng/web
readonly originName=ofm-da-core-1.0-SNAPSHOT.jar
readonly proguardName=classes-pg.jar
readonly targetPackageName=ofm-da-core-1.0-SNAPSHOT.tar
pomPath=$(
  cd "$(dirname "$0")"
  pwd
)
echo "the script run path at ${pomPath}"

remove_directory(){
   if [[ -d $1 ]]; then
      rm -rf $1
   fi
}

remove_file(){
  if [[ -f $1 ]]; then
     rm -rf $1
  fi
}

remove_files(){
  if [[ -d $1 ]]; then
     rm -rf $1/*
  fi
}

cd ${pomPath} && mvn package -DskipTests
# 分别解压jar包
remove_directory ${pomPath}/pg
remove_directory ${pomPath}/origin

mkdir pg origin
cd ${pomPath}/pg && ${JAVA_HOME}/bin/jar xvf ${pomPath}/target/${proguardName} >/dev/null
cd ${pomPath}/origin && ${JAVA_HOME}/bin/jar xvf ${pomPath}/target/app/lib/${originName} >/dev/null
remove_file ${pomPath}/target/${proguardName}
remove_file ${pomPath}/target/app/lib/${originName}

# 替换混淆的文件
remove_files ${pomPath}/origin${proguardPath}
cp -rf ${pomPath}/pg${proguardPath}/* ${pomPath}/origin${proguardPath}/
cd ${pomPath}/origin && ${JAVA_HOME}/bin/jar cvfM0 ../target/app/lib/${originName} ./* >/dev/null

#替换 bin/Application $CLASSPATH,APOLLO
sed -i 's!CLASSPATH=\"$BASEDIR\"/conf:\"$REPO\"\/\*!CLASSPATH=\"$BASEDIR\"/conf:\"$REPO\"\/\*\:$CLASSPATH!g' ${pomPath}/target/app/bin/Application
sed -i 's!-Dapp.name="Application" \\!-Dapp.name="Application" $JAVA_OPTS_APOLLO \\!g' ${pomPath}/target/app/bin/Application
remove_file ${pomPath}/target/app/lib/pi-linux-driver-1.0.0.jar

sed -i 's#UNIT_TEST#DEPLOY#g' ${pomPath}/target/app/conf/application.yml

# 生成tar包
cd ${pomPath}/target && tar zcf ${targetPackageName} app
