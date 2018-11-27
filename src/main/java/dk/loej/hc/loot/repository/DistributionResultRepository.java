package dk.loej.hc.loot.repository;

import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dk.loej.hc.loot.entity.DistributionResult;

public interface DistributionResultRepository extends CrudRepository<DistributionResult, Integer> {
	public List<DistributionResult> findByLootDateOrderByOrderSequenceAsc(Date lootDate);	
	@Query("select d from DistributionResult d where d.lootDate = :lootDate order by orderSequence asc")
    Stream<DistributionResult> findByLootDateOrderByOrderSequenceAscReturnStream(@Param("lootDate") Date lootDate);

}
