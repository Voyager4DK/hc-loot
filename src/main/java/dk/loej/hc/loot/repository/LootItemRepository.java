package dk.loej.hc.loot.repository;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.LootItem;

public interface LootItemRepository extends CrudRepository<LootItem, Integer> {

}
