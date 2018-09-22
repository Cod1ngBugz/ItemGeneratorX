package net.shin1gamix.generators.Generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.shin1gamix.generators.Core;

public class SimpleGenerator implements Generator {
	private final String id;
	private final Location loc;
	private final ItemStack item;

	private final int maxTime;
	private int playerLimit;
	private int currentTime;

	private final long creationDate;
	private boolean working = true;
	private final Vector vel;
	private final BukkitTask task;

	private final Core main;

	public Core getCore() {
		return this.main;
	}

	public SimpleGenerator(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit, final double velocity) {
		this.main = main;
		this.loc = loc; // Gen location
		this.id = id; // Gen id
		this.item = item; // Gen item
		this.setCurrentTime(0); // Resetting time
		this.maxTime = time; // Setting max time
		this.setPlayerLimit(playerLimit); // Setting player limit.
		this.vel = new Vector(0, velocity, 0); // Setting vector with velocity
		this.creationDate = System.currentTimeMillis(); // Creation time
		gens.put(id, this); // Adds the generator in the map.
		task = Bukkit.getScheduler().runTaskTimer(main, new GeneratorRunnable(this), 5, 1);
	}

	/**
	 * Sets a generator in the config file while removing and unregistering its
	 * holograms.
	 * 
	 * @param file
	 *            -> The file to save the generator's stats.
	 */
	public void saveGenerator(final Core main, final FileConfiguration file) {
		final String path = "Generators." + this.id + ".";
		file.set(path + "creation-time", this.creationDate);
		file.set(path + "time", this.maxTime); // Setting the time
		file.set(path + "player-limit", this.playerLimit); // Setting player-limit
		file.set(path + "item", this.item); // Settings the item
		file.set(path + "location", this.loc); // Setting
		file.set(path + "velocity", this.vel.getY());
		file.set(path + "using-hologram", false);
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
		return this.item;
	}

	/**
	 * @return the loc -> The location where the item is going to be dropped.
	 */
	public Location getLoc() {
		return this.loc;
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
		return this.creationDate;
	}

	/**
	 * @return the working
	 */
	public boolean isWorking() {
		return this.working;
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
		return this.playerLimit > 0 && this.playerLimit <= Bukkit.getOnlinePlayers().size();
	}

	/**
	 * @return the maxTime -> An integer that if reached by
	 *         {@link #getCurrentTime()} it will execute {@link #run()}
	 */
	public int getMaxTime() {
		return this.maxTime;
	}

	public Vector getVelocity() {
		return this.vel;
	}

	/**
	 * @return the task
	 */
	public BukkitTask getTask() {
		return this.task;
	}

	public void remove() {
		this.task.cancel();
		gens.remove(this.id);
		final FileConfiguration file = this.main.getSettings().getFile();
		if (file.contains("Generators." + id)) {
			file.set("Generators." + id, null);
			this.main.getSettings().saveFile();
		}
	}

}
