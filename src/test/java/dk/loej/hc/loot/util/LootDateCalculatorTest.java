package dk.loej.hc.loot.util;

import org.junit.Test;

public class LootDateCalculatorTest {

    @Test
    public void doTest() {
        System.out.println("lastLootDate=" + LootDateCalculator.getLastLootDate());
        System.out.println("nextLootDate=" + LootDateCalculator.getNextLootDate());
    }
}
