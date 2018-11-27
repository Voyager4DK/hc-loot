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
		
	
	public List<LootItem> findByLootDateAndOriginalOrderByPrioritySequenceAsc(Date lootDate, boolean original);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.original = :original order by prioritySequence asc")
    Stream<LootItem> findByLootDateAndOriginalOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate, @Param("original") boolean original);
	
	public List<LootItem> findByLootDateAndPlayerIdAndOriginalOrderByPrioritySequenceAsc(Date lootDate, Integer playerId, boolean original);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.playerId = :playerId and l.original = :original order by prioritySequence asc")
    Stream<LootItem> findByLootDateAndPlayerIdAndOriginalOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate, @Param("playerId") Integer playerId, @Param("original") boolean original);
	
	public List<LootItem> findByLootDateAndPlayerIdAndDisabledOrderByPrioritySequenceAsc(Date lootDate, Integer playerId, boolean enabled);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.playerId = :playerId and l.disabled = :disabled order by prioritySequence asc")
    Stream<LootItem> findByLootDateAndPlayerIdAndDisabledOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate, @Param("playerId") Integer playerId, @Param("disabled") boolean disabled);
	
	public List<LootItem> findByLootDateAndPlayerIdAndOriginalAndPrioritySequence(Date lootDate, Integer playerId, boolean original, int prioritySequence);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.playerId = :playerId and l.original = :original and prioritySequence = :prioritySequence")
    Stream<LootItem> findByLootDateAndPlayerIdAndOriginalAndAndPrioritySequenceReturnStream(@Param("lootDate") Date lootDate, @Param("playerId") Integer playerId, @Param("original") boolean original, @Param("prioritySequence") int prioritySequence);
}
