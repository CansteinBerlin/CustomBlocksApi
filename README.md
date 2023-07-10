# Custom Blocks Api

*This api works for minecraft versions 1.19.4*

An API for easily creating custom blocks that is heavily inspired by the Fabric API.
For information on how to use this api, see the GitHub wiki.

## Maven Dependency

To add this project as a dependency to your pom.xml, you must first add the repository:

```xml

<repositories>
    <repository>
        <id>hasenzahn-customblocksapi</id>
        <url>https://dl.cloudsmith.io/public/hasenzahn/customblocksapi/maven/</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </releases>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

After that you need to add the dependency:

```xml

<dependency>
    <groupId>de.canstein_berlin</groupId>
    <artifactId>CustomBlocksApi</artifactId>
    <version>0.1.0-alpha</version>
    <scope>provided</scope>
</dependency>
```

## Gradle Dependency

To add this API as a dependency for your Gradle project, make sure that the `dependencies` section in your build.gradle
looks like this
looks like this

```groovy
dependencies {
    implementation 'de.canstein_berlin:CustomBlocksApi:0.1.0-alpha'
    // ...
}
```

This project is hosted on Cloudsmith, so make sure your repositories section looks like this

```groovy
repositories {
    maven {
        url "https://dl.cloudsmith.io/public/hasenzahn/customblocksapi/maven/"
    }
    //...
}
```