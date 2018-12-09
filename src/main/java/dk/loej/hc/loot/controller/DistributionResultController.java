package dk.loej.hc.loot.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dk.loej.hc.loot.entity.DistributionResult;
import dk.loej.hc.loot.entity.GloryRanking;
import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.repository.DistributionResultRepository;
import dk.loej.hc.loot.repository.GloryRankingRepository;
import dk.loej.hc.loot.repository.LootItemRepository;
import dk.loej.hc.loot.repository.PlayerRepository;
import dk.loej.hc.loot.util.LootDateCalculator;

@Controller
@RequestMapping(value = "api/result")
public class DistributionResultController {
	
	private final DistributionResultRepository distributionResultRepository;
	private final LootItemRepository lootItemRepository;
    //private final PlayerRepository playerRepository;
    private final GloryRankingRepository gloryRankingRepository;
    
    @Autowired
    public DistributionResultController(DistributionResultRepository distributionResultRepository, LootItemRepository lootItemRepository, PlayerRepository playerRepository, 
    		GloryRankingRepository gloryRankingRepository) {
		this.distributionResultRepository = distributionResultRepository;
		this.lootItemRepository = lootItemRepository;
		//this.playerRepository = playerRepository;
		this.gloryRankingRepository = gloryRankingRepository;
	}
	
	@ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	//TODO add find by lastLootDate
        return StreamSupport
                .stream(distributionResultRepository.findByLootDateOrderByOrderSequenceAsc(lastLootDate).spliterator(), false)
                .collect(Collectors.toList());
    }
    
    @ResponseBody
    @GetMapping(value = "/distribute", produces = MediaType.APPLICATION_JSON_VALUE)
    public List distributeLoot() {
    	Date currentLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	List<DistributionResult> distributionResults = new ArrayList<>();
    	//TODO add find by lastLootDate
        deleteExistingDistributionsResults(currentLootDate);
        Map<String, LootItem> lootItemMap = getLootItemsMap(currentLootDate); 
        
        List<GloryRanking> gloryRankings = gloryRankingRepository.findByLootDateAndEligibleForLootOrderByGloryDesc(currentLootDate, true);//playerRepository.findByEnabledAndLootEnabledOrderByGloryPointsDesc(true, true);
        gloryRankings = removePlayerThatDontWantLoot(gloryRankings);
        int i = 0;
        while (!lootItemMap.isEmpty()) {
        	List<LootItem> wishList = getWishList(gloryRankings.get(i).getPlayer().getId(), currentLootDate);
        	for (LootItem wish : wishList) {
				if (lootItemMap.containsKey(wish.getRowAndNum())) {
					distributionResults.add(createDistributionResult(lootItemMap.get(wish.getRowAndNum()), gloryRankings.get(i), currentLootDate, distributionResults.size()+1));
					lootItemMap.remove(wish.getRowAndNum());
					break;
				}
			}
        	i++;
        	if (i == gloryRankings.size()) {
        		i = 0;
        	}        	
        }
        distributionResultRepository.save(distributionResults);
    	
    	return distributionResults;
    }
    
    private List<GloryRanking> removePlayerThatDontWantLoot(List<GloryRanking> gloryRankings) {
    	for (GloryRanking gloryRanking : gloryRankings) {
			if (!gloryRanking.getPlayer().isLootEnabled()) {
				gloryRankings.remove(gloryRanking);
			}
		}
    	return gloryRankings;
    }

	private DistributionResult createDistributionResult(LootItem lootItem, GloryRanking gloryRanking, Date lastLootDate, int resultIndex) {
		DistributionResult distributionResult = new DistributionResult();
		distributionResult.setGloryPoints(gloryRanking.getGlory());
		distributionResult.setLootDate(lastLootDate);
		distributionResult.setLootItem(lootItem);
		distributionResult.setPlayer(gloryRanking.getPlayer());
		distributionResult.setOrderSequence(resultIndex);
		
		return distributionResult;
	}

	private List<LootItem> getWishList(Integer playerId, Date lastLootDate) {
		List<LootItem> wishList = lootItemRepository.findByLootDateAndPlayerIdAndDisabledOrderByPrioritySequenceAsc(lastLootDate, playerId, false);
		if (wishList.isEmpty()) {
			wishList = lootItemRepository.findByLootDateAndOriginalOrderByPrioritySequenceAsc(lastLootDate, true);
		}
		return wishList;
	}

	private Map<String, LootItem> getLootItemsMap(Date lastLootDate) {
		Map<String, LootItem> lootItemMap = new HashMap<>();		
		List<LootItem> lootItems = lootItemRepository.findByLootDateAndOriginalOrderByPrioritySequenceAsc(lastLootDate, true);
		for (LootItem lootItem : lootItems) {
			lootItemMap.put(lootItem.getRowAndNum(), lootItem);
		}
		return lootItemMap;
	}

	private void deleteExistingDistributionsResults(Date lastLootDate) {
		List<DistributionResult> result = distributionResultRepository.findByLootDateOrderByOrderSequenceAsc(lastLootDate);
        if (result != null) {
        	distributionResultRepository.delete(result);
        }
	}

}
