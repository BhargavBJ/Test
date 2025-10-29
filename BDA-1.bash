pwd
hdfs dfs  -ls / 
hdfs dfs  -ls /user 
hdfs dfs -mkdir /user/root/demodir 
hdfs dfs -ls /user/root/ 
cd Desktop
hdfs  dfs  -copyFromLocal  temp.txt /user/root/demodir 
hdfs dfs -ls /user/root/demodir 
hdfs dfs -cat /user/root/demodir/temp.txt 
