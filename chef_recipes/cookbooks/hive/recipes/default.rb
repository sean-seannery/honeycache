#---------------------------install Hadoop utilities------------------------------------------

#set up environment variables for all users
cookbook_file "/etc/environment" do
    source "hive_environment"
    mode "0644"
end

#download the file
cookbook_file "/home/vagrant/hadoop-1.2.1.tar.gz" do
    source "hadoop-1.2.1.tar.gz"
    mode "0666"
    owner "vagrant"
    group "vagrant"
end

#unzip it
execute "unzip_hadoop" do
    command "cd /home/vagrant && tar -xvzf hadoop-1.2.1.tar.gz"
    creates "/home/vagrant/hadoop-1.2.1"
    user "vagrant"
 end

#update config files
cookbook_file "/home/vagrant/hadoop-1.2.1/conf/hadoop-env.sh" do
    source "hadoop-env.sh"
    mode "0664"
    owner "vagrant"
    group "vagrant"
end
cookbook_file "/home/vagrant/hadoop-1.2.1/conf/core-site.xml" do
    source "core-site.xml"
    mode "0664"
    owner "vagrant"
    group "vagrant"
end
cookbook_file "/home/vagrant/hadoop-1.2.1/conf/hdfs-site.xml" do
    source "hdfs-site.xml"
    mode "0664"
    owner "vagrant"
    group "vagrant"
end
cookbook_file "/home/vagrant/hadoop-1.2.1/conf/mapred-site.xml" do
    source "mapred-site.xml"
    mode "0664"
    owner "vagrant"
    group "vagrant"
end

#enable ssh to localhost for single node implementation (vagrant and root)
execute "create_key" do
    command "ssh-keygen -t dsa -P '' -f /home/vagrant/.ssh/id_dsa"
    creates "/home/vagrant/.ssh/id_dsa"
    user "vagrant"
end 
execute "authorize_key" do
    command "cat /home/vagrant/.ssh/id_dsa.pub >> /home/vagrant/.ssh/authorized_keys"
    user "vagrant"
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
  command "/home/vagrant/hadoop-1.2.1/bin/hadoop namenode -format"
  creates "/tmp/hadoop-root/dfs/name/"
end

#start all the services
service "hadoop_services" do
  supports :status => true, :restart => true
  start_command "sudo /home/vagrant/hadoop-1.2.1/bin/start-all.sh"
  restart_command "sudo /home/vagrant/hadoop-1.2.1/bin/stop-all.sh && sudo /home/vagrant/hadoop-1.2.1/bin/start-all.sh"
  status_command 'jps | grep "JobTracker\|NameNode\|DataNode"'
  action [ :start ]
end

#clean up
execute "cleanup_hadoop" do
    command "mv /home/vagrant/hadoop-1.2.1.tar.gz /home/vagrant/hadoop-1.2.1/" 
    creates "/home/vagrant/hadoop-1.2.1/hadoop-1.2.1.tar.gz"
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
 