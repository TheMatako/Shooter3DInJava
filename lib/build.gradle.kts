plugins {
    id("java")
    id("application")
}

group = "com.shooter3d"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // JMonkeyEngine core
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
    
    // Backend pour le rendu (LWJGL 3 recommandé)
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.6.1-stable")
    
    // Desktop support
    implementation("org.jmonkeyengine:jme3-desktop:3.6.1-stable")
    
    // Effets visuels (optionnel)
    implementation("org.jmonkeyengine:jme3-effects:3.6.1-stable")
    
    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

application {
    // Ta classe principale
    mainClass.set("shooter3D.Shooter3D")
}

// Java 25
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}