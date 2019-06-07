FROM java:8
RUN apt-get update && apt-get install -y dos2unix
RUN mkdir /var/lib/cbudgetbatch
WORKDIR /var/lib/cbudgetbatch/
ADD target/CBudgetBatch-0.0.1-SNAPSHOT.jar /var/lib/cbudgetbatch/server.jar
ADD scripts/budget_server.sh /var/lib/cbudgetbatch/budget_server.sh
RUN dos2unix /var/lib/cbudgetbatch/budget_server.sh
ADD scripts/plan_cache_trigger.sh /var/lib/cbudgetbatch/plan_cache_trigger.sh
RUN dos2unix /var/lib/cbudgetbatch/plan_cache_trigger.sh
ADD lib/postgresql-42.2.4.jar /var/lib/cbudgetbatch/postgresql-42.2.4.jar
CMD ["java","-classpath","/var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar","cbudgetbatch/Server  budget budget budget"]
