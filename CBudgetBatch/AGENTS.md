# CBudgetBatch

## Build and test

- Use Maven with Java 8 compatibility. `pom.xml` compiles all production sources directly from `src/` (not `src/main/java`) with `Cp1252` encoding; preserve that encoding when editing existing Java files.
- Run the full verification suite with `mvn test`. Tests are JUnit 5/Mockito under `src/test/java`; run one with `mvn -Dtest=CalculateWeightsTest test`. `CalculateWeightsTest` currently takes about 50 seconds.
- Run `mvn package` to create `target/CBudgetBatch-3.0.0.jar` and copy runtime dependencies into `target/lib/`.

## Runtime and packaging

- `cbudgetbatch.Server` is the TCP ingest service on port 7777. The container also runs recurring plan-cache, forecast, and history-cleanup jobs through `scripts/wrapper.sh`.
- The Docker image requires the output of `mvn package`: it copies the JAR and `target/lib/*.jar`, then launches the scripts from `/var/lib/cbudgetbatch`. Do not use those scripts as local launchers; their classpaths and working directory are container-specific.
- Database access is PostgreSQL. The runtime scripts pass `$connectstring` to each Java entrypoint; `docker-compose.yml` supplies it along with the loop intervals and `computeWeights`. Treat the compose connection settings as deployment-specific, not local defaults.
