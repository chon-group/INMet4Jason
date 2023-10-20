# Pythia Middlware
[![](https://jitpack.io/v/chon-group/pythia.svg)](https://jitpack.io/#chon-group/pythia)



## Importing the Pythia Middleware using Maven
To use the Pythia Middlaware into your project build:

Step 1. Add the JitPack repository to your build file 
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Step 2. Add the dependency
```
	<dependency>
	    <groupId>com.github.chon-group</groupId>
	    <artifactId>pythia</artifactId>
	    <version>rc-1.0</version>
	</dependency>
```

## Importing the Pythia Middleware using Gradle
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.chon-group:pythia:rc-1.0'
	}
```
