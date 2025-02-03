1.**Object-Oriented Design**
The game is structured using OOP principles, such as Encapsulation, Abstraction, Inheritance, and Polymorphism, Gui & Jdbc.

2.**Main Classes:**
Game - Manages the overall flow of the game.
Player - Represents a player with attributes like name and symbol (X or O).
Board - Handles the grid, moves, and win checks.
DatabaseManager - Uses JDBC to store and retrieve player records (wins, losses).
2. JDBC for Database Connectivity
A MySQL database (or any other RDBMS) is used to store player statistics.
The DatabaseManager class connects to the database, updates scores, and retrieves player history.


**3. Implementation Flow**
Best Of three Gameplay.
The system loads their past records and saves them in the database.
The game starts, alternating turns between players.
The board updates based on moves.
After a win/draw, the database updates the records.
The game can restart or exit.
