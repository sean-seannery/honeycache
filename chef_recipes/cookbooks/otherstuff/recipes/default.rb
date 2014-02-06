#install other utilities
execute "sudo apt-get update"

["vim", "curl", "wget", "rpm", "make", "ssh", "rsync" ].each do |installme|
    package installme do
        action :install
    end
end

execute "sudo /usr/sbin/sshd"

execute "sudo ufw disable"