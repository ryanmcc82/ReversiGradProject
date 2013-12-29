package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testInitialDefaultSize() {
    Board board = new Board();
    Map<Square, Player> owners = board.getSquareOwners();
    Assert.assertEquals(8, board.size());
    Assert.assertEquals(Player.WHITE, owners.get(new Square(3, 3)));
    Assert.assertEquals(Player.WHITE, owners.get(new Square(4, 4)));
    Assert.assertEquals(Player.BLACK, owners.get(new Square(3, 4)));
    Assert.assertEquals(Player.BLACK, owners.get(new Square(4, 3)));
    Assert.assertEquals(
        "________\n________\n________\n___WB___\n___BW___\n________\n________\n________\n",
        board.toString());
  }

  @Test
  public void testInitialSize4() {
    Board board = new Board(4);
    Map<Square, Player> owners = board.getSquareOwners();
    Assert.assertEquals(4, board.size());
    Assert.assertEquals(Player.WHITE, owners.get(new Square(1, 1)));
    Assert.assertEquals(Player.WHITE, owners.get(new Square(2, 2)));
    Assert.assertEquals(Player.BLACK, owners.get(new Square(1, 2)));
    Assert.assertEquals(Player.BLACK, owners.get(new Square(2, 1)));
    Assert.assertEquals("____\n_WB_\n_BW_\n____\n", board.toString());
  }

  @Test
  public void testEqualityAndHashing() {
    Board board1 = new Board();
    Board board2 = new Board();
    Board board3 = new Board(4);
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());
    Assert.assertNotEquals(board1, board3);
    Assert.assertNotEquals(board2, board3);
    Assert.assertNotEquals(board1, null);

    board1 = board1.play(new Square(2, 3));
    Assert.assertNotEquals(board1, board2);
    Assert.assertNotEquals(board1.hashCode(), board2.hashCode());

    board2 = board2.play(new Square(2, 3));
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());

  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToHaveSmallSize() {
    Board board = new Board(2);
    board.toString();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToHaveOddSize() {
    Board board = new Board(3);
    board.toString();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToReplaceStartingPiece() {
    Board board = new Board();
    board.play(new Square(3, 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonAdjacent() {
    Board board = new Board();
    board.play(new Square(6, 6));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonCapturing() {
    Board board = new Board();
    board.play(new Square(5, 5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPassWhenMovesPossible() {
    Board board = new Board();
    board.pass();
  }

  @Test
  public void testIsComplete() {
    Board board = new Board(4);
    Assert.assertFalse(board.isComplete());
    Assert.assertEquals("____\n_WB_\n_BW_\n____\n", board.toString());
    board = board.play(new Square(1, 0));
    Assert.assertEquals("____\nBBB_\n_BW_\n____\n", board.toString());
    board = board.play(new Square(0, 0));
    Assert.assertEquals("W___\nBWB_\n_BW_\n____\n", board.toString());
    board = board.play(new Square(3, 2));
    Assert.assertEquals("W___\nBWB_\n_BB_\n__B_\n", board.toString());
    board = board.play(new Square(3, 3));
    Assert.assertEquals("W___\nBWB_\n_BW_\n__BW\n", board.toString());
    board = board.play(new Square(2, 3));
    Assert.assertEquals("W___\nBWB_\n_BBB\n__BW\n", board.toString());
    board = board.play(new Square(3, 1));
    Assert.assertEquals("W___\nBWB_\n_WBB\n_WWW\n", board.toString());
    board = board.play(new Square(3, 0));
    Assert.assertEquals("W___\nBWB_\n_BBB\nBWWW\n", board.toString());
    board = board.play(new Square(1, 3));
    Assert.assertEquals("W___\nBWWW\n_BWW\nBWWW\n", board.toString());
    board = board.play(new Square(0, 3));
    Assert.assertEquals("W__B\nBWBW\n_BWW\nBWWW\n", board.toString());
    board = board.play(new Square(2, 0));
    Assert.assertEquals("W__B\nWWBW\nWWWW\nBWWW\n", board.toString());
    Assert.assertFalse(board.isComplete());
    board = board.pass();
    Assert.assertEquals("W__B\nWWBW\nWWWW\nBWWW\n", board.toString());
    Assert.assertFalse(board.isComplete());

    // end by completely filling the board
    Board completeBoard = board.play(new Square(0, 2));
    Assert.assertEquals("W_WB\nWWWW\nWWWW\nBWWW\n", completeBoard.toString());
    completeBoard = completeBoard.play(new Square(0, 1));
    Assert.assertEquals("WBBB\nWWWW\nWWWW\nBWWW\n", completeBoard.toString());
    Assert.assertTrue(completeBoard.isComplete());

    // end without completely filling the board
    Board incompleteBoard = board.play(new Square(0, 1));
    Assert.assertEquals("WW_B\nWWWW\nWWWW\nBWWW\n", incompleteBoard.toString());
    Assert.assertTrue(incompleteBoard.isComplete());
  }

  @Test
  public void testGetPlayerSquareCounts() {
    Board board = new Board(4);
    Map<Player, Integer> expected = new HashMap<>();
    expected.put(Player.BLACK, 2);
    expected.put(Player.WHITE, 2);
    Assert.assertEquals(expected, board.getPlayerSquareCounts());

    board = board.play(new Square(1, 0));
    Assert.assertEquals("____\nBBB_\n_BW_\n____\n", board.toString());
    expected.put(Player.BLACK, 4);
    expected.put(Player.WHITE, 1);
    Assert.assertEquals(expected, board.getPlayerSquareCounts());

    board = board.play(new Square(0, 0));
    Assert.assertEquals("W___\nBWB_\n_BW_\n____\n", board.toString());
    expected.put(Player.BLACK, 3);
    expected.put(Player.WHITE, 3);
    Assert.assertEquals(expected, board.getPlayerSquareCounts());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWinnerIncompleteBoard() {
    Board board = new Board();
    board.getWinner();
  }

  @Test
  public void testGetWinner() {
    Board board = new Board(4);
    board = board.play(new Square(1, 0));
    board = board.play(new Square(0, 0));
    board = board.play(new Square(3, 2));
    board = board.play(new Square(3, 3));
    board = board.play(new Square(2, 3));
    board = board.play(new Square(3, 1));
    board = board.play(new Square(3, 0));
    board = board.play(new Square(1, 3));
    board = board.play(new Square(0, 3));
    board = board.play(new Square(2, 0));
    board = board.pass();
    Assert.assertEquals("W__B\nWWBW\nWWWW\nBWWW\n", board.toString());

    // end by completely filling the board
    Board completeBoard = board.play(new Square(0, 2));
    completeBoard = completeBoard.play(new Square(0, 1));
    Assert.assertEquals("WBBB\nWWWW\nWWWW\nBWWW\n", completeBoard.toString());
    Assert.assertEquals(Player.WHITE, completeBoard.getWinner());

    // end without completely filling the board
    Board incompleteBoard = board.play(new Square(0, 1));
    Assert.assertEquals("WW_B\nWWWW\nWWWW\nBWWW\n", incompleteBoard.toString());
    Assert.assertEquals(Player.WHITE, incompleteBoard.getWinner());

  }

  @Test
  public void testGetWinnerTie() {
    Board board = new Board(4);
    board = board.play(new Square(1, 0));
    Assert.assertEquals("____\nBBB_\n_BW_\n____\n", board.toString());
    board = board.play(new Square(0, 2));
    Assert.assertEquals("__W_\nBBW_\n_BW_\n____\n", board.toString());
    board = board.play(new Square(2, 3));
    Assert.assertEquals("__W_\nBBW_\n_BBB\n____\n", board.toString());
    board = board.play(new Square(3, 2));
    Assert.assertEquals("__W_\nBBW_\n_BWB\n__W_\n", board.toString());
    board = board.play(new Square(0, 1));
    Assert.assertEquals("_BW_\nBBB_\n_BWB\n__W_\n", board.toString());
    board = board.play(new Square(0, 0));
    Assert.assertEquals("WWW_\nBWB_\n_BWB\n__W_\n", board.toString());
    board = board.pass();
    Assert.assertEquals("WWW_\nBWB_\n_BWB\n__W_\n", board.toString());
    board = board.play(new Square(1, 3));
    Assert.assertEquals("WWW_\nBWWW\n_BWB\n__W_\n", board.toString());
    board = board.play(new Square(0, 3));
    Assert.assertEquals("WWWB\nBWBB\n_BWB\n__W_\n", board.toString());
    board = board.play(new Square(2, 0));
    Assert.assertEquals("WWWB\nWWBB\nWWWB\n__W_\n", board.toString());
    board = board.play(new Square(3, 0));
    Assert.assertEquals("WWWB\nWWBB\nWBWB\nB_W_\n", board.toString());
    board = board.play(new Square(3, 1));
    Assert.assertEquals("WWWB\nWWBB\nWWWB\nBWW_\n", board.toString());
    board = board.play(new Square(3, 3));
    Assert.assertEquals("WWWB\nWWBB\nWWWB\nBBBB\n", board.toString());

    Assert.assertNull(board.getWinner());
  }
}
