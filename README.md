# SoPra FS21 - Group 11 - Usgrächnet Bünzen

## Introduction - project aim

Our project's aim is to implement the game *Usgrächnet Bünzen*. The original game allows users to test their
knowledge of Swiss locations in a playful manner. Each player can place location cards on a board
in relation to other locations based on their coordinates. All other players have the chance to doubt the 
card placement if they believe it is wrong. Players that continuously place cards correctly and prove other 
people's card placement to be wrong will be awarded tokens. The player with most tokens wins the game.
For a more extensive explanation of the original game please check out this **[manual](public/Usgrachnet_Help.pdf)**.


In order to introduce more complexity we added the following features:
* players can choose their **own game settings**
* players can **customize** the card decks to play with
* players can fetch international locations and create **new cards**


## Technologies

### Websocket

### React-js

### [Spring Boot](https://spring.io/)

### 

## High-level components

## Launch & Deployment - for joining developers

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs21` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## Illustrations of Game

## Roadmap

>

## Authors and acknowledgment

A special thanks goes to our TA Raffi and the sopra team FS21.

### Authors
- [Martin](https://github.com/tinu0816)
- [Tobias](https://github.com/tobcode)
- [Lukas](https://github.com/LukZeh)
- [Tanzil](https://github.com/tanzilkm)
- [Debby](https://github.com/theDebbister)



## License


