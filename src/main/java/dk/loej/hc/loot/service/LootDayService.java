package dk.loej.hc.loot.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.repository.LootItemRepository;
import dk.loej.hc.loot.util.LootDateCalculator;

@Service
public class LootDayService {	
	private final static Logger LOGGER = LoggerFactory.getLogger(LootDayService.class);
	
	private final LootItemRepository repository;
    
	@Autowired
	public LootDayService(LootItemRepository repository) {
		this.repository = repository;
	}

	/*
	 * TODO make this work with multiple pods..
	 */
	@Scheduled(cron = "0 30 4 * * ?") //"0 30 4 * * ?"  "0 26 13 * * ?"
	public void copyCommonItemsFromPreviousLootDateIfEmpty() {
		Date activeLootDate = Date.valueOf(LootDateCalculator.getCurrentLootDate());
		LOGGER.info("copyCommonItemsFromPreviousLootDateIfEmpty called on " + LocalDateTime.now() + ", active loot date: " + activeLootDate);
		List<LootItem> currentLootItems = repository.findByLootDateAndOriginalOrderByPrioritySequenceAsc(activeLootDate, true);
		//List<LootItem> currentLootItems = 
		if (currentLootItems.isEmpty()) {
			Date previousLootDate = Date.valueOf(LootDateCalculator.getPreviousLootDate());
			List<LootItem> previousLootItems = repository.findByLootDateAndOriginalOrderByPrioritySequenceAsc(previousLootDate, true);
			for (LootItem previousLootItem : previousLootItems) {
				if (previousLootItem.isCommon()) {
					LootItem currentLootItem = new LootItem();
					currentLootItem.setRowAndNum(previousLootItem.getRowAndNum());
					currentLootItem.setName(previousLootItem.getName());
					currentLootItem.setOriginal(true);
					currentLootItem.setDisabled(false);
					currentLootItem.setCommon(true);
					currentLootItem.setLootDate(activeLootDate);
					currentLootItem.setPrioritySequence(previousLootItem.getPrioritySequence());
					currentLootItems.add(currentLootItem);
				}				
			}
			repository.save(currentLootItems);
			LOGGER.info("Copied [" + currentLootItems.size() + "] common loot items to new loot date!");
		}
	}
	
}
