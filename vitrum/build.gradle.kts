plugins {
    id("java-library-convention")
    `maven-publish`
}

dependencies {
    implementation(libs.asm)
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("https://repo.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.pesekjak"
            artifactId = "vitrum"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
