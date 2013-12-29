package edu.uab.cis.reversi;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

  @Test
  public void testOpponent() {
    Assert.assertEquals(Player.BLACK.opponent(), Player.WHITE);
    Assert.assertEquals(Player.WHITE.opponent(), Player.BLACK);
  }

}
