# Higher-or-Lower-Game
Higher or lower is a game of guessing which of two selected countries has the larger population. Your goal is to achieve the highest score possible by hitting the right answers.

### Getting Started
To start the game for the first time when application opens, you need an internet connection, otherwise the application will not connect to the API. The next games do not require the Internet because API data is saved in the cache.
After game ends a result greater than zero, it is automatically saved to the leaderboard.

### How to play
1. You select two options from the list of available countries.
2. You choose which of these countries has the larger population.
4. The game shows whether you answered correctly.
5. If your answer was correct, the countries move up one.
6. After each round, there will be one new country to choose from.
7. The game continues until you run out of available countries or you lose.

### Technologies Used
The application is built using the following technologies:
- Room database(SQLite database for storing leaderboard)
- Android Studio
- .xml files(layouts)
- rest API(photos of flags, countries names and number of population)
- retrofit2(fast http connection)
- gson(converts json to other formats)
- glide(loading images from the Internet)

### Here are some pictures of the mobile application:

![menu2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/25ac2a5b-bd00-457a-9ef4-bad59f3a86ce)
![game2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/44c22c9b-0d91-427d-a204-7b73b980f165)
![result2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/14798e8e-08af-44e7-95a4-4dc4c747d139)
![leaderboard2](https://github.com/AdamDawi/Higher-or-Lower-Game/assets/49430055/9cf6f86d-5686-4cdd-b296-dc407d78a067)
