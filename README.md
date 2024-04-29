<h1 align="center"> Vitrum </h1>

<p align="center">Library for accessing hidden types with version incompatibility.</p>

<p align="center">
    <img src="https://img.shields.io/github/license/pesekjak/vitrum?style=for-the-badge&color=107185" alt="LICENSE">
    <img src="https://img.shields.io/github/v/release/pesekjak/vitrum?style=for-the-badge&color=edb228" alt="RELEASE">
</p>

---

This library was designed mainly for Spigot plugin development
to make working with the server code easier, as method and
class names are often changed between versions.

Vitrum allows developers to create single interface that can
be mapped to multiple different methods of different
server versions. During this process no reflection calls are
required and everything is handled via dynamically created
classes.

This project is heavily inspired by [Glass](https://github.com/Moderocky/Glass/),
while offering a slightly altered API design that prioritizes
flexibility over intuitive usage.

Working example can be seen in the [test](vitrum/src/test/java) directory.

### Importing

#### Gradle

```kotlin
repositories {
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repo.machinemc.org/releases")
    }
}

dependencies {
    implementation("me.pesekjak:vitrum:VERSION")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>machinemc-repository-releases</id>
        <name>MachineMC Repository</name>
        <url>https://repo.machinemc.org/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.pesekjak</groupId>
        <artifactId>vitrum</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

### License
Vitrum is free software licensed under the [MIT license](LICENSE).
