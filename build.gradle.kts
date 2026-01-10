plugins {
    java
    kotlin("jvm")
}

group = "com.xiangqi"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta4")
    implementation("com.github.haifengl:smile-core:5.1.0")
    implementation("org.apache.commons:commons-text:1.15.0") 
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

