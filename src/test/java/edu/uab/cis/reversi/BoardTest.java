package edu.uab.cis.reversi;

import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testInitialDefaultSize() {
    Board board = new Board();
    Assert.assertEquals(8, board.size());
    Assert.assertEquals(Player.WHITE, board.getPlayer(3, 3));
    Assert.assertEquals(Player.WHITE, board.getPlayer(4, 4));
    Assert.assertEquals(Player.BLACK, board.getPlayer(3, 4));
    Assert.assertEquals(Player.BLACK, board.getPlayer(4, 3));
    Assert.assertEquals(
        "________\n________\n________\n___WB___\n___BW___\n________\n________\n________\n",
        board.toString());
  }

  @Test
  public void testInitialSize4() {
    Board board = new Board(4);
    Assert.assertEquals(4, board.size());
    Assert.assertEquals(Player.WHITE, board.getPlayer(1, 1));
    Assert.assertEquals(Player.WHITE, board.getPlayer(2, 2));
    Assert.assertEquals(Player.BLACK, board.getPlayer(1, 2));
    Assert.assertEquals(Player.BLACK, board.getPlayer(2, 1));
    Assert.assertEquals("____\n_WB_\n_BW_\n____\n", board.toString());
  }

  @Test
  public void testEqualityAndHashing() {
    Board board1 = new Board();
    Board board2 = new Board();
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());

    board1 = board1.addMove(2, 3, Player.BLACK);
    Assert.assertNotEquals(board1, board2);
    Assert.assertNotEquals(board1.hashCode(), board2.hashCode());

    board2 = board2.addMove(2, 3, Player.BLACK);
    Assert.assertEquals(board1, board2);
    Assert.assertEquals(board1.hashCode(), board2.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToReplaceStartingPieceWithSamePlayer() {
    Board board = new Board();
    board.addMove(3, 3, Player.WHITE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToReplaceStartingPieceWithDifferentPlayer() {
    Board board = new Board();
    board.addMove(4, 3, Player.WHITE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonAdjacent() {
    Board board = new Board();
    board.addMove(6, 6, Player.BLACK);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalToPlaceNonCapturing() {
    Board board = new Board();
    board.addMove(5, 5, Player.BLACK);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalForWhiteToMoveFirst() {
    Board board = new Board();
    board.addMove(2, 4, Player.WHITE);
  }
}
