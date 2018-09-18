package net.shin1gamix.generators.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.shin1gamix.generators.Core;

public class GenScheduler extends BukkitRunnable {

	private Core main;

	public Core getCore() {
		return this.main;
	}

	private final ItemStack item;
	private int currentTime;
	private final int startTime;
	private final String id;
	private final Location loc;
	private int playerLimit;
	private final long creationDate;

	private static Map<String, GenScheduler> gens = new HashMap<>();

	public static Map<String, GenScheduler> getGens() {
		return gens;
	}

	final Vector vel;

	public GenScheduler(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit) {
		this.main = main;
		this.loc = loc;
		this.id = id;
		this.item = item;
		this.setCurrentTime(time);
		this.startTime = this.getCurrentTime();
		this.setPlayerLimit(playerLimit);
		this.vel = new Vector(0, 0.25, 0);
		this.creationDate = System.currentTimeMillis();
	}

	@Override
	public void run() {

		if (this.getCurrentTime() >= 1) {
			this.setCurrentTime(this.getCurrentTime() - 1);
			return;
		}

		this.setCurrentTime(this.getStartTime());
		if (this.getPlayerLimit() > 0 && this.getPlayerLimit() > Bukkit.getOnlinePlayers().size()) {
			return;
		}
		this.getLoc().getWorld().dropItemNaturally(this.getLoc(), this.getItem()).setVelocity(this.vel);

	}

	/**
	 * @return the currentTime
	 */
	private int getCurrentTime() {
		return this.currentTime;
	}

	/**
	 * @param currentTime
	 *            the currentTime to set
	 */
	public void setCurrentTime(final int currentTime) {
		this.currentTime = currentTime;
	}

	public ItemStack getItem() {
		return item;
	}

	public Location getLoc() {
		return loc;
	}

	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return this.startTime;
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
}
