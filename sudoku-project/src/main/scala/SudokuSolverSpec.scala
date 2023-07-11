// import org.scalatest._
// import org.scalatest.flatspec.AnyFlatSpec
// import org.scalatest.matchers.should.Matchers

// import sudoku.Main._

// class SudokuSolverSpec extends FlatSpec with Matchers {

//   // Test case for a solvable Sudoku
//   "solve" should "correctly solve a valid Sudoku puzzle" in {
//     val grid = List(
//       List(Some(5), Some(3), None, None, Some(7), None, None, None, None),
//       List(Some(6), None, None, Some(1), Some(9), Some(5), None, None, None),
//       List(None, Some(9), Some(8), None, None, None, None, Some(6), None),
//       List(Some(8), None, None, None, Some(6), None, None, None, Some(3)),
//       List(Some(4), None, None, Some(8), None, Some(3), None, None, Some(1)),
//       List(Some(7), None, None, None, Some(2), None, None, None, Some(6)),
//       List(None, Some(6), None, None, None, None, Some(2), Some(8), None),
//       List(None, None, None, Some(4), Some(1), Some(9), None, None, Some(5)),
//       List(None, None, None, None, Some(8), None, None, Some(7), Some(9))
//     )

//     val solvedGrid = solve(grid)

//     val expectedSolution = List(
//       List(Some(5), Some(3), Some(4), Some(6), Some(7), Some(8), Some(9), Some(1), Some(2)),
//       List(Some(6), Some(7), Some(2), Some(1), Some(9), Some(5), Some(3), Some(4), Some(8)),
//       List(Some(1), Some(9), Some(8), Some(3), Some(4), Some(2), Some(5), Some(6), Some(7)),
//       List(Some(8), Some(5), Some(9), Some(7), Some(6), Some(1), Some(4), Some(2), Some(3)),
//       List(Some(4), Some(2), Some(6), Some(8), Some(5), Some(3), Some(7), Some(9), Some(1)),
//       List(Some(7), Some(1), Some(3), Some(9), Some(2), Some(4), Some(8), Some(5), Some(6)),
//       List(Some(9), Some(6), Some(1), Some(5), Some(3), Some(7), Some(2), Some(8), Some(4)),
//       List(Some(2), Some(8), Some(7), Some(4), Some(1), Some(9), Some(6), Some(3), Some(5)),
//       List(Some(3), Some(4), Some(5), Some(2), Some(8), Some(6), Some(1), Some(7), Some(9))
//     )

//     solvedGrid shouldEqual expectedSolution
//   }

//   // Test case for an unsolvable Sudoku
//   "it" should "return the original grid if the Sudoku puzzle is unsolvable" in {
//     val grid = List(
//       List(Some(5), Some(3), None, None, Some(7), None, None, None, None),
//       List(Some(6), None, None, Some(1), Some(9), Some(5), None, None, None),
//       List(None, Some(9), Some(8), None, None, None, None, Some(6), None),
//       List(Some(8), None, None, None, Some(6), None, None, None, Some(3)),
//       List(Some(4), None, None, Some(8), None, Some(3), None, None, Some(1)),
//       List(Some(7), None, None, None, Some(2), None, None, None, Some(6)),
//       List(None, Some(6), None, None, None, None, Some(2), Some(8), None),
//       List(None, None, None, Some(4), Some(1), Some(9), None, None, Some(5)),
//       List(None, None, None, None, Some(8), None, None, Some(7), Some(8))
//     )

//     val solvedGrid = solve(grid)

//     solvedGrid shouldEqual grid
//   }
// }
