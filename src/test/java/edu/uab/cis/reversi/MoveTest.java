package edu.uab.cis.reversi;

import org.junit.Assert;
import org.junit.Test;

public class MoveTest {

  @Test
  public void testGetters() {
    Move move = new Move(new Square(1, 1), Player.WHITE);
    Assert.assertEquals(new Square(1, 1), move.getSquare());
    Assert.assertEquals(Player.WHITE, move.getPlayer());
  }

  @Test
  public void testHashingAndEquality() {
    Move move = new Move(new Square(6, 4), Player.BLACK);
    Assert.assertEquals(move, new Move(new Square(6, 4), Player.BLACK));
    Assert.assertEquals(move.hashCode(), new Move(new Square(6, 4), Player.BLACK).hashCode());
    Assert.assertNotEquals(move, new Move(new Square(6, 4), Player.WHITE));
    Assert.assertNotEquals(move, new Move(new Square(4, 6), Player.BLACK));
    Assert.assertNotEquals(move, null);
  }

  @Test
  public void testToString() {
    Move move = new Move(new Square(6, 4), Player.BLACK);
    Assert.assertTrue(move.toString().contains("BLACK"));
    Assert.assertTrue(move.toString().contains("6"));
    Assert.assertTrue(move.toString().contains("4"));

    move = new Move(Square.PASS, Player.WHITE);
    Assert.assertTrue(move.toString().contains("WHITE"));
    Assert.assertTrue(move.toString().contains("PASS"));
  }
}
