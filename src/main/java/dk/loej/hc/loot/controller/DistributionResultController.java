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
import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.repository.DistributionResultRepository;
import dk.loej.hc.loot.repository.LootItemRepository;
import dk.loej.hc.loot.repository.PlayerRepository;
import dk.loej.hc.loot.util.LootDateCalculator;

@Controller
@RequestMapping(value = "api/result")
public class DistributionResultController {
	
	private final DistributionResultRepository distributionResultRepository;
	private final LootItemRepository lootItemRepository;
    private final PlayerRepository playerRepository;
    
    @Autowired
    public DistributionResultController(DistributionResultRepository distributionResultRepository, LootItemRepository lootItemRepository, PlayerRepository playerRepository) {
		this.distributionResultRepository = distributionResultRepository;
		this.lootItemRepository = lootItemRepository;
		this.playerRepository = playerRepository;
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
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	List<DistributionResult> distributionResults = new ArrayList<>();
    	//TODO add find by lastLootDate
        deleteExistingDistributionsResults(lastLootDate);
        Map<String, LootItem> lootItemMap = getLootItemsMap(lastLootDate); 
        
        List<Player> players = playerRepository.findByEnabledAndLootEnabledOrderByGloryPointsDesc(true, true);
        int i = 0;
        int resultIndex = 1;
        while (!lootItemMap.isEmpty()) {
        	List<LootItem> wishList = getWishList(players.get(i).getId(), lastLootDate);
        	for (LootItem wish : wishList) {
				if (lootItemMap.containsKey(wish.getRowAndNum())) {
					distributionResults.add(createDistributionResult(wish, players.get(i), lastLootDate, resultIndex));
					lootItemMap.remove(wish.getRowAndNum());
					break;
				}
			}
        	i++;
        	if (i == players.size()) {
        		i = 0;
        	}
        	resultIndex++;
        }
        distributionResultRepository.save(distributionResults);
    	
    	return distributionResults;
    }

	private DistributionResult createDistributionResult(LootItem wish, Player player, Date lastLootDate, int resultIndex) {
		DistributionResult distributionResult = new DistributionResult();
		distributionResult.setGloryPoints(player.getGloryPoints());
		distributionResult.setLootDate(lastLootDate);
		distributionResult.setLootItem(wish);
		distributionResult.setPlayer(player);
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