#---------------------------install Hadoop utilities------------------------------------------


#download the file
cookbook_file "/home/hadoop/apache-cassandra-1.2.15-bin.tar.gz" do
    source "apache-cassandra-1.2.15-bin.tar.gz"
    mode "0666"
    owner "hadoop"
    group "hadoop"
end

#unzip it
execute "unzip_cassandra" do
    command "cd /home/hadoop && tar -xf apache-cassandra-1.2.15-bin.tar.gz"
    creates "/home/hadoop/apache-cassandra-1.2.15"
    user "hadoop"
    group "hadoop"
end

#clean up
execute "cleanup_hive" do
   command "mv /home/hadoop/apache-cassandra-1.2.15-bin.tar.gz /home/hadoop/apache-cassandra-1.2.15/" 
   creates "/home/hadoop/apache-cassandra-1.2.15/apache-cassandra-1.2.15-bin.tar.gz"
end

directory "/home/hadoop/apache-cassandra-1.2.15/logs" do
  owner "hadoop"
  group "hadoop"
  mode "0755"
  action :create
end

directory "/home/hadoop/apache-cassandra-1.2.15/data" do
  owner "hadoop"
  group "hadoop"
  mode "0755"
  action :create
end

directory "/home/hadoop/apache-cassandra-1.2.15/data/commitlog" do
  owner "hadoop"
  group "hadoop"
  mode "0755"
  action :create
end

directory "/home/hadoop/apache-cassandra-1.2.15/data/saved_caches" do
  owner "hadoop"
  group "hadoop"
  mode "0755"
  action :create
end


cookbook_file "/home/hadoop/apache-cassandra-1.2.15/conf/log4j-server.properties" do
    source "log4j-server.properties"
    mode "0664"
    user "hadoop"
    group "hadoop"
end

cookbook_file "/home/hadoop/apache-cassandra-1.2.15/conf/cassandra.yaml" do
    source "cassandra.yaml"
    mode "0664"
    user "hadoop"
    group "hadoop"
end

# start all the services
execute "/home/hadoop/apache-cassandra-1.2.15/bin/cassandra" do
  user "hadoop"
  group "hadoop"
end


