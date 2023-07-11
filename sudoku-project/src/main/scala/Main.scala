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
  // type Position = (Int, Int)
  // type SudokuData = (IO[String, Sudoku], Grid)

  /*object SudokuGrid {
    implicit val encoder: Encoder[Sudoku] = deriveEncoder[Sudoku]
    implicit val decoder: Decoder[Sudoku] = deriveDecoder[Sudoku]
  }*/

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

  def fillingBoard(stringBoard: String): Board = { // board
    val convertedBoard = stringBoard.fromJson[SudokuBoard] // board
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

  /*def fillingBoard(stringBoard: String): List[List[Option[Int]]] = {
    decode[List[List[Int]]](stringBoard) match {
      case Right(board) =>
        val convertedBoard = board.map(row => row.map(value => if (value >= 1 && value <= 9) Some(value) else None))

        // Perform a simple validation to check if the grid has a valid size
        if (convertedBoard.length == 9 && convertedBoard.forall(_.length == 9)) {
          println("Successfully parsed and validated JSON input")
          convertedBoard
        } else {
          println("Invalid Sudoku grid")
          List.empty[List[Option[Int]]]
        }
      case Left(error) =>
        println(s"Error: $error")
        List.empty[List[Option[Int]]]
    }
  }*/

  /*def parseSudoku(jsonString: String): ZIO[Any, Throwable, Array[Array[Option[Int]]]] =
    jsonString.fromJson[Map[String, Array[Array[Option[Int]]]]] match {
      case Left(error) => ZIO.fail(new RuntimeException(error))
      case Right(data) =>
        ZIO
          .fromOption(data.get("grid"))
          .orElse(ZIO.succeed(Array.empty[Array[Option[Int]]]))
    }*/

  def solve(grid: Board): Board = {
    def solveRec(grid: Board, row: Int, column: Int): Option[Board] = {
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
              .headOption
        }
      }
    }

    solveRec(grid, 0, 0).getOrElse(grid)
  }

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

  /* Read the JSON file and return the parsed Sudoku data along with the raw grid
  def readSudokuFromJson(path: String): SudokuData = {
    val file = new File(path)
    if (!file.exists() || !file.isFile) { // handle invalid file
      println("Invalid file path")
      return null
    }

    val fileContent = Source.fromFile(file).mkString
    println(s"$fileContent \n")

    val sudoku = fileContent.fromJson[Sudoku]

    val jsonIO = ZIO.fromEither(fileContent.fromJson[Sudoku])

    sudoku match {
      case Right(i) =>
        val rawdata = i.RawData.map(row => row.map(value => if (value == 0) None else Some(value)))

        // Perform a simple validation to check if the grid has a valid size
        if (rawdata.length == 9 && rawdata.forall(_.length == 9)) {
          println("Successfully parsed and validated JSON input")
          (jsonIO, rawdata)
        } else {
          println("Invalid Sudoku grid")
          null
        }
      case Left(i) =>
        println(s"Error: $i")
        null
    }
  }


  //objet cell
  case class SudokuCell(row: Int, column: Int, value: Option[Int])

  //la grille
  class SudokuGrid(private val cells: Array[Array[SudokuCell]]) {
    def getCell(row: Int, column: Int): SudokuCell = cells(row)(column)

    def updateCell(row: Int, column: Int, value: Option[Int]): SudokuGrid = {
      val updatedCells = cells.updated(row, cells(row).updated(column, SudokuCell(row, column, value)))
      new SudokuGrid(updatedCells)
    }

    override def toString: String = cells.map(_.map(_.value.getOrElse(" ")).mkString(" ")).mkString("\n")
  }


  object SudokuGrid {
    def fromProblem(problem: SudokuProblem): SudokuGrid = {
      val cells = problem.grid.zipWithIndex.flatMap {
        case (row, rowIndex) =>
          row.zipWithIndex.map {
            case (value, columnIndex) => SudokuCell(rowIndex, columnIndex, value)
          }
      }
      new SudokuGrid(cells.grouped(9).toArray)
    }
  }*/

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

/*
case class SudokuProblem(grid: Array[Array[Option[Int]]])

object SudokuProblem {
  implicit val decoder: JsonDecoder[SudokuProblem] = DeriveJsonDecoder.gen[SudokuProblem]
}

def parseJsonFile(filePath: String): IO[Throwable, SudokuGrid] = {
  for {
    content <- json.fromJson(readJsonFile(filePath))
    problem <- ZIO.fromEither(content.fromJson[SudokuProblem])
  } yield SudokuGrid.fromProblem(problem)
}

def readJsonFile(filePath: String): Either[Throwable, String] = {
  val file = FileSystems.getDefault().getPath(filePath)
  val read = for {
    channel <- AsynchronousFileChannel.open(file, StandardOpenOption.READ)
    size <- channel.size()
    buffer <- ByteBuffer.allocate(size.toInt)
    _ <- channel.read(buffer, 0)
    _ <- channel.close()
    _ <- buffer.flip()
    content = new String(buffer.array())
  } yield content

  ZIO.fromCompletionStage(read.toCompletableFuture()).either.map(_.flatten)
}
def parseJsonFile(filePath: String): IO[Throwable, SudokuGrid] = {
  for {
    content <- ZIO.fromEither(readJsonFile(filePath))
    problem <- ZIO.fromEither(decode[SudokuProblem](content))
  } yield SudokuGrid.fromProblem(problem)
}

def readJsonFile(filePath: String): Either[Throwable, String] = {
  val path = Path.of(filePath)
  if (Files.exists(path) && Files.isRegularFile(path)) {
    val fileContent = Files.readString(path)
    Right(fileContent)
  } else {
    Left(new IllegalArgumentException("Invalid file path"))
  }
}*/
