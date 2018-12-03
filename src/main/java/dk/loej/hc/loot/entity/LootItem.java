package dk.loej.hc.loot.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LootItem {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	private String rowAndNum;
	private String name;
    private int prioritySequence;
    private boolean original;
    private boolean common;
    private boolean disabled;
    private Integer playerId;
    private Date lootDate;
    private int clan_id;    
        
    public LootItem() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRowAndNum() {
		return rowAndNum;
	}

	public void setRowAndNum(String rowAndNum) {
		this.rowAndNum = rowAndNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrioritySequence() {
		return prioritySequence;
	}

	public void setPrioritySequence(int prioritySequence) {
		this.prioritySequence = prioritySequence;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public Date getLootDate() {
		return lootDate;
	}

	public void setLootDate(Date lootDate) {
		this.lootDate = lootDate;
	}

	public boolean isCommon() {
		return common;
	}

	public void setCommon(boolean common) {
		this.common = common;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getClanId() {
		return clan_id;
	}

	public void setClanId(int clan_id) {
		this.clan_id = clan_id;
	}	
	
	

}
