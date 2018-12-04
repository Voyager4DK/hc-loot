package dk.loej.hc.loot.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    public List<Player> findAllByOrderByNameAsc();
    
    //public List<Player> findByEnabledOrderByNameAsc(boolean enabled);
    
    public List<Player> findByEnabledAndLootEnabledOrderByGloryPointsDesc(boolean enabled, boolean lootEnabled);    
    
}
