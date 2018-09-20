package net.shin1gamix.generators.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.shin1gamix.generators.Core;

public class GenScheduler extends BukkitRunnable {

	private Core main;

	private static Map<String, GenScheduler> gens = new HashMap<>();

	public static Map<String, GenScheduler> getGens() {
		return gens;
	}

	private final ItemStack item;
	private int currentTime;
	private final int maxTime;
	private final String id;
	private final Location loc;
	private int playerLimit;
	private final long creationDate;
	private final Hologram holo;
	private boolean working = true;
	private final Vector vel;
	private double velocity;

	public GenScheduler(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit, final double velocity) {
		this.main = main; // Main instance
		this.loc = loc; // Gen location
		this.id = id; // Gen id
		this.item = item; // Gen item

		this.setCurrentTime(0); // Resetting time

		this.maxTime = time; // Setting max time

		this.setPlayerLimit(playerLimit); // Setting player limit.

		this.velocity = velocity; // Setting velocity
		this.vel = new Vector(0, this.velocity, 0); // Setting vector with velocity

		this.creationDate = System.currentTimeMillis(); // Creation time

		this.holo = this.main.getHapi().startHoloTasks(this); // Adding hologram

		gens.put(id, this);

	}

	@Override
	public void run() {
		/* Is the machine working? */
		if (!this.isWorking()) {
			return;
		}

		/* Are there enough players? */
		if (!this.areEnoughPlayers()) {
			return;
		}

		/* Is the current time less than max? Meaning it's not ready to drop yet. */
		if (this.getCurrentTime() <= this.getMaxTime()) {
			this.setCurrentTime(this.getCurrentTime() + 1);
			return;
		}

		/* Rest the time and drop item */
		this.setCurrentTime(0);
		this.getLoc().getWorld().dropItemNaturally(this.getLoc(), this.getItem()).setVelocity(this.vel); // Drop item

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
		return this.getPlayerLimit() > 0 && this.getPlayerLimit() <= Bukkit.getOnlinePlayers().size();
	}

	/**
	 * @return the maxTime
	 */
	public int getMaxTime() {
		return maxTime;
	}

	public void saveMachine(final FileConfiguration file) {
		final Hologram holo = this.getHolo();
		holo.delete();
		HologramsAPI.unregisterPlaceholder(this.main, "%time-left-" + this.getId() + "%");
		if (file.contains("Generators." + this.getId())) {
			return;
		}
		final String path = "Generators." + this.getId() + ".";
		file.set(path + "creation-time", this.getCreationDate());
		file.set(path + "time", this.getMaxTime()); // Setting the time
		file.set(path + "player-limit", this.getPlayerLimit()); // Setting player-limit
		file.set(path + "item", this.getItem()); // Settings the item
		file.set(path + "location", this.getLoc()); // Setting
		file.set(path + "velocity", this.getVelocity());
	}
}
