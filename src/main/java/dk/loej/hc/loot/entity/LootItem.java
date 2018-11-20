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
    private int playerId;
    private Date lootDate;
        
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

	public void setPriotirySequence(int prioritySequence) {
		this.prioritySequence = prioritySequence;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public Date getLootDate() {
		return lootDate;
	}

	public void setLootDate(Date lootDate) {
		this.lootDate = lootDate;
	}

}
