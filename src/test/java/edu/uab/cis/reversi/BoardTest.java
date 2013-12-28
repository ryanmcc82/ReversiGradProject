package edu.uab.cis.reversi;

import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testInitialDefaultSize() {
    Board board = new Board();
    Assert.assertEquals(8, board.size());
    Assert.assertEquals(Player.WHITE, board.getOwner(3, 3));
    Assert.assertEquals(Player.WHITE, board.getOwner(4, 4));
    Assert.assertEquals(Player.BLACK, board.getOwner(3, 4));
    Assert.assertEquals(Player.BLACK, board.getOwner(4, 3));
    Assert.assertEquals(
        "________\n________\n________\n___WB___\n___BW___\n________\n________\n________\n",
        board.toString());
  }

  @Test
  public void testInitialSize4() {
    Board board = new Board(4);
    Assert.assertEquals(4, board.size());
    Assert.assertEquals(Player.WHITE, board.getOwner(1, 1));
    Assert.assertEquals(Player.WHITE, board.getOwner(2, 2));
    Assert.assertEquals(Player.BLACK, board.getOwner(1, 2));
    Assert.assertEquals(Player.BLACK, board.getOwner(2, 1));
    Assert.assertEquals("____\n_WB_\n_BW_\n____\n", board.toString());
  }

  @Test
  public void testEqualityAndHashing() {
    Board board1 = new Board();
    Board board2 = new Board();
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());

    board1 = board1.addMove(2, 3);
    Assert.assertNotEquals(board1, board2);
    Assert.assertNotEquals(board1.hashCode(), board2.hashCode());

    board2 = board2.addMove(2, 3);
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToReplaceStartingPiece() {
    Board board = new Board();
    board.addMove(3, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonAdjacent() {
    Board board = new Board();
    board.addMove(6, 6);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonCapturing() {
    Board board = new Board();
    board.addMove(5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPassWhenMovesPossible() {
    Board board = new Board();
    board.pass();
  }

  @Test
  public void testHasMoreMoves() {
    Board board = new Board(4);
    Assert.assertTrue(board.hasMoreMoves());
    Assert.assertEquals("____\n_WB_\n_BW_\n____\n", board.toString());
    board = board.addMove(1, 0);
    Assert.assertEquals("____\nBBB_\n_BW_\n____\n", board.toString());
    board = board.addMove(0, 0);
    Assert.assertEquals("W___\nBWB_\n_BW_\n____\n", board.toString());
    board = board.addMove(3, 2);
    Assert.assertEquals("W___\nBWB_\n_BB_\n__B_\n", board.toString());
    board = board.addMove(3, 3);
    Assert.assertEquals("W___\nBWB_\n_BW_\n__BW\n", board.toString());
    board = board.addMove(2, 3);
    Assert.assertEquals("W___\nBWB_\n_BBB\n__BW\n", board.toString());
    board = board.addMove(3, 1);
    Assert.assertEquals("W___\nBWB_\n_WBB\n_WWW\n", board.toString());
    board = board.addMove(3, 0);
    Assert.assertEquals("W___\nBWB_\n_BBB\nBWWW\n", board.toString());
    board = board.addMove(1, 3);
    Assert.assertEquals("W___\nBWWW\n_BWW\nBWWW\n", board.toString());
    board = board.addMove(0, 3);
    Assert.assertEquals("W__B\nBWBW\n_BWW\nBWWW\n", board.toString());
    board = board.addMove(2, 0);
    Assert.assertEquals("W__B\nWWBW\nWWWW\nBWWW\n", board.toString());
    Assert.assertTrue(board.hasMoreMoves());
    board = board.pass();
    Assert.assertEquals("W__B\nWWBW\nWWWW\nBWWW\n", board.toString());
    Assert.assertTrue(board.hasMoreMoves());

    // end by completely filling the board
    Board completeBoard = board.addMove(0, 2);
    Assert.assertEquals("W_WB\nWWWW\nWWWW\nBWWW\n", completeBoard.toString());
    completeBoard = completeBoard.addMove(0, 1);
    Assert.assertEquals("WBBB\nWWWW\nWWWW\nBWWW\n", completeBoard.toString());
    Assert.assertFalse(completeBoard.hasMoreMoves());

    // end without completely filling the board
    Board incompleteBoard = board.addMove(0, 1);
    Assert.assertEquals("WW_B\nWWWW\nWWWW\nBWWW\n", incompleteBoard.toString());
    Assert.assertFalse(incompleteBoard.hasMoreMoves());
  }
}
