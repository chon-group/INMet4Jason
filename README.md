
# Pythia Middleware
[![](https://jitpack.io/v/chon-group/Pythia.svg)](https://jitpack.io/#chon-group/Pythia)

|![](src/main/resources/pythia.png)|
|:-:|
|Pythia is a middleware that integrates Java applications with the [INMET::AlertAS](https://alertas2.inmet.gov.br/) Service that issues alerts based on weather and climate monitoring, analysis, and forecasting provided by INMET (National Institute of Meteorology), an agency of the Ministry of Agriculture, Livestock and Food Supply.|



## How to import?

#### Using Maven:
  - Add it in your pom.xml at the end of repositories and add the dependency.
    <details>
    <summary>pom.xml</summary>

    ```xml
    <repositories>
      <repository>
        <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
    </repositories>

    <dependencies>
      <dependency>
        <groupId>com.github.chon-group</groupId>
        <artifactId>Pythia</artifactId>
        <version>1.1.0</version>
      </dependency>
    </dependencies>
    ```
    </details>

#### Using Gradle:
  - Add it in your root build.gradle at the end of repositories and add the dependency.
    <details>
    <summary>build.gradle</summary>

    ```xml
    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }

    dependencies {
      implementation 'com.github.chon-group:Pythia:1.1.0'
    }

    ```
    </details>

## COPYRIGHT
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />Pythia Middleware is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>. The licensor cannot revoke these freedoms as long as you follow the license terms:

* __Attribution__ — You must give __appropriate credit__ 
