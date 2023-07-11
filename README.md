# Sudoku Solver project

This project is a Sudoku solver written in Scala. It allows solving Sudoku grids using a recursive approach based on backtracking algorithms.

## Developped by:
- LISZEWSKI Benjamin
- MABECQUE Julien
- RAVAILHE Hugo
- TRAN Victor (2 pseudo hein mdr)

## Features
* Reading a JSON file containing a Sudoku grid.
* Validation of the Sudoku grid.
* Solving the Sudoku grid.
* Displaying the solved Sudoku grid.

## Installation

Clone the Git repository or download the project files.
```console
git clone https://github.com/hugo-ravailhe/functional_programming_project.git
```

Navigate to the project directory.
```console
cd sudoku-project
```

## Run the project

Run the application with sbt.
```console
sbt run
```

The application will prompt you for the path to the JSON file containing the Sudoku grid. Enter the full file path and press Enter.
```console
Enter the path to the JSON file containing the Sudoku problem: /path/to/sudoku.json
```

## JSON Structure

The main key is "grid", which contains a list of nine sublists. Each sublist represents a row in the Sudoku grid. Each element within the sublist can either be a value from 1 to 9 representing a digit in the grid or the value null indicating an empty cell.

Here is an exemple of the JSON structure.
```JSON
{
  "grid": [
    [null, null, null, 2, 6, null, 7, null, 1],
    [6, 8, null, null, 7, null, null, 9, null],
    [1, 9, null, null, null, 4, 5, null, null],
    [8, 2, null, 1, null, null, null, 4, null],
    [null, null, 4, 6, null, 2, 9, null, null],
    [null, 5, null, null, null, 3, null, 2, 8],
    [null, null, 9, 3, null, null, null, 7, 4],
    [null, 4, null, null, 5, null, null, 3, 6],
    [7, null, 3, null, 1, 8, null, null, null]
  ]
}
```
