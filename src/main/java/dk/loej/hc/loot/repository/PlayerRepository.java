package dk.loej.hc.loot.repository;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}
