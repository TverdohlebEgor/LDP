plugins {
    id("java")
}

group = "ldp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.slf4j:slf4j-api:2.0.13")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
}

tasks.test {
    useJUnitPlatform()
}