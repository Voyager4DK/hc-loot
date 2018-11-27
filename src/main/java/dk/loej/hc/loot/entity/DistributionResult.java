package dk.loej.hc.loot.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DistributionResult {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private int orderSequence;
	private Date lootDate;
	private int gloryPoints;
	
	@ManyToOne
	@JoinColumn(name ="FK_playerId")
	private Player player;	
	
	@ManyToOne
	@JoinColumn(name ="FK_lootItemId")
	private LootItem lootItem;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getOrderSequence() {
		return orderSequence;
	}

	public void setOrderSequence(int orderSequence) {
		this.orderSequence = orderSequence;
	}

	public Date getLootDate() {
		return lootDate;
	}

	public void setLootDate(Date lootDate) {
		this.lootDate = lootDate;
	}

	public int getGloryPoints() {
		return gloryPoints;
	}

	public void setGloryPoints(int gloryPoints) {
		this.gloryPoints = gloryPoints;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public LootItem getLootItem() {
		return lootItem;
	}

	public void setLootItem(LootItem lootItem) {
		this.lootItem = lootItem;
	}
}
