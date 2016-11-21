FROM tomcat:7-jre7
MAINTAINER Alpha Hinex <AlphaHinex@gmail.com>

RUN apk add --no-cache tzdata
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
RUN apk del tzdata


COPY tomcat/server.xml /usr/local/tomcat/conf/server.xml

EXPOSE 9090