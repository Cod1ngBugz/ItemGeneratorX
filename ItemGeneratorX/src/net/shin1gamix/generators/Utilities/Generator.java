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

public class Generator extends BukkitRunnable {

	private Core main;

	private static Map<String, Generator> gens = new HashMap<>();

	public static Map<String, Generator> getGens() {
		return gens;
	}

	private final String id;
	private final Location loc;
	private final ItemStack item;

	private final int maxTime;
	private int playerLimit;
	private int currentTime;

	private final long creationDate;
	private final Hologram holo;
	private boolean working = true;
	private final Vector vel;

	public Generator(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit, final double velocity) {
		this.main = main; // Main instance
		this.loc = loc; // Gen location
		this.id = id; // Gen id
		this.item = item; // Gen item
		this.setCurrentTime(0); // Resetting time
		this.maxTime = time; // Setting max time
		this.setPlayerLimit(playerLimit); // Setting player limit.
		this.vel = new Vector(0, velocity, 0); // Setting vector with velocity
		this.creationDate = System.currentTimeMillis(); // Creation time
		this.holo = this.main.getHapi().startHoloTasks(this); // Adding hologram
		gens.put(id, this); // Adds the generator in the map.

	}

	@Override
	public void run() {
		/* Is the generator working? */
		if (!this.working) {
			return;
		}

		/* Are there enough players? */
		if (!this.areEnoughPlayers()) {
			return;
		}

		/* Is the current time less than max? Meaning it's not ready to drop yet. */
		if (this.currentTime <= this.maxTime) {
			this.setCurrentTime(this.currentTime + 1);
			return;
		}

		/* Rest the time and drop item */
		this.setCurrentTime(0);
		this.loc.getWorld().dropItemNaturally(this.loc, this.item).setVelocity(this.vel); // Drop item

	}

	/**
	 * Sets a generator in the config file while removing and unregistering its
	 * holograms.
	 * 
	 * @param file
	 *            -> The file to save the generator's stats.
	 */
	public void saveGenerator(final FileConfiguration file) {
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
		file.set(path + "velocity", this.vel.getY());
	}

	/**
	 * @return the currentTime -> An integer that is less than {@link #maxTime}
	 *         which increases per tick.
	 */
	public int getCurrentTime() {
		return this.currentTime;
	}

	/**
	 * @param currentTime
	 *            -> The currentTime to set
	 */
	public void setCurrentTime(final int currentTime) {
		if (currentTime > getMaxTime()) {
			this.setCurrentTime(this.getMaxTime());
			return;
		}
		this.currentTime = currentTime;
	}

	/**
	 * @return the item -> The ItemStack that should be dropped.
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return the loc -> The location where the item is going to be dropped.
	 */
	public Location getLoc() {
		return loc;
	}

	/**
	 * @return the playerLimit -> Amount of users needed for the generator to work.
	 */
	public int getPlayerLimit() {
		return this.playerLimit;
	}

	/**
	 * @param playerLimit
	 *            -> Sets the amount of users needed for the generator to work.
	 */
	public void setPlayerLimit(int playerLimit) {
		this.playerLimit = playerLimit;
	}

	/**
	 * @return the id -> The generator's ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the creationDate -> Time in millis
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
	 *            -> Set the generator to either disabled or enabled.
	 */
	public void setWorking(boolean working) {
		this.working = working;
	}

	/**
	 * @return boolean -> Whether there are enough players for the generator to run.
	 */
	public boolean areEnoughPlayers() {
		return this.getPlayerLimit() > 0 && this.getPlayerLimit() <= Bukkit.getOnlinePlayers().size();
	}

	/**
	 * @return the maxTime -> An integer that if reached by
	 *         {@link #getCurrentTime()} it will execute {@link #run()}
	 */
	public int getMaxTime() {
		return maxTime;
	}

}
