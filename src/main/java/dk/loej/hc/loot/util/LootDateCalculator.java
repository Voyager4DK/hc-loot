package dk.loej.hc.loot.util;

import dk.loej.hc.loot.entity.LootItemProperties;

import java.time.LocalDate;

public final class LootDateCalculator {
    private static final LocalDate startDate = LocalDate.of(2018, 11, 13);
    private static final int interval = 14;

    private LootDateCalculator() {}

    public static LocalDate getNextLootDate() {
    	return getCurrentLootDate().plusDays(interval);
    }

    public static LocalDate getCurrentLootDate() {
    	LocalDate now = LocalDate.now();
        LocalDate nextLootDate = startDate;
        while (nextLootDate.isBefore(now)) {
            nextLootDate = nextLootDate.plusDays(interval);
        }
        return nextLootDate;
    }
    
    public static LocalDate getPreviousLootDate() {
        return getCurrentLootDate().minusDays(interval);
    }

    public static LootItemProperties getLootItemProperties() {
        LootItemProperties lootItemProperties = new LootItemProperties();
        lootItemProperties.setLastLootDate(getCurrentLootDate());
        lootItemProperties.setNextLootDate(getNextLootDate());
        return lootItemProperties;
    }
}
