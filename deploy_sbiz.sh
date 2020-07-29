HOST=18.140.197.136
PRJ_HOME=$(pwd)
DIR_DATA="$PRJ_HOME/data"
DIR_RELEASE="$PRJ_HOME/build/libs"
JAR_NAME=heartbeat-1.0-SNAPSHOT.jar
RM_BASE=/home/centos/heartbeat/libs
PEM_KEY=blacknano888sea.pem

#rsync [option] local_dir remote_dir
rsync -rave "ssh -i ~/$PEM_KEY" "$DIR_DATA/" centos@$HOST:"$RM_BASE/data"

echo deploy jars to HOST $HOST
chmod 770 "$DIR_RELEASE/$JAR_NAME"
scp -C -i ~/$PEM_KEY -r "$DIR_RELEASE/$JAR_NAME" centos@$HOST:"$RM_BASE/$JAR_NAME"
ssh -i ~/$PEM_KEY centos@$HOST "cd $RM_BASE && /bin/bash ./HBServer.sh restart"