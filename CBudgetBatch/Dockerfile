FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

RUN apt-get update && apt-get install -y dos2unix

RUN mkdir /var/lib/cbudgetbatch
WORKDIR /var/lib/cbudgetbatch/
ADD target/CBudgetBatch-0.0.1-SNAPSHOT.jar /var/lib/cbudgetbatch/server.jar
ADD scripts/budget_server.sh /var/lib/cbudgetbatch/budget_server.sh
RUN chmod 777 /var/lib/cbudgetbatch/budget_server.sh
RUN dos2unix /var/lib/cbudgetbatch/budget_server.sh

ADD scripts/wrapper.sh /var/lib/cbudgetbatch/wrapper.sh
RUN chmod 777 /var/lib/cbudgetbatch/wrapper.sh
RUN dos2unix /var/lib/cbudgetbatch/wrapper.sh

ADD scripts/plan_cache_trigger.sh /var/lib/cbudgetbatch/plan_cache_trigger.sh
RUN chmod 777 /var/lib/cbudgetbatch/plan_cache_trigger.sh
RUN dos2unix /var/lib/cbudgetbatch/plan_cache_trigger.sh

COPY lib/*.jar /var/lib/cbudgetbatch
ENTRYPOINT ["/var/lib/cbudgetbatch/wrapper.sh"]
