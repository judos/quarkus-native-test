# quarkus-test

This project uses Quarkus, see <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Building fat jar

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
java -jar build/quarkus-test-1.0.0-SNAPSHOT-runner.jar
```

# Building native executable

From WSL Ubuntu 20.04
```shell script
./gradlew build -D"quarkus.native.enabled"=true -D"quarkus.package.jar.enabled"=false
./build/quarkus-test-1.0.0-SNAPSHOT-runner
```
Or:
```shell script
./gradlew build -D"quarkus.native.enabled"=false -D"quarkus.native.container-build"=true
```
