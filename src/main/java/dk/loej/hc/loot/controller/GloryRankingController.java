package dk.loej.hc.loot.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import dk.loej.hc.loot.entity.GloryRanking;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.repository.GloryRankingRepository;
import dk.loej.hc.loot.repository.PlayerRepository;
import dk.loej.hc.loot.util.LootDateCalculator;

@Controller
@RequestMapping(value = "api/glory_rankings")
public class GloryRankingController {
	
	private GloryRankingRepository repository;
	private PlayerRepository playerRepository;
	
	
	@Autowired
	public GloryRankingController(GloryRankingRepository repository, PlayerRepository playerRepository) {
		this.repository = repository;
		this.playerRepository = playerRepository;
	}
	
    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
    	Date currentLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	List<GloryRanking> gloryRankings = repository.findByLootDateOrderByEligibleForLootDescGloryDesc(currentLootDate);
    	
    	List<Player> players = playerRepository.findByEnabledOrderByNameAsc(true);
    	if (gloryRankings.isEmpty()) {    		
    		gloryRankings = createAndPersistGloryRanksFromPlayers(currentLootDate, players);
    	} else {
    		gloryloop : for (GloryRanking gloryRanking : gloryRankings) {
    			for (Player player : players) {
    				if (gloryRanking.getPlayer().getId().equals(player.getId())) {
    					players.remove(player);
    					continue gloryloop;
    				}    				
    			}
    			repository.delete(gloryRanking);    			
    		}
    		if (!players.isEmpty()) {
    			gloryRankings.addAll(createAndPersistGloryRanksFromPlayers(currentLootDate, players));
    		}
    		gloryRankings = repository.findByLootDateOrderByEligibleForLootDescGloryDesc(currentLootDate);
    	}
    	return gloryRankings;
    }

	private List<GloryRanking> createAndPersistGloryRanksFromPlayers(Date currentLootDate, List<Player> players) {
		List<GloryRanking> gloryRankings = new ArrayList<>();
		for (Player player : players) {
			gloryRankings.add(createGloryRanking(currentLootDate, player));
		}
		repository.save(gloryRankings);
		return gloryRankings;
	}

	private GloryRanking createGloryRanking(Date currentLootDate, Player player) {
		GloryRanking gloryRanking = new GloryRanking();
		gloryRanking.setClanId(1);
		gloryRanking.setEligibleForLoot(true);
		gloryRanking.setGlory(0);
		gloryRanking.setLootDate(currentLootDate);
		gloryRanking.setPlayer(player);
		gloryRanking.setUpdatedTs(Timestamp.valueOf(LocalDateTime.now()));
		return gloryRanking;
	}
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody(required = false) List<GloryRanking> gloryRankings) {
    	Date currentLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	List<GloryRanking> oldGloryRankings = repository.findByLootDateOrderByEligibleForLootDescGloryDesc(currentLootDate);
    	List<GloryRanking> changedGloryRankings = new ArrayList<>();

		for (int i=0; i<oldGloryRankings.size(); i++) {
			if (!oldGloryRankings.get(i).equals(gloryRankings.get(i))) {
				changedGloryRankings.add(gloryRankings.get(i));
			}
		}

    	System.out.println("Updating this amount of rankings: " + changedGloryRankings.size());
    	repository.save(changedGloryRankings);
    }



}
