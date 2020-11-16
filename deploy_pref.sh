HOST=13.212.159.107
PRJ_HOME=$(pwd)
DIR_RELEASE="$PRJ_HOME/build/libs"
JAR_NAME=heartbeat-1.0-SNAPSHOT.jar
RM_BASE=/home/centos/sbiz_ref/libs
PEM_KEY=blacknano888sea.pem

echo deploy jars to HOST $HOST
chmod 770 "$DIR_RELEASE/$JAR_NAME"
scp -C -i ~/$PEM_KEY -r "$DIR_RELEASE/$JAR_NAME" centos@$HOST:"$RM_BASE/$JAR_NAME"
ssh -i ~/$PEM_KEY centos@$HOST "cd $RM_BASE && sudo /bin/bash ./HBPref.sh restart"