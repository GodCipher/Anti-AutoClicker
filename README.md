# Introduction

The following explains how and for what the API can be used.

With the API you can create standalone Addons, which you can put into plugins/AntiAC/Checks.
*The Spigot-API still have to be implemented*

You can also implement the API into your plugin.

The API is mainly intended to create new Checks.
The data of the User can also be modified by using the API.
Otherwise, a little bit more is possible and what you make possible.

# How to implement 

You can simply implement the API into your Addon/Plugin by:

### Add the AntiAC.jar to your project library
Not much to explain here.

### Maven

Just add this to your pom.xml: 

Add the JitPack repository to your build file 
```java
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Add the dependency (You can, if there are more versions, ofc. change the version)
```java
	<dependency>
	    <groupId>com.github.Luziferium</groupId>
	    <artifactId>Anti-Auto-Clicker</artifactId>
	    <version>2.6.3</version>
	</dependency>
```

### Gradle

Add the JitPack repository to your build file 
Add it in your root build.gradle at the end of repositories:
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency
```java

	dependencies {
	        implementation 'com.github.Luziferium:Anti-Auto-Clicker:version'
	}

```

# How to use

### Creating a new Check

For this purpose you will need a class:

```java
public class TestCheck {

}
```

This class we will now extend with Check:
*This will add some methods to the class and make the class visible for the ClassLoader*

```java
public class TestCheck extends Check {

    @Override
    public void onEnable() {
        // everything in here will be executed, when the Check gets loaded
    }

    @Override
    public void execute(User user) {
        // everything in here will be executed
    }
    
}
```

Now you just have to fill in the methods and there you go.


