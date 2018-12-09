package dk.loej.hc.loot.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.FeatureToggle;
import dk.loej.hc.loot.type.Feature;

public interface FeatureToggleRepository extends CrudRepository<FeatureToggle, Integer> {
	public List<FeatureToggle> findByFeature(Feature feature);
}
