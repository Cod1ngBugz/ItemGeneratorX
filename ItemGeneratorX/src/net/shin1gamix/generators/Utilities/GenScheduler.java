package net.shin1gamix.generators.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import net.shin1gamix.generators.Core;

public class GenScheduler extends BukkitRunnable {

	private Core main;

	public Core getCore() {
		return this.main;
	}

	private final ItemStack item;
	private int currentTime;
	private final int maxTime;
	private final String id;
	private final Location loc;
	private int playerLimit;
	private final long creationDate;
	private final Hologram holo;
	private boolean working;

	private static Map<String, GenScheduler> gens = new HashMap<>();

	public static Map<String, GenScheduler> getGens() {
		return gens;
	}

	final Vector vel;
	double velocity;

	public GenScheduler(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit, final double velocity) {
		this.main = main;
		this.loc = loc;
		this.id = id;
		this.item = item;
		this.setCurrentTime(time);
		this.maxTime = this.getCurrentTime();
		this.setPlayerLimit(playerLimit);
		this.velocity = velocity;
		this.vel = new Vector(0, this.velocity, 0);
		this.creationDate = System.currentTimeMillis();
		this.setWorking(true);
		this.holo = this.getCore().getHapi().startHoloTasks(this);

	}

	@Override
	public void run() {
		if (!this.isWorking()) {
			return;
		}

		if (this.getPlayerLimit() > 0 && this.getPlayerLimit() > Bukkit.getOnlinePlayers().size()) {
			return;
		}
		
		if (this.getCurrentTime() <= this.getMaxTime()) {
			this.setCurrentTime(this.getCurrentTime() + 1);
			return;
		}

		this.setCurrentTime(0);
		this.getLoc().getWorld().dropItemNaturally(this.getLoc(), this.getItem()).setVelocity(this.vel);

	}

	/**
	 * @return the currentTime
	 */
	public int getCurrentTime() {
		return this.currentTime;
	}

	/**
	 * @param currentTime
	 *            the currentTime to set
	 */
	public void setCurrentTime(final int currentTime) {
		this.currentTime = currentTime;
	}

	public void setVelocity(final double velocity) {
		this.velocity = velocity;
	}

	public double getVelocity() {
		return this.velocity;
	}

	public ItemStack getItem() {
		return item;
	}

	public Location getLoc() {
		return loc;
	}

	/**
	 * @return the playerLimit
	 */
	public int getPlayerLimit() {
		return this.playerLimit;
	}

	public void setPlayerLimit(int playerLimit) {
		this.playerLimit = playerLimit;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the creationDate
	 */
	public long getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the holo
	 */
	public Hologram getHolo() {
		return holo;
	}

	/**
	 * @return the working
	 */
	public boolean isWorking() {
		return working;
	}

	/**
	 * @param working
	 *            the working to set
	 */
	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean areEnoughPlayers() {
		return this.getPlayerLimit() <= Bukkit.getOnlinePlayers().size();
	}

	/**
	 * @return the maxTime
	 */
	public int getMaxTime() {
		return maxTime;
	}
}
