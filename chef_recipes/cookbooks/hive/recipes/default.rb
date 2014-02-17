#---------------------------install Hadoop utilities------------------------------------------

package "mysql-server" do
    action :install
end

service "mysql-server" do
    action [:start, :enable]
end

["hadoop","hduser"].each do |createme|
    user createme do
        action :create
    end
end

group "hadoop" do
    action :create
    append true
    members "hadoop, hduser, vagrant"
end

#set up environment variables for all users
cookbook_file "/etc/environment" do
    source "hive_environment"
    mode "0644"
end

#download the file
cookbook_file "/home/hadoop/hadoop-1.2.1.tar.gz" do
    source "hadoop-1.2.1.tar.gz"
    mode "0666"
    owner "hadoop"
    group "hadoop"
end

#unzip it
execute "unzip_hadoop" do
    command "cd /home/hadoop && tar -xvzf hadoop-1.2.1.tar.gz"
    creates "/home/hadoop/hadoop-1.2.1"
    user "hadoop"
    group "hadoop"
 end
 
 execute "ln /home/vagrant/hadoop /home/hadoop/hadoop-1.2.1/" do
    creates "/home/vagrant/hadoop"
 end

#update config files
cookbook_file "/home/hadoop/hadoop-1.2.1/conf/hadoop-env.sh" do
    source "hadoop-env.sh"
    mode "0664"
    owner "hadoop"
    group "hadoop"
end
cookbook_file "/home/hadoop/hadoop-1.2.1/conf/core-site.xml" do
    source "core-site.xml"
    mode "0664"
    owner "hadoop"
    group "hadoop"
end
cookbook_file "/home/hadoop/hadoop-1.2.1/conf/hdfs-site.xml" do
    source "hdfs-site.xml"
    mode "0664"
    owner "hadoop"
    group "hadoop"
end
cookbook_file "/home/hadoop/hadoop-1.2.1/conf/mapred-site.xml" do
    source "mapred-site.xml"
    mode "0664"
    owner "hadoop"
    group "hadoop"
end

#enable ssh to localhost for single node implementation (vagrant and root)
execute "createvagrant_key" do
    command "ssh-keygen -t dsa -P '' -f /home/vagrant/.ssh/id_dsa"
    creates "/home/vagrant/.ssh/id_dsa"
    user "vagrant"
end 
execute "authorizevagrant_key" do
    command "cat /home/vagrant/.ssh/id_dsa.pub >> /home/vagrant/.ssh/authorized_keys"
    user "vagrant"
end 
#enable ssh to localhost for single node implementation (vagrant and root)
execute "createhadoop_key" do
    command "ssh-keygen -t dsa -P '' -f /home/hadoop/.ssh/id_dsa"
    creates "/home/hadoop/.ssh/id_dsa"
    user "hadoop"
end 
execute "authorizehadoop_key" do
    command "cat /home/hadoop/.ssh/id_dsa.pub >> /home/hadoop/.ssh/authorized_keys"
    user "hadoop"
end 

execute "createroot_key" do
    command "ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa"
    creates "/root/.ssh/id_dsa"
    user "root"
end 
execute "authorizeroot_key" do
    command "cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys"
    user "root"
end 

#format the node. creates /tmp/hadoop-root/dfs/name     run this only once or you lose all data
execute "hadoop_format_namenode" do
  command "/home/hadoop/hadoop-1.2.1/bin/hadoop namenode -format"
  creates "/tmp/hadoop-root/dfs/name/"
  user "hadoop"
end

#start all the services
service "hadoop_services" do
  supports :status => true, :restart => true
  start_command "/home/vagrant/hadoop-1.2.1/bin/start-all.sh"
  restart_command "/home/vagrant/hadoop-1.2.1/bin/stop-all.sh && sudo /home/vagrant/hadoop-1.2.1/bin/start-all.sh"
  status_command 'jps | grep "JobTracker\|NameNode\|DataNode"'
  action [ :start ]
end

#clean up
execute "cleanup_hadoop" do
    command "mv /home/hadoop/hadoop-1.2.1.tar.gz /home/hadoop/hadoop-1.2.1/" 
    creates "/home/hadoop/hadoop-1.2.1/hadoop-1.2.1.tar.gz"
end

#---------------  Install Hive ------------------------------------------------

cookbook_file "/home/vagrant/hive-0.12.0.tar.gz" do
    source "hive-0.12.0.tar.gz"
    mode "0664"
    user "vagrant"
end

#unzip it
execute "unzip_hive" do
    command "cd /home/vagrant && tar -xvzf hive-0.12.0.tar.gz"
    creates "/home/vagrant/hive-0.12.0"
    user "vagrant"
 end
 