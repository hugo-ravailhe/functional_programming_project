package sudoku

import scala.annotation.tailrec
import zio._
import zio.Console._
import zio.nio._
import zio.nio.channels._
import zio.nio.file.{Path, Files}
import zio.json._

import zio.nio.file._
import io.circe._
import io.circe.generic.semiauto._
import scala.io.Source
import sudoku.Main.validateEnv
import io.circe.parser._
import io.circe.generic.auto._
//import scala.compiletime.ops.int
//import scala.compiletime.ops.any
//import scala.io.Source

object Main extends ZIOAppDefault {

  type Board = List[List[Option[Int]]]

  case class SudokuBoard(grid: List[List[Option[Int]]])

  object SudokuBoard {
    implicit val decoder: JsonDecoder[SudokuBoard] =
      DeriveJsonDecoder.gen[SudokuBoard]
    implicit val encoder: JsonEncoder[SudokuBoard] =
      DeriveJsonEncoder.gen[SudokuBoard]
  }

  def extractSudokuFromFile(path: String): ZIO[Any, Nothing, String] = {
    ZIO.succeed(Source.fromFile(path).mkString)
  }

  def fillingBoard(stringBoard: String): Board = { // takes a board in string format and return a board in boardtype with the right value entered
    val convertedBoard = stringBoard.fromJson[SudokuBoard] // parsing of the json string into our custom main class sudokuboard
    convertedBoard match {
      case Right(i) =>
        val rawdata = i.grid.map(row =>
          row.map(value => if (value != None) value else None)
        ) // value in (1,2,3,4,5,6,7,8,9)  or List(1,2,3,4,5,6,7,8,9).contains(value)

        // Perform a simple validation to check if the grid has a valid size
        if (rawdata.length == 9 && rawdata.forall(_.length == 9)) {
          println("Successfully parsed and validated JSON input")
          rawdata
        } else {
          println("Invalid Sudoku grid")
          null
        }
      case Left(i) =>
        println(s"Error: $i")
        null
    }
  }


  /***
   * solve takes a grid in Board format and give it to solveRec
   * solveRec takes a Board, a starting row and column
   * it checks if we are still within the 9x9 Board then calls getValidValues on each None
   * */
  def solve(grid: Board): Board = {
    def solveRec(grid: Board, row: Int, column: Int): Option[Board] = {// it was supposed to be tail recursive but we failed
      if (row == 9)
        Some(grid) // Puzzle solved
      else {
        val nextRow = if (column == 8) row + 1 else row
        val nextColumn = (column + 1) % 9

        val cell = grid(row)(column)

        cell match {
          case Some(_) => 
            solveRec(grid, nextRow, nextColumn) // Skip filled cells
          case None =>
            val validValues = getValidValues(grid, row, column)
            validValues.view
              .flatMap(value =>
                solveRec(
                  grid.updated(row, grid(row).updated(column, Some(value))),
                  nextRow,
                  nextColumn
                )
              )
              .headOption //why not .head? bc it didn't work and we had errors
        }
      }
    }

    solveRec(grid, 0, 0).getOrElse(grid) // so in order to return the proper type Board we getOrElse
  }

  /**
   * for a given cell (row,column) we want to check in a range of 9 included
   * using diff, what numbers can be allowed considering rows, columns, and others subgrids
   * and we take the first value that is return (cf voir au dessus)
  */
  def getValidValues(grid: Board, row: Int, column: Int): Seq[Int] = {
    val usedInRow = grid(row).flatten
    val usedInColumn = grid.map(row => row(column)).flatten
    val startRow = (row / 3) * 3
    val startColumn = (column / 3) * 3
    val usedInSubgrid = for {
      r <- startRow until startRow + 3
      c <- startColumn until startColumn + 3
      value <- grid(r)(c)
    } yield value

    (1 to 9).diff(usedInRow ++ usedInColumn ++ usedInSubgrid)
  }

  def printSudoku(sudokuSolved: Board): ZIO[Any, Throwable, Unit] = {
    val sudokuString = sudokuSolved
      .grouped(3)
      .map { bigGroup =>
        bigGroup
          .map { row =>
            row
              .grouped(3)
              .map(_.map {
                case Some(0)     => "_"
                case Some(value) => value.toString
                case None        => "_"
              }.mkString(" ", " ", " "))
              .mkString("| ", " | ", " | ")
          }
          .mkString("\n")
      }
      .mkString(
        "+-----------------------------+\n",
        "\n+-----------------------------+\n",
        "\n+-----------------------------+"
      )
    Console.printLine(sudokuString)
  }

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print(
        "Enter the path to the JSON file containing the Sudoku problem:"
      )
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      json <- extractSudokuFromFile(path)
      _ <- Console.print(json)
      grid = fillingBoard(json)
      _ <- Console.printLine(s"Sudoku to solve:")
      _ <- printSudoku(grid)
      _ <- Console.printLine(s"Resolving problem...")
      solvedGrid = solve(grid)
      _ <- Console.printLine(s"Sudoku solved:")
      _ <- printSudoku(solvedGrid)
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console

    } yield ()
}
