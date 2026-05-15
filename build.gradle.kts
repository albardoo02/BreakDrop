plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "net.azisaba"
version = "2.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paperweight.reobfArtifactConfiguration.set(io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://mvn.lumine.io/repository/maven-public/") }
    maven { url = uri("https://nexus.neetgames.com/repository/maven-public/") } // for mcMMO
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.12.0")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.051") {
        exclude("com.sk89q.worldguard", "worldguard-core")
        exclude("com.sk89q.worldguard", "worldguard-legacy")
    }
}

tasks {
    processResources {
        from(
            sourceSets.main
                .get()
                .resources.srcDirs,
        ) {
            include("**")
            val tokenReplacementMap =
                mapOf(
                    "version" to project.version,
                )
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokenReplacementMap)
        }
        filteringCharset = "UTF-8"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(projectDir) { include("LICENSE") }
    }

    compileJava {
        options.encoding = "UTF-8"
    }
}

publishing {
    repositories {
        maven {
            name = "repo"
            credentials(PasswordCredentials::class)
            url =
                uri(
                    if (project.version.toString().endsWith("SNAPSHOT")) {
                        project.findProperty("deploySnapshotURL")
                            ?: System.getProperty("deploySnapshotURL", "https://repo.azisaba.net/repository/maven-snapshots/")
                    } else {
                        project.findProperty("deployReleasesURL")
                            ?: System.getProperty("deployReleasesURL", "https://repo.azisaba.net/repository/maven-releases/")
                    },
                )
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.reobfJar)
        }
    }
}
