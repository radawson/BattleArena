plugins {
    id("maven-publish")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.modrinth.minotaur") version "2.+"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

val supportedVersions = listOf(
    "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.11"
)

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(libs.bstats.bukkit)
    compileOnlyApi(libs.paper.api)
    compileOnly(libs.worldedit)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    runServer {
        dependsOn("bundledJar")

        minecraftVersion("1.21.11")

        // Set Java 21 (1.21.11 requires Java 21)
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        relocate("org.bstats", "org.battleplugins.arena.util.shaded.bstats")

        // Merge META-INF files to avoid conflicts (e.g., from MySQL connector, HikariCP, etc.)
        mergeServiceFiles()
        
        // Exclude duplicate META-INF entries that cause conflicts
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        
        archiveFileName.set("BattleArena.jar")
    }

    val extractShadowJar by registering(Copy::class) {
        dependsOn(shadowJar)
        from(zipTree(shadowJar.get().archiveFile.get().asFile))
        into(layout.buildDirectory.get().asFile.resolve("extractedShadow"))
    }

    register<Jar>("bundledJar") {
        dependsOn(extractShadowJar)
        from(layout.buildDirectory.get().asFile.resolve("extractedShadow"))

        // Bundle in our modules
        project(":module").subprojects.forEach {
            from(it.tasks.jar) {
                into("modules")
            }
        }

        archiveFileName.set("BattleArena.jar")
        archiveClassifier.set("")
    }

    javadoc {
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}

// Gradle 9.2.1 requires explicit dependency on processJavaSources
// since sourcesJar uses the processed sources output
tasks.named<Jar>("sourcesJar") {
    dependsOn(tasks.named("processJavaSources"))
}

publishing {
    val isSnapshot = "SNAPSHOT" in version.toString()

    repositories {
        maven {
            name = "battleplugins"
            url = uri("https://repo.battleplugins.org/${if (isSnapshot) "snapshots" else "releases"}")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = "arena"

                from(components["java"])
                pom {
                    packaging = "jar"
                    url.set("https://github.com/BattlePlugins/BattleArena")

                    scm {
                        connection.set("scm:git:git://github.com/BattlePlugins/BattleArena.git")
                        developerConnection.set("scm:git:ssh://github.com/BattlePlugins/BattleArena.git")
                        url.set("https://github.com/BattlePlugins/BattleArena");
                    }

                    licenses {
                        license {
                            name.set("GNU General Public License v3.0")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        }
                    }

                    developers {
                        developer {
                            name.set("BattlePlugins Team")
                            organization.set("BattlePlugins")
                            organizationUrl.set("https://github.com/BattlePlugins")
                        }
                    }
                }
            }
        }
    }
}

modrinth {
    val snapshot = "SNAPSHOT" in rootProject.version.toString()

    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set("battlearena")
    versionNumber.set(rootProject.version as String + if (snapshot) "-" + System.getenv("BUILD_NUMBER") else "")
    versionType.set(if (snapshot) "beta" else "release")
    changelog.set(System.getenv("CHANGELOG") ?: "")
    uploadFile.set(tasks.named("bundledJar"))
    gameVersions.set(supportedVersions)
}
