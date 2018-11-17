package dk.loej.hc.loot.repository;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.Fruit;

public interface FruitRepository extends CrudRepository<Fruit, Integer> {
}