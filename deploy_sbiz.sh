HOST_1=18.140.197.136
HOST_2=18.141.216.52

PRJ_HOME=$(pwd)
DIR_DATA="$PRJ_HOME/data"
DIR_RELEASE="$PRJ_HOME/build/libs"
JAR_NAME=heartbeat-1.0-SNAPSHOT.jar
RM_BASE_1=/home/centos/heartbeat_1/libs
RM_BASE_2=/home/centos/heartbeat_2/libs
RM_BASE_3=/home/centos/heartbeat_3/libs

PEM_KEY=blacknano888sea.pem

#rsync [option] local_dir remote_dir
#rsync -rave "ssh -i ~/$PEM_KEY" "$DIR_DATA/" centos@$HOST_1:"$RM_BASE_1/data"
#rsync -rave "ssh -i ~/$PEM_KEY" "$DIR_DATA/" centos@$HOST_1:"$RM_BASE_2/data"
rsync -rave "ssh -i ~/$PEM_KEY" "$DIR_DATA/" centos@$HOST_2:"$RM_BASE_3/data"

#echo deploy jars to HOST "Adam $HOST_1"
#chmod 770 "$DIR_RELEASE/$JAR_NAME"
#scp -C -i ~/$PEM_KEY -r "$DIR_RELEASE/$JAR_NAME" centos@$HOST_1:"$RM_BASE_1/$JAR_NAME"
#ssh -i ~/$PEM_KEY centos@$HOST_1 "cd $RM_BASE_1 && /bin/bash ./HBServer.sh restart"

#echo deploy jars to HOST "Eva $HOST_1"
#chmod 770 "$DIR_RELEASE/$JAR_NAME"
#scp -C -i ~/$PEM_KEY -r "$DIR_RELEASE/$JAR_NAME" centos@$HOST_1:"$RM_BASE_2/$JAR_NAME"
#ssh -i ~/$PEM_KEY centos@$HOST_1 "cd $RM_BASE_2 && /bin/bash ./HBServer.sh restart"

echo deploy jars to HOST "Lambda $HOST_2"
chmod 770 "$DIR_RELEASE/$JAR_NAME"
scp -C -i ~/$PEM_KEY -r "$DIR_RELEASE/$JAR_NAME" centos@$HOST_2:"$RM_BASE_3/$JAR_NAME"
ssh -i ~/$PEM_KEY centos@$HOST_2 "cd $RM_BASE_3 && /bin/bash ./HBServer.sh restart"