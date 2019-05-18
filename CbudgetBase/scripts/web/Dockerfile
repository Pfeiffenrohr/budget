FROM tomcat:8-jre8  
MAINTAINER lpradel

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh  
COPY ./budget.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]  
