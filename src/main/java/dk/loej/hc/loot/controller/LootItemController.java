package dk.loej.hc.loot.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import dk.loej.hc.loot.entity.LootItemProperties;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.util.LootDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.repository.LootItemRepository;
import dk.loej.hc.loot.repository.PlayerRepository;

@Controller
@RequestMapping(value = "api/loot_items")
public class LootItemController {

    private final LootItemRepository repository;
    private final PlayerRepository playerRepository;

    @Autowired
    public LootItemController(LootItemRepository repository, PlayerRepository playerRepository) {
        this.repository = repository;
        this.playerRepository = playerRepository;
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
    	//TODO remove this again : Used for simulating slow load times!
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
        return StreamSupport
                .stream(repository.findByLootDateAndOriginalOrderByPrioritySequenceAsc(lastLootDate, true).spliterator(), false)
                .collect(Collectors.toList());
    }
    
    @ResponseBody
    @GetMapping(value = "/for_player/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAllForPlayer(@PathVariable("playerId") Integer playerId) {
    	//TODO remove this again : Used for simulating slow load times!
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	
    	Player player = playerRepository.findOne(playerId);
    	    	
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
        List items = getItemsForPlayer(playerId, lastLootDate);
        
        if (items == null || items.size() == 0) {
        	items = copyOriginalItems(playerId, lastLootDate);
        }
        
        return items;
    }
    
    @ResponseBody
    @GetMapping(value = "/reset_player/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List resetPlayer(@PathVariable("playerId") Integer playerId) {
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
    	
    	//delete all lootItems for this player for current lootDate
    	repository.delete(repository.findByLootDateAndPlayerIdAndOriginalOrderByPrioritySequenceAsc(lastLootDate, playerId, false));
        
        return copyOriginalItems(playerId, lastLootDate);
    }

	private List<LootItem> getItemsForPlayer(Integer playerId, Date lastLootDate) {
		return StreamSupport
                .stream(repository.findByLootDateAndPlayerIdAndOriginalOrderByPrioritySequenceAsc(lastLootDate, playerId, false).spliterator(), false)
                .collect(Collectors.toList());
	}

    private List copyOriginalItems(Integer playerId, Date lastLootDate) {
    	List<LootItem> originalItems = getAll();
		List<LootItem> newItems =  new ArrayList<>();
		for (LootItem lootItem : originalItems) {
			LootItem newItem = new LootItem();
			newItem.setRowAndNum(lootItem.getRowAndNum());
			newItem.setName(lootItem.getName());
			newItem.setPlayerId(playerId);
			newItem.setLootDate(lastLootDate);
			newItem.setPrioritySequence(lootItem.getPrioritySequence());
			newItem.setOriginal(false);
			newItem.setCommon(lootItem.isCommon());
			newItem.setDisabled(false);
			newItems.add(repository.save(newItem));
		}
		return newItems;
	}

	@ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem createLootItem(@RequestBody(required = false) LootItem lootItem) {
        verifyCorrectPayload(lootItem);

        lootItem.setLootDate(Date.valueOf(LootDateCalculator.getCurrentLootDate()));
        lootItem.setClanId(1);
        
        return repository.save(lootItem);
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem get(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        return repository.findOne(id);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem updateLootItem(@PathVariable("id") Integer id, @RequestBody(required = false) LootItem lootItem) {
        verifyLootItemExists(id);
        verifyCorrectPayload(lootItem);

        lootItem.setLootDate(Date.valueOf(LootDateCalculator.getCurrentLootDate()));
        lootItem.setClanId(1);
        lootItem.setId(id);
        return repository.save(lootItem);
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}/change_sequence", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List changeSequence(@PathVariable("id") Integer id, @RequestBody(required = false) Integer change) {
        verifyLootItemExists(id);
        LootItem lootItem = repository.findOne(id);
        
        lootItem.setPrioritySequence(lootItem.getPrioritySequence() + change);
        
        //TODO also check max value
        if (lootItem.getPrioritySequence() > 0) {
        	updateLootItemWithSameSequence(change, lootItem);
        	repository.save(lootItem);        	
        } 
        
        if (lootItem.getPlayerId() == null) {
        	return getAll();
        } else {
        	return getAllForPlayer(lootItem.getPlayerId());
        }
        
    }

	private void updateLootItemWithSameSequence(Integer change, LootItem lootItem) {
		boolean original = lootItem.getPlayerId() == null;
		List<LootItem> lootItemsWithSameSequence = repository.findByLootDateAndPlayerIdAndOriginalAndPrioritySequence(lootItem.getLootDate(), lootItem.getPlayerId(), original, lootItem.getPrioritySequence());
		if (!lootItemsWithSameSequence.isEmpty()) {
			for (LootItem lootItemWithSameSeq : lootItemsWithSameSequence) {
				lootItemWithSameSeq.setPrioritySequence(lootItemWithSameSeq.getPrioritySequence() - change);
				repository.save(lootItemWithSameSeq);
			}
		}
	}
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}/toggle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List toggle(@PathVariable("id") Integer id, @RequestBody(required = false) boolean disabled) {
        System.out.println("toggle, id=" + id + ", disabled=" + disabled);
    	verifyLootItemExists(id);
        LootItem lootItem = repository.findOne(id);        
        lootItem.setDisabled(disabled);
        
        repository.save(lootItem);
        
        return getAllForPlayer(lootItem.getPlayerId());        
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        repository.delete(id);
    }

    @ResponseBody
    @GetMapping(value = "/loot_item_properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItemProperties getLootItemProperties() {
        return LootDateCalculator.getLootItemProperties();
    }

    private void verifyLootItemExists(Integer id) {
        if (!repository.exists(id)) {
            throw new RuntimeException(String.format("LootItem with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(LootItem lootItem) {
        if (Objects.isNull(lootItem)) {
            throw new RuntimeException("LootItem cannot be null");
        }

        if (!Objects.isNull(lootItem.getId())) {
            throw new RuntimeException("Id field must be generated");
        }
    }
}
