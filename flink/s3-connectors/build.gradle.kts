plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val lombokDependency = "org.projectlombok:lombok:1.18.22"
var flinkVersion = "1.17.1"
val jacksonVersion = "2.13.4"
var slf4jVersion = "2.0.9"
var logbackVersion = "1.4.14"
dependencies {
    annotationProcessor(lombokDependency)
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    shadow(lombokDependency)
    shadow("org.slf4j:slf4j-simple:$slf4jVersion")
    shadow("org.apache.flink:flink-streaming-java:$flinkVersion")
    shadow("org.apache.flink:flink-clients:$flinkVersion")
    implementation("org.apache.flink:flink-s3-fs-presto:$flinkVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // implementation("org.apache.flink:flink-file-sink-common:$flinkVersion")
    // implementation("org.apache.flink:flink-connector-files:$flinkVersion")
    // implementation("org.apache.flink:flink-parquet:$flinkVersion")
    // implementation("org.apache.flink:flink-walkthrough-common:$flinkVersion")
    // implementation("org.apache.flink:flink-streaming-java:$flinkVersion")
    // implementation("org.apache.flink:flink-clients:$flinkVersion")
    // implementation("org.apache.flink:flink-connector-datagen:$flinkVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "tech.geekcity.flink.App"
}

tasks.shadowJar {
    relocate("com.google.common", "tech.geekcity.flink.shadow.com.google.common")
}