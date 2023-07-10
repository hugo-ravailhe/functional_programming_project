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

//import scala.compiletime.ops.int
//import scala.compiletime.ops.any
//import scala.io.Source

object Main extends ZIOAppDefault {

  case class Sudoku(cells: List[List[Option[Int]]])

  type Board = List[List[Option[Int]]]
  type Position = (Int, Int)
  type SudokuData = (IO[String, Sudoku], Grid)

  object SudokuGrid {
    implicit val encoder: Encoder[Sudoku] = deriveEncoder[Sudoku]
    implicit val decoder: Decoder[Sudoku] = deriveDecoder[Sudoku]
  }


  //what ever
  def extractSudokuFromFile(filename: String): Task[Sudoku] = {
    ZIO
      .effect(Files.readAllBytes(Paths.get(filename)))
      .flatMap(bytes => ZIO.fromEither(bytes.fromJson[Sudoku]))
      .mapError(ex => new RuntimeException(s"Failed to extract Sudoku from file: $filename", ex))
  }

  def printSudoku(grid: Sudoku): Task[Unit] = {
    ZIO.foreach_(grid.cells) { row =>
      ZIO.foreach_(row) { cell =>
        putStr(s"$cell ")
      } *> putStrLn("")
    }
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
  }

  def solve(grid: SudokuGrid): Option[SudokuGrid] = {
    @tailrec
    def solveHelper(grid: SudokuGrid, row: Int, column: Int): Option[SudokuGrid] = {
      if (row == 9)
        Some(grid) // Puzzle solved
      else {
        val nextRow = if (column == 8) row + 1 else row
        val nextColumn = (column + 1) % 9

        val cell = grid.getCell(row, column)

        cell.value match {
          case Some(_) => solveHelper(grid, nextRow, nextColumn) // Skip filled cells
          case None =>
            val validValues = getValidValues(grid, cell)
            validValues.view
              .flatMap(value => solveHelper(grid.updateCell(row, column, Some(value)), nextRow, nextColumn))
              .headOption
        }
      }
    }

    solveHelper(grid, 0, 0)
  }

  def getValidValues(grid: SudokuGrid, cell: SudokuCell): Seq[Int] = {
    val usedInRow = grid.cells(cell.row).flatMap(_.value)
    val usedInColumn = grid.cells.map(_(cell.column).value).flatten
    val usedInSubgrid = {
      val startRow = (cell.row / 3) * 3
      val startColumn = (cell.column / 3) * 3
      for {
        row <- startRow until startRow + 3
        column <- startColumn until startColumn + 3
        value <- grid.cells(row)(column).value
      } yield value
    }
  }*/

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      sudoku <- extractSudokuFromFile(filePath)
      _ <- printSudoku(sudoku)
      _ <-  Console.printLine(s"You entered: $path")

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