# CBudgetBatch

## Build and test

- Use Maven with Java 8 compatibility. `pom.xml` compiles all production sources directly from `src/` (not `src/main/java`) with `Cp1252` encoding; preserve that encoding when editing existing Java files. Test sources live under `src/test/` below that same root and are excluded from the production compile.
- Run the full verification suite with `mvn test`. Tests are JUnit 5/Mockito under `src/test/java`; run one with `mvn -Dtest=CalculateWeightsTest test`. The whole suite takes about 17 seconds, of which `CalculateWeightsTest` is roughly 6. It was about 50 seconds before `CalculateWeights` switched from boxed `Map`s to primitive arrays; do not reintroduce the `Map` variant.
- Run `mvn package` to create `target/CBudgetBatch-3.0.0.jar` and copy runtime dependencies into `target/lib/`. Use `mvn clean package` when checking jar contents: without `clean`, stale directories from earlier builds are still archived.

## Runtime and packaging

- `cbudgetbatch.Server` is the TCP ingest service on port 7777. The container also runs recurring plan-cache, forecast, forecast-evaluation, and history-cleanup jobs through `scripts/wrapper.sh`.
- The Docker image requires the output of `mvn package`: it copies the JAR and `target/lib/*.jar`, then launches the scripts from `/var/lib/cbudgetbatch`. Do not use those scripts as local launchers; their classpaths and working directory are container-specific.
- Database access is PostgreSQL. The runtime scripts pass `$connectstring` to each Java entrypoint; `docker-compose.yml` supplies it along with the loop intervals and `computeWeights`. Treat the compose connection settings as deployment-specific, not local defaults.

## Forecast accuracy evaluation

- `db/forecast_evaluation.sql` must be applied once by hand (`psql -d <db> -f db/forecast_evaluation.sql`). Nothing applies it automatically, and there is no migration tool. It creates the three tables, the indexes, the `forecast_eval_*` settings, and the `v_forecast_*` views that the UI reads.
- Every `cbudgetbatch.Forecast` run writes a snapshot of its monthly projections to `forecast_snapshot`. `Forecast.deleteOldForecast` removes the live forecast rows first, so the snapshot is the only record of what was predicted. The run passes a stichtag (`setBasisZeit`) so the horizon is reproducible.
- `cbudgetbatch.evaluation.ForecastEvaluation` compares past snapshots with actual transactions and is started once a day by `scripts/forecast-evaluation.sh`. It re-evaluates the same months every day so late bookings still land in already-closed periods; `forecast_evaluation` therefore upserts on `(monat_key, kategorie, konto)`.
- The core rule is in `SnapshotSelector`: only a snapshot created strictly before the first day of the period counts. A forecast recomputed mid-period knows that period's data and would confirm itself.
- `ForecastMetricsCalculator` holds the metric formulas and the two special cases the spec calls for: missing actual values and near-zero forecasts are flagged (`kein_ist`, `kein_forecast`) and excluded from the averages. The `v_forecast_*` views implement the same formulas, so keep them in step. Note that the percentage deviation inverts its sign for negative forecasts; this is documented on the method and is intentional.
- Retention lives in `ThreadDeleteForecastSnapshots`, started by `DeleteOldHistoryFiles` (not by `wrapper.sh`). Snapshots grow by roughly 300k rows a year; the default retention is 3 years via `forecast_eval_retention_jahre`.

## Known environment limits

- No PostgreSQL and no `psql` are available here, so the DDL has never been executed. Verify it against a real database before deploying; the views and the `on conflict` clauses are untested by the test suite.
- `mvn -o` fails because `maven-resources-plugin:2.6` is not in the local cache. Run Maven online.
- `target/lib/` still receives JUnit and Mockito even though they are `test` scope, because `copy-dependencies` copies every scope unless `includeScope` is set. Do not add `includeScope=runtime` to fix this: it drops the `system`-scoped PostgreSQL driver from `target/lib` and breaks the container.
- All `scripts/*.sh` reference `/var/lib/cbudgetbatch/server.jar`, while `pom.xml` produces `CBudgetBatch-3.0.0.jar`. Pre-existing, and the scripts were not changed to match, since the deployment may rename the artifact.
- `src/test/java/cbudgetbatch/CalculateForecastTest.java` is an empty placeholder with no assertions and a misleading name. It passes trivially and covers nothing.
