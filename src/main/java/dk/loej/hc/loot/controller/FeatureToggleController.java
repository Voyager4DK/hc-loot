package dk.loej.hc.loot.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dk.loej.hc.loot.entity.FeatureToggle;
import dk.loej.hc.loot.repository.FeatureToggleRepository;
import dk.loej.hc.loot.type.Feature;

@Controller
@RequestMapping(value = "api/feature_toggle")
public class FeatureToggleController {
	
	private FeatureToggleRepository featureToggleRepository;
	
	@Autowired
	public FeatureToggleController(FeatureToggleRepository featureToggleRepository) {
		this.featureToggleRepository = featureToggleRepository;
	}
	
    @ResponseBody
    @GetMapping(value = "/{feature}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean get(@PathVariable("feature") Feature feature) {
    	List<FeatureToggle> list = featureToggleRepository.findByFeature(feature);
    	if (list.isEmpty()) {
    		return false;
    	}
    	return list.get(0).isDisabled();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{feature}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("feature") Feature feature, @RequestBody(required = false) boolean featureDisabled) throws ValidationException {
    	FeatureToggle featureToggle =  getFeatureToggle(feature);
    	featureToggle.setDisabled(featureDisabled);
    	featureToggleRepository.save(featureToggle);
    }
    
    private FeatureToggle getFeatureToggle(Feature feature) {
    	List<FeatureToggle> list = featureToggleRepository.findByFeature(feature);
    	if (list.isEmpty()) {
    		FeatureToggle featureToggle = new FeatureToggle();
    		featureToggle.setDisabled(false);
    		featureToggle.setFeature(feature);
    		featureToggle.setUpdatedTs(Timestamp.valueOf(LocalDateTime.now()));
    		return featureToggle;
    	} else {
    		return list.get(0);
    	}
    	
    	
    }
}
