package dk.loej.hc.loot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dk.loej.hc.loot.entity.Player;

import java.util.List;
import java.util.stream.Stream;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    public List<Player> findAllByOrderByGloryPointsDesc();
    
    public List<Player> findByEnabledOrderByNameAsc(boolean enabled);
    
    public List<Player> findByEnabledAndLootEnabledOrderByGloryPointsDesc(boolean enabled, boolean lootEnabled);
    //@Query("select p from Player p where p.enabled = :enabled and p.lootEnabled = :lootEnabled order by gloryPoints desc")
    //Stream<Player> findByEnabledAndLootEnabledOrderGloryPointsDescReturnStream(@Param("enabled") boolean enabled, @Param("lootEnabled") boolean lootEnabled);
	
    
    
}
