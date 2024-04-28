## 简介
sql：
2个表(record,record_data)
test

#前端部署
#首次部署
#m2c-view目录下新增Dockerfile、default.conf
##切换到前端根目录下
#cd /home/ubuntu/m2c/m2c-view
#git pull
## 基于dockerfile打镜像
#docker build -t m2c-view:v1 .

#docker run -p 8001:80 --name=m2c-view -d m2c-view:v1
#更新部署
## 切换到前端根目录下，更新，显示版本
#cd /home/ubuntu/m2c/tansci-view && git pull && docker ps
## 基于dockerfile打镜像
#docker build -t m2c-view:v2.5.1.0 .
## 移除使用的容器
#docker stop m2c-view && docker rm m2c-view
## 启动容器
#docker run -p 8016:80 --name=m2c-view -d m2c-view:v2.5.1.0

##后端部署
## m2c根目录下新增Dockerfile
#cd /home/ubuntu/m2c && git pull && docker ps
## mvn打jar程序包
#mvn clean package -Dmaven.test.skip=true
## 基于dockerfile打镜像
#docker build -t m2c:v1.0.0.3 .
## 移除使用的容器
#docker stop m2c && docker rm m2c
## 启动容器
#docker run -d --name=m2c -p 8015:8015 -e SPRING_PROFILES_ACTIVE=dev  --restart=always --privileged=true  m2c:v1.0.0.3
#docker run -d --name=m2c -p 8015:8015 -e SPRING_PROFILES_ACTIVE=pro  --restart=always --privileged=true  m2c:v1.0.0.3

1.proxy_pass代理地址端口后有目录(包括 / )，转发后地址：代理地址+访问URL目录部分去除location匹配目录 
2.proxy_pass代理地址端口后无任何，转发后地址：代理地址+访问URL目录部