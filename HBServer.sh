javaArgs="-d64 -server -Xms512m -Xmx1g -Xmn150m -XX:GCTimeRatio=2 -XX:ParallelGCThreads=10 -XX:+UseParNewGC -XX:MaxGCPauseMillis=50 -XX:+DisableExplicitGC"
serviceCmd=`readlink -f $0`
serviceDir="${serviceCmd%/*}"
serviceFile="${serviceCmd##*/}"
serviceLogFile="/$serviceDir/$serviceFile.log"
jarName="heartbeat-1.0-SNAPSHOT.jar"
javaClasspath="$serviceDir/$jarName"
serverIP=127.0.0.1
serverPort=8080
javaCommandLine="java $javaArgs -jar $javaClasspath $serverIP $serverPort"
processPattern="$serverIP $serverPort"

function startService {
   echo -n "Starting $jarName[$serverIP:$serverPort]... "
   pid=`ps aux | grep -v grep | grep "$processPattern" | awk '{print $2}'`
   if [ $pid ];  then
      echo "already started!!! PID=$pid"
   else
      cmd="nohup $javaCommandLine >$serviceLogFile 2>&1 &"
      $SHELL -c "$cmd" || return 1
      sleep 0.1
	   pid=`ps aux | grep -v grep | grep "$processPattern" | awk '{print $2}'`
	   if [ $pid ];  then
         echo "[OK] PID=$pid"
         return 1
      else
	     echo -ne "[failed], see logfile: $serviceLogFile\n"
      fi
   fi
   return 0; }

function stopService {
   echo -n "Stopping $jarName[$serverIP:$serverPort]... "
   pid=`ps aux | grep -v grep | grep "$processPattern" | awk '{print $2}'`
   if [ $pid ];  then
    kill -15 $pid
	echo "[OK]"
   else
    echo "not running!!!"
   fi
   return 0; }

function checkServiceStatus {
   echo -n "Checking for $jarName[$serverIP:$serverPort]:   "
   pid=`ps aux | grep -v grep | grep "$processPattern" | awk '{print $2}'`
   if [ $pid ];  then
	echo "running PID=$pid"
   else
	echo "stopped"
   fi
   return 0; }

function main {
   RETVAL=0
   case "$1" in
      start)
         startService
         ;;
      stop)
         stopService
         ;;
      restart)
         stopService
		 sleep 4
		 startService
         ;;
      status)
         checkServiceStatus
         ;;
      *)
         echo "Usage: $0 {start|stop|restart|status}"
         exit 1
         ;;
      esac
   exit $RETVAL
}

main $1
