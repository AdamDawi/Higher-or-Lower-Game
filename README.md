# <img src="https://github.com/AdamDawi/Real-chat-room/assets/49430055/edb8c16b-2d11-4f68-89fe-71cbe5c47182" width="60" height="60" align="center" /> Higher-or-Lower-Game
Higher or lower is a game of guessing which one of two selected countries has the larger population. Your goal is to achieve the highest score possible by choosing the right answers.

### Getting Started
To start the game for the first time, when application opens, you need an internet connection. Otherwise the application will not connect to the API. The next games do not require the Internet because API data is saved in the cache.
After the game ends with a result higher than zero, it is automatically saved to the leaderboard.

### How to play
1. You select two options from the list of available countries.
2. You choose which one of this countries has the larger population.
4. The game shows whether you answered correctly.
5. If your answer was correct, the countries move one place up.
6. After each round, there will be one new country to choose from.
7. The game continues until you run out of available countries or you loose.

### Technologies Used
The application is built using the following technologies:
- Room database(SQLite database for storing leaderboard)
- Android Studio
- .xml files(layouts)
- rest API(photos of flags, countries names and number of population)
- retrofit2(fast http connection)
- gson(converts json to other formats)
- glide(loading images from the Internet)

### Here are some pictures of the application:

![menu2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/fa2bedca-4660-4da6-89d9-0ec6877480ec)
![game2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/320f673d-3074-4fc8-a0e4-7c446f725d5f)

![result2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/e02dffc6-3c16-4172-817a-0072679ba665)
![leaderboard2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/8a76ca5d-1a13-4fd2-8edf-2aa7d9fb556b)

## Game:
!["Game"](gifs/game.gif)
