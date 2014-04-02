#!/bin/bash
HADOOP_PREFIX=/home/hadoop/hadoop-1.2.1
HIVE_HOME=/home/hadoop/hive-0.12.0
#CASSANDRA_HOME=/home/hadoop/apache-cassandra-1.2.15
MY_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
 
#echo -e '1\x01foo' > /tmp/a.txt
#echo -e '2\x01bar' >> /tmp/a.txt
 
HADOOP_CORE=`ls $HADOOP_PREFIX/hadoop-core-*.jar`
CLASSPATH=$MY_DIR/Hcache.jar:$MY_DIR/../conf/:$HADOOP_CORE:$HIVE_HOME/conf

for i in ${HIVE_HOME}/lib/*.jar ; do
    CLASSPATH=$CLASSPATH:$i
done

#for i in ${CASSANDRA_HOME}/lib/*.jar ; do
#    CLASSPATH=$CLASSPATH:$i
#done
 
java -cp $CLASSPATH honeycache.cli.HCacheMain "$@"
