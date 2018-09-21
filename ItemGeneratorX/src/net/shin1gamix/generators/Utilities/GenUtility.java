package net.shin1gamix.generators.Utilities;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;

public class GenUtility {

	private final Core main;

	public GenUtility(final Core main) {
		this.main = main;
	}

	/**
	 * Create generator without playerlimit and or vector
	 * 
	 * @param p
	 * @param id
	 * @param timeAmount
	 * @see #createGenerator(Player, String, String, String)
	 */
	public void createGenerator(final Player p, final String id, final String timeAmount) {
		createGenerator(p, id, timeAmount, null);
	}

	/**
	 * Create generator without vector
	 * 
	 * @param p
	 * @param id
	 * @param timeAmount
	 * @param playerLimitAmount
	 * @see #createGenerator(Player, String, String, String, String)
	 */
	public void createGenerator(final Player p, final String id, final String timeAmount, String playerLimitAmount) {
		createGenerator(p, id, timeAmount, playerLimitAmount, null);
	}

	/**
	 * Creates a full generator.
	 * 
	 * @param p
	 *            -> The player creating the generator.
	 * @param id
	 *            -> The generator's ID
	 * @param timeAmount
	 *            -> The maxAmount for the generator to reach so as to drop an item.
	 * @param playerLimitAmount
	 *            -> The player amount needed for the generator to work.
	 * @param vector
	 *            -> A double indicating how high the item will be thrown.
	 */
	public void createGenerator(final Player p, final String id, final String timeAmount, String playerLimitAmount,
			String vector) {

		final ItemStack item;
		if (Bukkit.getVersion().contains("1.8")) {
			item = p.getItemInHand().clone();
		} else {
			item = p.getInventory().getItemInMainHand().clone();
		}

		Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		/* Does the id contain weird characters? */
		if (!Ut.isStringLegal(id)) {
			MessagesX.INVALID_ID.msg(p, map);
			return;
		}

		/* Does this id exist as a generator? */
		if (isGenerator(id, true)) {
			MessagesX.GEN_ALREADY_EXISTS.msg(p, map);
			return;
		}

		/* Is the item air or null? Meaning it's not something that can be generated. */
		if (item.getType() == Material.AIR || item == null) {
			MessagesX.INVALID_ITEM.msg(p);
			return;
		}

		/* Is timeAmount not an integer? */
		if (!Ut.isInt(timeAmount)) {
			MessagesX.NO_TIME_INSERTED.msg(p);
			return;
		}

		final int playerLimit;
		/* Is playerLimitAmount null or not an integer? */
		if (playerLimitAmount == null || !Ut.isInt(playerLimitAmount)) {
			playerLimit = 1;
		} else {
			playerLimit = Integer.valueOf(playerLimitAmount);
		}

		final double velocity;
		/* Is vector null or not a double? */
		if (vector == null || !Ut.isDouble(vector)) {
			velocity = 0.25;
		} else {
			velocity = Double.valueOf(vector);
		}

		final Location loc = p.getLocation(); // Player's current location (feet)
		/* The max number that if reached, the generator will generate an item. */
		final int time = Integer.valueOf(timeAmount);

		/* Creating the generator */
		final Generator generator = new Generator(this.main, loc, id, item, time < 1 ? 1 : time, playerLimit, velocity);
		/* Setting the generator to work 1 second later for every 1 second. */
		generator.runTaskTimer(this.main, 20, 1);
		MessagesX.GEN_CREATED.msg(p, map);
	}

	/**
	 * Checks if the string provided is a name given in a generator.
	 * 
	 * @param id
	 *            -> The string to check if it is a generator.
	 * @return true if the {@link Generator#getGens()} contains the id as a key.
	 */
	public boolean isGenerator(final String id, boolean ignoreCase) {
		final Map<String, Generator> map = Generator.getGens();
		return ignoreCase ? map.keySet().stream().anyMatch(id::equalsIgnoreCase) : map.containsKey(id);
	}

	/**
	 * Attempts to fully remove a generator.
	 * 
	 * @param p
	 *            -> The player removing the generator.
	 * @param id
	 *            -> The id of the generator being removed.
	 * 
	 * @see #isGenerator(String)
	 * @see #deleteGenerator(String)
	 * @see MessagesX#msg(org.bukkit.command.CommandSender, Map)
	 */
	public void removeGenerator(final Player p, final String id) {

		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!isGenerator(id, true)) {
			MessagesX.NOT_GENERATOR.msg(p, map);
			return;
		}

		/* Completely remove the generator */
		this.deleteGenerator(id);

		if (main.getSettings().getFile().contains("Generators." + id)) {
			main.getSettings().getFile().set("Generators." + id, null);
			main.getSettings().saveFile();
		}

		MessagesX.GEN_REMOVED.msg(p, map);
	}

	/**
	 * Attempts to add every generator into the file.
	 * 
	 * @see Generator#getGens()
	 */
	public void saveGenerators() {

		/* Is the file deleted or removed? */
		final File filex = new File(this.main.getDataFolder(), "config.yml");
		if (!filex.exists()) {
			return;
		}

		/* Is the file for some reason empty? */
		if (filex.getTotalSpace() < 10) {
			filex.delete();
			return;
		}

		final FileConfiguration file = this.main.getSettings().getFile();

		final Collection<Generator> generators = Generator.getGens().values();

		/* Attempt to gather all paths from the config that are not a generator. */
		final Set<String> offMachines = new HashSet<>();
		for (final String confPath : file.getConfigurationSection("Generators").getKeys(false)) {
			machloop: for (final String mach : Generator.getGens().values().stream().map(Generator::getId)
					.collect(Collectors.toSet())) {
				if (confPath.equalsIgnoreCase(mach)) {
					continue machloop;
				}
				offMachines.add(confPath);
			}
		}
		/* Removing all invalid paths */
		offMachines.stream().map(str -> "Generators." + str).forEach(str -> file.set(str, null));

		/* Saving all working paths to config */
		generators.forEach(gen -> gen.saveGenerator(file));

		/* Saving file */
		this.main.getSettings().saveFile();
	}

	/**
	 * Disables all generators, making them not generate items.
	 * 
	 * @param p
	 *            -> The one disabling the generators.
	 * 
	 * @see Generator#getGens()
	 * @see Generator#setWorking(boolean)
	 * @see Generator#isWorking()
	 * @see HologramAPI#refreshAll()
	 */
	public void disableGenerators(final Player p) {
		final Map<String, Generator> gens = Generator.getGens();
		if (gens.isEmpty()) {
			// TODO No generators were working.
			return;
		}

		if (gens.values().stream().allMatch(gen -> !gen.isWorking())) {
			// TODO All gens are working
			return;
		}
		gens.values().forEach(gen -> gen.setWorking(false));
		this.main.getHapi().refreshAll();
		MessagesX.TASKS_CANCELLED.msg(p);
	}

	/**
	 * Enable all generators, making them generate items again.
	 * 
	 * @param p
	 *            -> The one disabling the generators.
	 * 
	 * @see Generator#getGens()
	 * @see Generator#setWorking(boolean)
	 * @see Generator#isWorking()
	 * @see HologramAPI#refreshAll()
	 */
	public void enableGenerators(final Player p) {
		final Map<String, Generator> gens = Generator.getGens();
		if (gens.isEmpty()) {
			// TODO No generators were working.
			return;
		}

		if (gens.values().stream().allMatch(Generator::isWorking)) {
			// TODO All gens are working
			return;
		}

		gens.values().forEach(gen -> gen.setWorking(true));
		this.main.getHapi().refreshAll();
		MessagesX.TASKS_CONTINUE.msg(p);
	}

	/**
	 * Checks if the string is a generator and then returns it from a map.
	 * 
	 * @param id
	 *            -> The string to lookup so as to return the generator.
	 * @return Generator -> The generator gained through the
	 *         {@link Generator#getGens()} map.
	 */
	public Generator getGenerator(final String id) {
		return this.isGenerator(id, false) ? Generator.getGens().get(id) : null;
	}

	/**
	 * Creates generators with values grabbed by the file.
	 *
	 * @see Generator#getGens()
	 * @see #removeGenerator(Player, String)
	 * @see #isGenerator(String)
	 */
	public void initGenerators() {
		final FileConfiguration file = this.main.getSettings().getFile();
		if (file == null) {
			return;
		}
		if (file.getConfigurationSection("Generators").getKeys(false).isEmpty()) {
			return;
		}
		final Set<String> generators = file.getConfigurationSection("Generators").getKeys(false);

		for (final String id : generators) {
			if (isGenerator(id, true)) {
				continue;
			}
			final String path = "Generators." + id + ".";
			final Location loc = (Location) file.get(path + "location");
			if (loc == null || loc.getWorld() == null) {
				this.removeGenerator(null, id);
				continue;
			}

			final ItemStack item = file.getItemStack(path + "item");
			final int time = file.getInt(path + "time");
			final int playerLimit = file.getInt(path + "player-limit");
			final double velocity = file.getDouble(path + "velocity");

			final Generator generator = new Generator(this.main, loc, id, item, time, playerLimit, velocity);
			generator.runTaskTimer(this.main, 20, 1);
		}

	}

	/**
	 * Attempts to remove a generator by a given string but doesn't remove it from
	 * file.
	 * 
	 * @param id
	 *            -> The generator to remove.
	 * @return -> true if the removal was succesful.
	 */
	public boolean deleteGenerator(final String id) {
		if (!isGenerator(id, false)) {
			return false;
		}
		final Generator gen = Generator.getGens().get(id);
		gen.cancel();
		gen.getHolo().delete();
		HologramsAPI.unregisterPlaceholder(this.main, this.main.getHapi().getTimeString(id));
		Generator.getGens().remove(id);
		return true;
	}

	/**
	 * Attempts to remove a generator but not from file.
	 * 
	 * @param gen
	 *            -> The generator to be removed.
	 * @return -> true if the removal was succesful.
	 * @see #deleteGenerator(String)
	 */
	public boolean deleteGenerator(final Generator gen) {
		return gen != null && this.deleteGenerator(gen.getId());
	}
}
