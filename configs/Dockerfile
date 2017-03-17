FROM tomcat:7-jre8
MAINTAINER Alpha Hinex <AlphaHinex@gmail.com>

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone

COPY tomcat/conf/server.xml /usr/local/tomcat/conf/server.xml
COPY tomcat/conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml

EXPOSE 8080