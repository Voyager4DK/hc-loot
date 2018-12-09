package dk.loej.hc.loot.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class GloryRanking {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@ManyToOne
	@JoinColumn(name ="FK_playerId")
	private Player player;

    private Date lootDate;
    private int glory;
    private boolean eligibleForLoot;
    private int clanId;
    private String updatedBy;
    private Timestamp updatedTs;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public Date getLootDate() {
		return lootDate;
	}
	public void setLootDate(Date lootDate) {
		this.lootDate = lootDate;
	}
	public int getGlory() {
		return glory;
	}
	public void setGlory(int glory) {
		this.glory = glory;
	}
	public boolean isEligibleForLoot() {
		return eligibleForLoot;
	}
	public void setEligibleForLoot(boolean eligibleForLoot) {
		this.eligibleForLoot = eligibleForLoot;
	}
	public int getClanId() {
		return clanId;
	}
	public void setClanId(int clanId) {
		this.clanId = clanId;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Timestamp getUpdatedTs() {
		return updatedTs;
	}
	public void setUpdatedTs(Timestamp updatedTs) {
		this.updatedTs = updatedTs;
	}
	
	@Override
	public boolean equals(Object obj) {
		GloryRanking other = (GloryRanking) obj;
		// TODO Auto-generated method stub
		return this.glory == other.getGlory() 
				&& this.player.getId().equals(other.getPlayer().getId())
				&& this.isEligibleForLoot() == other.isEligibleForLoot();
	}
	
}
