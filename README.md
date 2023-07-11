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
sbt update
```
```console
sbt compile
```
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

## Backtracking algorithm

Backtracking is an algorithmic technique that is widely used to solve problems by systematically exploring potential solutions. It is particularly effective when dealing with problems that have a large solution space and require making a series of choices.
At its core, backtracking involves a recursive approach to problem-solving. The algorithm starts by making an initial choice and then explores the consequences of that choice. If the choice leads to a valid solution, the algorithm continues to the next step. However, if the choice leads to an invalid or unsatisfactory solution, the algorithm backtracks to the previous step and tries an alternative choice.

## Our choices

### List
The data in the algorithm is structured mainly using the Board type, which is a list of lists of Option[Int]. This type represents the Sudoku grid, where each element in the outer list represents a row of the grid, and each element in the inner list represents an individual cell.

We decided to use lists instead of arrays for several reasons. Firstly, lists provide a more flexible and dynamic approach to handle the data in our algorithm. With lists, we can easily add or remove elements without worrying about resizing or copying the entire data structure. This flexibility was important for us as we needed to manipulate and update the Sudoku grid during the solving process.

Another reason for choosing lists is their immutability. By default, lists in Scala are immutable, meaning their elements cannot be modified once created. This immutability aligns well with our functional programming approach, as it ensures safer concurrent programming and eliminates the risk of accidental modifications. Immutability also allows us to reason about our code more easily and avoid unexpected side effects.

### JSON
We use the zio-json library for parsing and encoding JSON data in our Sudoku solver algorithm.
We define implicit decoders and encoders for the SudokuBoard case class using zio-json's DeriveJsonDecoder.gen and DeriveJsonEncoder.gen macros. These implicit instances enable the conversion between JSON and the SudokuBoard case class.
This function takes a string representation of a Sudoku board in JSON format and converts it into a Board type, which is a List[List[Option[Int]]]. It utilizes the fromJson method provided by zio-json to parse the JSON string into an instance of SudokuBoard, applying the implicit decoder.

### ZIO Console Interaction
The solver interacts with the ZIO Console by requesting the user to provide a JSON file path containing a Sudoku problem. This input is then used to extract the Sudoku data and initiate the solving process. The solved Sudoku grid is displayed to the user through the console.


### Test
We made an attempt to write tests for our Sudoku solver, but encountered some difficulties that prevented us from successfully implementing them.
