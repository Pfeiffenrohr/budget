FROM tomcat:8.5-jre8  
MAINTAINER lpradel

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh  
COPY target/CbudgetNew-3.0.0 /usr/local/tomcat/webapps/budget.war

CMD ["catalina.sh", "run"]