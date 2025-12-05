# VOD Browser

TODO

# Getting started

1. Have JDK 21 installed
2. Open the project in IDEA
    - If IDEA complains about JDK versions, check that the correct JDK is
      set in `Settings | Build, Execution, Deployment | Build Tools | Gradle`
3. Run `subprojects/backend/src/main/kotlin/org/reckfularchive/browser/backend/Application.kt`
4. Open `https://localhost:8080` in your browser
5. ???
6. PROFIT!

## How to add/update dependencies

See [libs.versions.toml](gradle/libs.versions.toml)
and [subprojects/backend/build.gradle.kts](subprojects/backend/build.gradle.kts)
to figure out.

## Where are the dependencies / plugins configured?

[Gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html) are used to do that,
located [here](build-logic/src/main/kotlin).

Most dependency versions are resolved automatically by Spring's dependency management plugin.

### !!! IMPORTANT !!!

After any changes in `libs.versions.toml` or inside `build-logic`, you need to reload
all Gradle projects (refresh button in IDEA). The changes need to be compiled
to be accessible from other build scripts.

## Actuator

Actuator adds various endpoints, they're behind the same basic auth as swagger ui

Some endpoints:

* http://localhost:8080/actuator/health
* http://localhost:8080/actuator/info (git commit info is pretty cool)
* http://localhost:8080/actuator/metrics
    * ^ will show a list of values, you can query each one individiaully,
      like http://localhost:8080/actuator/metrics/system.cpu.count
* http://localhost:8080/actuator/loggers
    * ^ will show a list of loggers, you can change the levels in
      runtime, see https://docs.spring.io/spring-boot/api/rest/actuator/loggers.html#loggers.setting-level
* http://localhost:8080/actuator/scheduledtasks for any `@Scheduled` tasks
* http://localhost:8080/actuator/flyway for flyway migrations (persistence convention plugin needs to be applied)

## How to build a bootJar

Run

```bash
./gradlew bootJar
```

and see [subprojects/backend/build/libs](subprojects/backend/build/libs)

## If shit goes wrong

If you suspect that Gradle is acting funny (caching is enabled), try

```bash
./gradlew clean
./gradlew --stop

pkill -f ".*gradle.*"
```

It's the equivalent of IDEA's "invalidate caches"
