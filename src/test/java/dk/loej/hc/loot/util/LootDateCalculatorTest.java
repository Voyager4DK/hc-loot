package dk.loej.hc.loot.util;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class LootDateCalculatorTest {

    @Test
    public void doTestBefore() {    	
        System.out.println("previousLootDate for 26-11-2018=" + LootDateCalculator.getPreviousLootDate(LocalDate.of(2018, 11, 26)));
        System.out.println("currentLootDate for 26-11-2018=" + LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 26)));
        System.out.println("nextLootDate for 26-11-2018=" + LootDateCalculator.getNextLootDate(LocalDate.of(2018, 11, 26)));
        assertEquals(LocalDate.of(2018, 11, 13), LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 26)));
    }
    
    @Test
    public void doTestEqual() { 
    	System.out.println("previousLootDate for 27-11-2018=" + LootDateCalculator.getPreviousLootDate(LocalDate.of(2018, 11, 27)));
        System.out.println("currentLootDate for 27-11-2018=" + LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 27)));
        System.out.println("nextLootDate for 27-11-2018=" + LootDateCalculator.getNextLootDate(LocalDate.of(2018, 11, 27)));
        assertEquals(LocalDate.of(2018, 11, 27), LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 27)));
    }
    
    @Test
    public void doTestAfter() {    
    	System.out.println("previousLootDate for 28-11-2018=" + LootDateCalculator.getPreviousLootDate(LocalDate.of(2018, 11, 28)));
        System.out.println("currentLootDate for 28-11-2018=" + LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 28)));
        System.out.println("nextLootDate for 28-11-2018=" + LootDateCalculator.getNextLootDate(LocalDate.of(2018, 11, 28)));
        assertEquals(LocalDate.of(2018, 11, 27), LootDateCalculator.getCurrentLootDate(LocalDate.of(2018, 11, 28)));
    }
}
