package dk.loej.hc.loot.entity;

import java.time.LocalDate;

public class LootItemProperties {
    private LocalDate nextLootDate;
    private LocalDate lastLootDate;

    public String getNextLootDate() {
        return nextLootDate.toString();
    }

    public void setNextLootDate(LocalDate nextLootDate) {
        this.nextLootDate = nextLootDate;
    }

    public String getLastLootDate() {
        return lastLootDate.toString();
    }

    public void setLastLootDate(LocalDate lastLootDate) {
        this.lastLootDate = lastLootDate;
    }
}
