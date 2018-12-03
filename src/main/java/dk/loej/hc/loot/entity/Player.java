package dk.loej.hc.loot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private int gloryPoints;
    private boolean lootEnabled;
    private boolean enabled;
    private String password;
    private boolean admin;
    private Integer clan_id;
    
    public Player() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGloryPoints() {
		return gloryPoints;
	}

	public void setGloryPoints(int gloryPoints) {
		this.gloryPoints = gloryPoints;
	}

	public boolean isLootEnabled() {
		return lootEnabled;
	}

	public void setLootEnabled(boolean lootEnabled) {
		this.lootEnabled = lootEnabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Integer getClanId() {
		return clan_id;
	}

	public void setClanId(Integer clan_id) {
		this.clan_id = clan_id;
	}
}
