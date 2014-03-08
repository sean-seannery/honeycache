["openjdk-7-jdk", "libmysql-java"].each do |jpkg|
    package jpkg do
        action :install
    end

end
