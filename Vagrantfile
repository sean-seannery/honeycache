# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # The url from where the 'config.vm.box' box will be fetched if it doesn't exist
  config.vm.box_url = "http://files.vagrantup.com/precise32.box"


  config.vm.define "hive" do |hive|
      # Every Vagrant virtual environment requires a box to build off of.
      hive.vm.box = "precise32"
      hive.vm.hostname = "hive"
      hive.vm.network "private_network", ip: "192.168.2.200"
    # hive.vm.network "private_network", ip: "192.168.1.200", :bridge => 'Wireless Network Connection'	  
	# hive.vm.network "forwarded_port", guest: 50070, host: 50070
    # hive.vm.network "forwarded_port", guest: 50030, host: 50030
      hive.vm.provider :virtualbox do |vb|
        # Use VBoxManage to customize the VM. For example to change memory:
        vb.customize ["modifyvm", :id, "--memory", "2048"]
        vb.customize ["modifyvm", :id, "--cpus", "2"]
        vb.name = "hive"
      end
      
       # Share an additional folder to the guest VM. The first argument is
       # the path on the host to the actual folder. The second argument is
       # the path on the guest to mount the folder. And the optional third
       # argument is a set of non-required options.
      config.vm.synced_folder ".", "/home/vagrant/honeycache"
      
      # config.vm.network :forwarded_port, guest: 80, host: 8080
      
      hive.vm.provision :chef_solo do |chef|
         chef.cookbooks_path = "./scripts/chef_recipes/cookbooks"
         chef.add_recipe "otherstuff"
         chef.add_recipe "java"
         chef.add_recipe "hive"
         chef.add_recipe "cassandra"
      end
  end
  

end
