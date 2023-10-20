This is a simple Android calculator application with the following features:
·Calculate common mathematical expressions
·Display the last 10 calculation results
·Provide scientific calculator functions, such as factorial, square root, trigonometric functions, etc.
·Store historical calculation results in an SQLite database

Key Features
·Basic Calculation: Users can input mathematical expressions, and the program will parse and calculate the result.
·History Records: Users can view the last 10 calculation results for historical reference.
·Scientific Calculations: Support scientific calculator functions like factorial, square root, trigonometric functions, and more.
·Database Storage: Calculation results are automatically saved in an SQLite database for future retrieval.
·Clear History: Users can clear the historical records, deleting all results from the database.

Usage Example
·Open the application and input a mathematical expression.
·Click the "=" button to calculate the result, which will be displayed on the screen.
·Click the "History" button to view recent calculation results.
·To clear the history, click the "Clear History" button.

Code Structure
·MainActivity.java: Contains the main calculator functionality and user interface.
·MySqliteHelper.java: An auxiliary class for managing the SQLite database.
·ShowActivity.java: An activity responsible for displaying historical calculation results.

How to Run
·Clone the repository to your local machine.
·Open the project using Android Studio or another compatible IDE.
·Run the application on an emulator or a connected Android device.

Technology Stack
Android Development
SQLite Database Management
