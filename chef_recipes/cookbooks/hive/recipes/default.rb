#install hive utilities

#download the file

cookbook_file "/home/vagrant/hadoop-1.2.1.tar.gz" do
    source "hadoop-1.2.1.tar.gz"
    mode "0664"
end

#unzip it
execute "cd /home/vagrant && tar -xvzf hadoop-1.2.1.tar.gz"

cookbook_file "/home/vagrant/hadoop-1.2.1/conf/hadoop-env.sh" do
    source "hadoop-env.sh"
    mode "0664"
end

cookbook_file "/home/vagrant/hadoop-1.2.1/conf/core-site.xml" do
    source "core-site.xml"
    mode "0664"
end

cookbook_file "/home/vagrant/hadoop-1.2.1/conf/hdfs-site.xml" do
    source "hdfs-site.xml"
    mode "0664"
end

cookbook_file "/home/vagrant/hadoop-1.2.1/conf/mapred-site.xml" do
    source "mapred-site.xml"
    mode "0664"
end

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
    not_if { ::File.exists?("~/.ssh/id_dsa")}
    user "root"
end 

execute "authorizeroot_key" do
    command "cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys"
    user "root"
end 

