#---------------------------install Hadoop utilities------------------------------------------


package "mysql-server" do
    action :install
end

service "mysql" do
    action [:start, :enable]
end

["hadoop","hduser"].each do |createme|
    user createme do
        home "/home/#{createme}"
        supports :manage_home => true
        action :create
        #password "$6$jITd7aMT$kFkCmHN7SwYlwmLxTkjGR7TVvsvN9xdeifnoNcBdPSP4LIqyKYk3HLVVNOsYA/s5yYUDQ80Af5shvuJKRRiyi0"
        shell "/bin/bash"
    end
end

group "hadoop" do
    action :create
    append true
    members ["hadoop", "hduser", "vagrant"]
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

execute "add localhost to known_hosts" do
  command "ssh-keyscan localhost >> /home/hadoop/.ssh/known_hosts"
  user "hadoop"
  not_if "grep -q \"`ssh-keyscan localhost`\" /home/hadoop/.ssh/known_hosts"
end

#format the node. creates /tmp/hadoop-root/dfs/name     run this only once or you lose all data
execute "hadoop_format_namenode" do
  command "/home/hadoop/hadoop-1.2.1/bin/hadoop namenode -format"
  creates "/tmp/hadoop-hadoop/dfs/name/"
  user "hadoop"
end

# start all the services
execute "/home/hadoop/hadoop-1.2.1/bin/start-all.sh" do
  not_if "jps | grep 'JobTracker\|NameNode\|DataNode\|TaskTracker'"
  user "hadoop"
end

 execute "ln -s /home/hadoop/hadoop-1.2.1/ /home/vagrant/hadoop" do
    creates "/home/vagrant/hadoop"
 end

#clean up
execute "cleanup_hadoop" do
    command "mv /home/hadoop/hadoop-1.2.1.tar.gz /home/hadoop/hadoop-1.2.1/" 
    creates "/home/hadoop/hadoop-1.2.1/hadoop-1.2.1.tar.gz"
end

#---------------  Install Hive ------------------------------------------------

cookbook_file "/home/hadoop/hive-0.12.0.tar.gz" do
    source "hive-0.12.0.tar.gz"
    mode "0664"
    user "hadoop"
end

#unzip it
execute "unzip_hive" do
    command "cd /home/hadoop && tar -xvzf hive-0.12.0.tar.gz"
    creates "/home/hadoop/hive-0.12.0"
    user "hadoop"
 end

execute "ln -s /home/hadoop/hive-0.12.0/ /home/vagrant/hive" do
    creates "/home/vagrant/hive"
 end
 
 #clean up
execute "cleanup_hive" do
    command "mv /home/hadoop/hive-0.12.0.tar.gz /home/hadoop/hive-0.12.0/" 
    creates "/home/hadoop/hive-0.12.0/hive-0.12.0.tar.gz"
end

#create hive directories in hdfs
execute "hadoop fs -mkdir /tmp" do
    user "hadoop"
    not_if "hadoop fs -ls /tmp"
end
execute "hadoop fs -mkdir /user/hive/warehouse" do
    user "hadoop"
    not_if "hadoop fs -ls /user/hive/warehouse"
end
execute "hadoop fs -chmod g+w /tmp" do
    user "hadoop"
end
execute "hadoop fs -chmod g+w /user/hive/warehouse" do
    user "hadoop"
end

 