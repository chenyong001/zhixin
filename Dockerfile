FROM com/my/python-java:v1.1
#FROM openjdk:8-jdk-alpine
#基础镜像
#工作目录
RUN mkdir /app
WORKDIR /app
# 修改时区
ENV TZ Asia/Shanghai
#加入jar包
ADD  target/*.jar /app/m2c.jar
#创建日志目录
RUN mkdir /logs
#暴露端口
EXPOSE 8006
# 运行jar包
ENTRYPOINT ["nohup","java","-jar","/app/m2c.jar","&"]

