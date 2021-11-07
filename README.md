# Introduction

The following explains how and for what the API can be used.

With the API you can hook into the plugin.

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

```java
public class TestCheck extends Check {

    @Override
    protected void onLoad() throws Exception {
    
	// do your thing when the Check loads
        FileConfiguration fileConfiguration = loadConfiguration();
        fileConfiguration.addDefault("Check.Wonderful", 5);
        
        fileConfiguration.save(file);
    }
    
    @Override
    protected void onUnload() throws Exception {
        // do your thing when the Check unloads
    }
    
    @Override
    public void onSuccess(User user) {
        // when the Check was successfull
    }
    
    @Override
    public void onFailure(User user) {
        // when the Check wasn't successfull
    }
    
    @Override
    public boolean check(User user) {
        // do whatever you have to do 
        return false; // return wheter the Check was successfull or not
    }
    
}
```

Now fill in the methods, go to your main class and register the Check in your onEnable()

```java

@Override
public void onEnable() {

// other stuff

  CheckManager.registerCheck(new TestCheck());

// another other stuff

}

```


