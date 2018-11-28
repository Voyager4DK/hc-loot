package dk.loej.hc.loot.util;

import dk.loej.hc.loot.entity.LootItemProperties;

import java.time.LocalDate;

public final class LootDateCalculator {
    private static final LocalDate startDate = LocalDate.of(2018, 11, 13);
    private static final int interval = 14;

    private LootDateCalculator() {}

    public static LocalDate getNextLootDate(LocalDate now) {
        LocalDate nextLootDate = startDate;
        while (nextLootDate.isBefore(now) || nextLootDate.isEqual(now)) {
            nextLootDate = nextLootDate.plusDays(interval);
        }
        return nextLootDate;
    }

    public static LocalDate getCurrentLootDate() {
        return getCurrentLootDate(LocalDate.now());
    }
    
    public static LocalDate getCurrentLootDate(LocalDate now) {
        return getNextLootDate(now).minusDays(interval);
    }
    
    public static LocalDate getPreviousLootDate(LocalDate now) {
        return getCurrentLootDate(now).minusDays(interval);
    }
    
    public static LocalDate getPreviousLootDate() {
        return getPreviousLootDate(LocalDate.now());
    }

    public static LootItemProperties getLootItemProperties() {
    	LocalDate now = LocalDate.now();
    	LootItemProperties lootItemProperties = new LootItemProperties();
        lootItemProperties.setLastLootDate(getCurrentLootDate(now));
        lootItemProperties.setNextLootDate(getNextLootDate(now));
        return lootItemProperties;
    }
}
