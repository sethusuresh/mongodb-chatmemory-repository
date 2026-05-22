# MongoDB Chat Memory Repository

A minimal Java library project configured for publishing to the local Maven repository via the Gradle wrapper.

## Prerequisites

- Java 21 (configured via Gradle toolchains)
- Gradle Wrapper included in the repo (`gradlew` / `gradlew.bat`)

## Build & Publish

Run the following from the project root:

- Clean: `./gradlew clean` (or `gradlew.bat clean` on Windows)
- Build: `./gradlew build` (or `gradlew.bat build`)
- Publish to local Maven repo: `./gradlew publishToMavenLocal` (or `gradlew.bat publishToMavenLocal`)

Artifacts will be placed in your local Maven repository (e.g., `~/.m2/repository`) using the group `com.springai` and version `0.0.1`.

## Source Layout

- Library entry class: `src/main/java/com/springai/chatmemoryrepository/MongodbChatmemoryRepository.java`
- Tests: `src/test/java`

## Notes

The Gradle build is configured with the `maven-publish` plugin; the publication is named `mavenJava` and publishes the `java` component defined in [`build.gradle`](build.gradle:1).
