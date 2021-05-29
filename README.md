[![Deploy Project](https://github.com/sopra-fs21-group-11/sopra-server/actions/workflows/deploy.yml/badge.svg)](https://github.com/sopra-fs21-group-11/sopra-server/actions/workflows/deploy.yml)
# SoPra FS21 - Group 11 - Usgrächnet Bünzen

## The Game

Our project's aim is to implement the game *Usgrächnet Bünzen*. The original game allows users to test their
knowledge of Swiss locations in a playful manner. Each player can place location cards on a board
in relation to other locations based on their coordinates. All other players have the chance to doubt the 
card placement if they believe it is wrong. Players that continuously place cards correctly and prove other 
people's card placement to be wrong will be awarded tokens. The player with most tokens wins the game.
For a more extensive explanation of the original game please check out this **[manual](public/Usgrachnet_Help.pdf)**.


In order to introduce more complexity we added the following features:
* players can choose their **own game settings**
* players can **customize** the card decks to play with
* players can fetch international locations and create **new cards and decks**

Check out this project's [:computer: client repo](https://github.com/sopra-fs21-group-11/sopra-client).

## Technologies 

- **[WebSocket](https://stomp-js.github.io/stomp-websocket/)** 🧦: Enables bidirectional communication between client and server. We used this to implement the game. 

- **[React-js](https://reactjs.org/)** :rocket:: React-js is a framwork for building JavaScript user interfaces in a component-based way. 

- **[Spring Boot](https://spring.io/)** :boot:: Technology used for the backend. 

- [**JPA**](https://www.oracle.com/java/technologies/persistence-jsp.html) :floppy_disk:: for card / deck and user database

## High-level components

- **[Game Controller](https://github.com/sopra-fs21-group-11/sopra-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/controller/GameController.java)**: Handles all game related requests from the client.

- **[Socket Controller](https://github.com/sopra-fs21-group-11/sopra-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/controller/SocketController.java)**: This controller handles the core functionality of the game using websocket technology.

- **[Deck Controller](https://github.com/sopra-fs21-group-11/sopra-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs21/controller/DeckController.java)**: Provides the functionality which is the fundamental difference to the original game: to personalize and create new decks. 

Those three components are stronlgy interconnected as they provide the basic game functionality. While the game controller is used to create a game, the socket controller ensures a smooth game flow. The deck controller enables customization of the core component of the game without which could not be played. 

## Launch & Deployment - for joining developers

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/)) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

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

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

#### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## Roadmap
Joining developers can contribute the following things:

- Add more comparison types, i.e. compare population instead of coordinates
- Add profile pages for the users
- ... whatever creative extensions you can come up with! :smile:

## Authors and acknowledgment

A special thanks goes to our TA Raffi and the sopra team FS21.

### Authors
- [Martin](https://github.com/tinu0816)
- [Tobias](https://github.com/tobcode)
- [Lukas](https://github.com/LukZeh)
- [Tanzil](https://github.com/tanzilkm)
- [Debby](https://github.com/theDebbister)



## License

The project is licensed under the Apache License 2.0. For more information check [this :page_with_curl:](https://github.com/sopra-fs21-group-11/sopra-client/blob/master/LICENSE) out.

## Disclaimer on the extrnal API

Given that heroku terminates all requests that take longer than 30s, we were not able to fully exploit the usage of our external API. Fetching really large sets of places can in some cases take longer than 30s. We are aware of this but under the given circumstances were not able to resolve it.
