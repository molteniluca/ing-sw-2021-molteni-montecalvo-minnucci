# Final Software Engineering Exam, AY 2020/2021
![alt text](src/main/resources/images/Masters-of-Renaissance.png)


The final exam consists in the implementation of the game board of [Masters of Renaissance](http://www.craniocreations.it/prodotto/masters-of-renaissance/). 
The project involves the use of a client-server architecture with a single central server in 
which the clients can connect via socket. The project in its entirety uses the MCV architecture 
(Model-View-Controller).

## Functionalities
Features that have been implemented:
- Complete rules of the game.
- CLI
- GUI
- Socket
- 1 Advanced functionality (FA):
  - **Multiple games**: server can support more than one game at the same time.
    When they log in, the player can choose either to join a game by entering 
    the specific ID or to create a new one. In the second case, it will be asked 
    the numbers of players, and he will receive an ID, which has to be entered by 
    the players who want to join. The game starts only when all the players are connected. 

- In the game is possible to see other player boards just like in the real board game.

## Tools
Different software tools have been used in the project making:

|Software|Description|
|-------------|-----|
|IntelliJ IDEA| IDE written in Java developed by JetBrain|
|[Apache Maven](https://maven.apache.org)|Java-based build automation and software project management tool|
|JavaFX|Graphic library used by Java for the creation of user interfaces|
|JUnit|Unit testing Framework (built into IntelliJ)|


## Starting the game
In order to use the game correctly, java versions 8.x or higher are recommended.

### Client
a client can be launched either in GUI (Graphical User Interface) or CLI 
(Command Line Interface) mode. To launch the client through the terminal 
it is necessary to reach the folder which contains the jar and type:
``` 
java -jar Client.jar
```
Mode GUI will be selected automatically. To launch the command line 
interface it is necessary to enter a specific parameter:
```
java -jar Client.jar --cli
```
A client can also be launched by clicking two times on 
the application icon, in that case, it will be launched in GUI mode.
### Server
The server does not own any graphic interface or command line. 
To launch it, it is necessary to reach the folder which contains the jar and use the command:
```
java -jar Server.jar <portNumber>
```
The program will be launched and will be followed in the background on the selected door.
``portNumber = 10000`` will be selected by default. If the parameters supplied are incorrect,
the server won’t start, and it will show an error alert which suggests using the -h parameter to ask for assistance.

## Group members
- **[Luca Molteni](https://github.com/molteniluca)**
- **[Manuel Montecalvo](https://github.com/ManuelMontecalvo)**
- **[Francesco Minnucci](https://github.com/FrancescoMinnucci)**
