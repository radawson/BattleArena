package org.clockworx.battlearena.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Hibernate entity mapping the {@code player_data} table used for persisting
 * backed-up player state between arena competitions.
 * <p>
 * Table names receive the configured storage prefix via
 * {@link org.clockworx.data.hibernate.PrefixPhysicalNamingStrategy}.
 */
@Entity
@Table(name = "player_data")
public class PlayerDataEntity {

    @Id
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    @Column(name = "player_name", length = 16)
    private String playerName;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "health")
    private Double health;

    @Column(name = "healthp")
    private Double healthp;

    @Column(name = "hunger")
    private Integer hunger;

    @Column(name = "magic")
    private Integer magic;

    @Column(name = "magicp")
    private Double magicp;

    @Column(name = "items", columnDefinition = "TEXT")
    private String items;

    @Column(name = "match_items", columnDefinition = "TEXT")
    private String matchItems;

    @Column(name = "gamemode", length = 16)
    private String gamemode;

    @Column(name = "godmode")
    private Boolean godmode;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    @Column(name = "effects", columnDefinition = "TEXT")
    private String effects;

    @Column(name = "flight")
    private Boolean flight;

    @Column(name = "arena_class", length = 64)
    private String arenaClass;

    @Column(name = "old_team", length = 64)
    private String oldTeam;

    @Column(name = "scoreboard", columnDefinition = "TEXT")
    private String scoreboard;

    @Column(name = "money")
    private Double money;

    @Column(name = "stored_at")
    private LocalDateTime storedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Default constructor required by Hibernate. */
    protected PlayerDataEntity() {
    }

    public PlayerDataEntity(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Double getHealth() {
        return health;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public Double getHealthp() {
        return healthp;
    }

    public void setHealthp(Double healthp) {
        this.healthp = healthp;
    }

    public Integer getHunger() {
        return hunger;
    }

    public void setHunger(Integer hunger) {
        this.hunger = hunger;
    }

    public Integer getMagic() {
        return magic;
    }

    public void setMagic(Integer magic) {
        this.magic = magic;
    }

    public Double getMagicp() {
        return magicp;
    }

    public void setMagicp(Double magicp) {
        this.magicp = magicp;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getMatchItems() {
        return matchItems;
    }

    public void setMatchItems(String matchItems) {
        this.matchItems = matchItems;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public Boolean getGodmode() {
        return godmode;
    }

    public void setGodmode(Boolean godmode) {
        this.godmode = godmode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEffects() {
        return effects;
    }

    public void setEffects(String effects) {
        this.effects = effects;
    }

    public Boolean getFlight() {
        return flight;
    }

    public void setFlight(Boolean flight) {
        this.flight = flight;
    }

    public String getArenaClass() {
        return arenaClass;
    }

    public void setArenaClass(String arenaClass) {
        this.arenaClass = arenaClass;
    }

    public String getOldTeam() {
        return oldTeam;
    }

    public void setOldTeam(String oldTeam) {
        this.oldTeam = oldTeam;
    }

    public String getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(String scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public LocalDateTime getStoredAt() {
        return storedAt;
    }

    public void setStoredAt(LocalDateTime storedAt) {
        this.storedAt = storedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
