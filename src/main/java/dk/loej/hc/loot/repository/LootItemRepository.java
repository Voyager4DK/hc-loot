package dk.loej.hc.loot.repository;

import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dk.loej.hc.loot.entity.LootItem;

public interface LootItemRepository extends CrudRepository<LootItem, Integer> {
	public List<LootItem> findByLootDateOrderByPrioritySequenceAsc(Date lootDate);
	
	@Query("select l from LootItem l where l.lootDate = :lootDate order by prioritySequence asc")
    Stream<LootItem> findByLootDateOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate);

}
