FROM java:8
RUN mkdir /var/lib/cbudgetbatch
WORKDIR /var/lib/cbudgetbatch/
ADD target/CBudgetBatch-0.0.1-SNAPSHOT.jar /var/lib/cbudgetbatch/server.jar
ADD scrips/budget_server.sh /var/lib/cbudgetbatch/budget_server.sh
ADD scrips/plan_cache_trigger.sh /var/lib/cbudgetbatch/plan_cache_trigger.sh
ADD lib/postgresql-42.2.4.jar /var/lib/cbudgetbatch/postgresql-42.2.4.jar
CMD ["java","--classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar","cbudgetbatch/Server  budget budget budget"]
