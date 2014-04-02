#install other utilities
execute "sudo apt-get update"

["vim", "curl", "wget", "rpm", "make", "ssh", "rsync", "unzip", "lzop", "liblzo2-dev", "daemon" ].each do |installme|
    package installme do
        action :install
    end
end

service "sshd" do 
    action [:start]
end

execute "sudo ufw disable"

# execute "sudo gem install ruby-shadow" do 
    # creates "/root/.ssh/id_dsa"
    # not_if 'gem list | grep ruby-shadow'
    # user "vagrant"
# end 

