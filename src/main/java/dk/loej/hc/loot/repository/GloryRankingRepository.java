package dk.loej.hc.loot.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.GloryRanking;

public interface GloryRankingRepository extends CrudRepository<GloryRanking, Integer> {
	public List<GloryRanking> findByLootDateOrderByGloryDesc(Date lootDate);
	
	public List<GloryRanking> findByLootDateAndEligibleForLootOrderByGloryDesc(Date lootDate, boolean eligibleForLoot);
}
