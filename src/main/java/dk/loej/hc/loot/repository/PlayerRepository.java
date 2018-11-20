package dk.loej.hc.loot.repository;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.Player;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    public List<Player> findAllByOrderByGloryPointsDesc();
}
