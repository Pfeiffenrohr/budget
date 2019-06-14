FROM tomcat:8-jre8  
MAINTAINER lpradel

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh  
COPY target/CbudgetNew-1.0 /usr/local/tomcat/webapps/budget.war

CMD ["catalina.sh", "run"] 