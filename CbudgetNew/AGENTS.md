# CbudgetNew

## Build and verification

- Build the deployable WAR with `mvn package`; `pom.xml` compiles Java 8 sources from the nonstandard `src/` directory and packages `WebContent/`.
- Preserve the configured `Cp1252` source encoding. Several Java files contain non-ASCII German text that will be corrupted if saved as UTF-8 without changing the build configuration.
- There are no test sources, test plugins, lint, formatter, or CI configuration in this checkout. Use `mvn package` as the available focused verification.
- The build artifact is `target/CbudgetNew-3.0.0.war`. `Dockerfile` currently copies `target/CbudgetNew-3.0.0` (without `.war`), so resolve that mismatch before relying on `docker build`.

## Application structure

- This is a legacy Servlet 3.1 application deployed as a WAR to Tomcat, not a Spring application. All servlet classes live in `src/budget/`; they generate HTML directly rather than using JSPs.
- `WebContent/WEB-INF/web.xml` is the routing and runtime configuration source of truth. Add or change servlet URLs there, and keep its servlet declaration and mapping aligned.
- Authenticated handlers expect the login servlet to have placed both `db` and `auth == "ok"` in the HTTP session. Database connection settings are the `DBusername`, `DBuserpassword`, and `DBconnectstring` context parameters in `web.xml`.
- Persistence is provided by the external `cbudgetbase.DB` class from the JitPack `com.github.Pfeiffenrohr:budget:Version3` dependency; this repository does not contain its schema or DB implementation.

## Runtime

- The Compose files run the app on host port `8081` and PostgreSQL 9 as `myapp-db`; the default `web.xml` JDBC URL uses that Compose hostname. Persistent database data is bind-mounted at `./data`.
- The container deploys the WAR as `budget.war`, so the application context is `/budget` when running through the supplied image.
