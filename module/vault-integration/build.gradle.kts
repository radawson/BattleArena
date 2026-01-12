repositories {
    maven("https://jitpack.io")
}

dependencies {    
    // VaultAPI - only needed for interface definitions at compile time (compileOnly, not included in JAR)
    // ServiceIO implements these interfaces at runtime, but we need the interface classes for compilation
    // This is compileOnly so it won't be bundled - ServiceIO will be used at runtime instead
    // Note: Vault 1.7.3 is the latest release (https://github.com/MilkBowl/Vault), but VaultAPI 1.7
    // is the latest version available on JitPack that works for our use case (interface definitions only)
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        // Exclude old Bukkit dependency - we use Paper API from root build
        exclude(group = "org.bukkit", module = "bukkit")
    }
}