package net.shin1gamix.generators.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.shin1gamix.generators.Core;

public interface Generator {

	final static Map<String, Generator> gens = new HashMap<>();

	public static Map<String, Generator> getGens() {
		return gens;
	}

	/**
	 * Sets a generator in the config file while removing and unregistering its
	 * holograms.
	 * 
	 * @param file
	 *            -> The file to save the generator's stats.
	 */
	public void saveGenerator(final Core main, final FileConfiguration file);

	/**
	 * @return the currentTime -> An integer that is less than {@link #maxTime}
	 *         which increases per tick.
	 */
	public int getCurrentTime();

	/**
	 * @param currentTime
	 *            -> The currentTime to set
	 */
	public void setCurrentTime(final int currentTime);

	/**
	 * @return the item -> The ItemStack that should be dropped.
	 */
	public ItemStack getItem();

	/**
	 * @return the velocity -> The item's velocity.
	 */
	public Vector getVelocity();

	/**
	 * @return the loc -> The location where the item is going to be dropped.
	 */
	public Location getLoc();

	/**
	 * @return the playerLimit -> Amount of users needed for the generator to work.
	 */
	public int getPlayerLimit();

	/**
	 * @param playerLimit
	 *            -> Sets the amount of users needed for the generator to work.
	 */
	public void setPlayerLimit(int playerLimit);

	/**
	 * @return the id -> The generator's ID
	 */
	public String getId();

	/**
	 * @return the creationDate -> Time in millis
	 */
	public long getCreationDate();

	/**
	 * @return the working
	 */
	public boolean isWorking();

	/**
	 * @param working
	 *            -> Set the generator to either disabled or enabled.
	 */
	public void setWorking(boolean working);

	/**
	 * @return boolean -> Whether there are enough players for the generator to run.
	 */
	public boolean areEnoughPlayers();

	/**
	 * @return the maxTime -> An integer that if reached by
	 *         {@link #getCurrentTime()} it will execute {@link #run()}
	 */
	public int getMaxTime();

	/**
	 * @return the runnable
	 */
	public BukkitTask getTask();

	public void remove();
}
