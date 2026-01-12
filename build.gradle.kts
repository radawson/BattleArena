plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "org.battleplugins"

    repositories {
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "hangar"
            url = uri("https://maven.papermc.io/repository/maven-public/")
        }
        maven {
            name = "enginehub"
            url = uri("https://maven.enginehub.org/repo/")
        }
        mavenCentral()
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
        
        // Use CommandAPI 11.1.0 from local file (downloaded from Hangar)
        // Download URL: https://hangarcdn.papermc.io/plugins/Skepter/CommandAPI/versions/11.1.0/PAPER/CommandAPI-11.1.0-Paper.jar
        // This is needed because 11.1.0 isn't published to Maven repositories yet
        // Once 11.1.0 is available in Maven, this can be changed to:
        // implementation("dev.jorel:commandapi-bukkit-shade:11.1.0")
        implementation(files("libs/commandapi-bukkit-shade-11.1.0.jar"))
        
        // SQLite JDBC driver - embedded database, no external server needed
        implementation("org.xerial:sqlite-jdbc:3.45.1.0")
        
        // HikariCP - high-performance connection pooling for MySQL
        implementation("com.zaxxer:HikariCP:5.1.0")
        
        // MySQL connector - only loaded if MySQL is configured (compileOnly to reduce JAR size)
        compileOnly("com.mysql:mysql-connector-j:8.3.0")
        
        // Apply paperweight dev bundle only if paperweight plugin is applied
        if (project.plugins.hasPlugin("io.papermc.paperweight.userdev")) {
            paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks {
        // Process resources to replace version placeholders
        // This replaces ${version} and ${project.version} in resource files during build
        // without modifying source files - processed files go to build output directory
        processResources {
            // Process all YAML resource files for version expansion
            filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
                // Replace ${version} and ${project.version} with actual version from gradle.properties
                expand(
                    "version" to project.version,
                    "project.version" to project.version
                )
                // Also handle $version (without braces) for compatibility
                filter { line ->
                    line.replace("\$version", project.version.toString())
                }
            }
        }
        
        // Process Java source files to replace version placeholder in @version annotation
        // This processes source files before compilation without modifying the original source files
        val processJavaSources by registering(Copy::class) {
            val version = project.version.toString()
            from(sourceSets.main.get().java.srcDirs)
            into(layout.buildDirectory.dir("processed-sources/java").get().asFile)
            
            // Replace version placeholder in Java source files
            filter { line ->
                line.replace("{\$version}", version)
            }
            
            // Include all Java files
            include("**/*.java")
            
            // Preserve directory structure
            includeEmptyDirs = false
        }
        
        // Make compileJava depend on processing Java sources and use processed sources
        compileJava {
            dependsOn(processJavaSources)
            
            // Configure to use processed sources
            doFirst {
                // Temporarily add processed sources to the source set for this compilation
                val processedSourceDir = layout.buildDirectory.dir("processed-sources/java").get().asFile
                if (processedSourceDir.exists()) {
                    sourceSets.main.get().java.srcDir(processedSourceDir)
                    // Remove original source directories to avoid duplicates
                    sourceSets.main.get().java.setSrcDirs(listOf(processedSourceDir))
                }
            }
        }
        
        // Configure the JAR task to include plugin.yml
        jar {
            archiveBaseName.set("BattleArena")
            archiveVersion.set(project.property("version") as String)
            
            // Include plugin.yml in the JAR
            from(sourceSets.main.get().output)
            
            // Copy resources (like plugin.yml) into the JAR
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    tasks.jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
    
    // Note: ShadowJar configuration is handled in plugin/build.gradle.kts
    // The plugin project uses com.github.johnrengelman.shadow for its build process
}