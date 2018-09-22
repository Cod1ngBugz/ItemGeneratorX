package net.shin1gamix.generators.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.Generators.Generator;
import net.shin1gamix.generators.Generators.HoloGenerator;

public class HologramAPI implements Listener {

	private final Core main;

	public HologramAPI(final Core main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	/**
	 * Attempts to lookup and return a placeholder material from the hologram
	 * section of the config.
	 * 
	 * @param input
	 *            -> The string to be looked up
	 * @return String -> The placeholder in a string form if found.]
	 * @see Material#matchMaterial(String)
	 */
	private String getMaterialPlaceholder(final String input) {
		final String replaced = input.replace("%", "");
		if (replaced.contains(" ")) {
			return null;
		}
		final FileConfiguration file = this.main.getSettings().getFile();
		final Set<String> matSection = file.getConfigurationSection("Holograms.materials").getKeys(false);

		/* Null if no placeholder found matching the input string. */
		final String path = matSection.stream().filter(replaced::equalsIgnoreCase).findFirst().orElse(null);

		try {
			Material.valueOf(file.getString("Holograms.materials." + path));
			return path;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates a hologram in the location of a generator.
	 * 
	 * @param generator
	 *            -> The generator at which the hologram will be created.
	 * @return Hologram -> The hologram object so as to modify later.
	 * @see HologramsAPI#registerPlaceholder(org.bukkit.plugin.Plugin, String,
	 *      double, PlaceholderReplacer)
	 * @see #refresh(Generator)
	 * @see Hologram#setAllowPlaceholders(boolean)
	 * @see #getTimeString(String)
	 */
	public Hologram createGeneratorHologram(final HoloGenerator generator) {
		final Location loc = generator.getLoc().clone();

		final Hologram hologram = HologramsAPI.createHologram(this.main, loc.add(0, 4, 0));
		hologram.setAllowPlaceholders(true);

		final String plc = this.getTimeStringPlaceholder(generator);
		HologramsAPI.registerPlaceholder(this.main, plc, .1, new PlaceholderReplacer() {
			@Override
			public String update() {
				final int max = generator.getMaxTime();
				final double current = generator.getCurrentTime();
				return ((int) Math.floor((current / max) * 100)) + "%";
			}
		});

		refreshLater(generator);
		return hologram;
	}

	/**
	 * Adds a line in a hologram.
	 * 
	 * @param generator
	 *            -> The generator to retrieve stats from.
	 * @param file
	 *            -> The file to retrieve certain material placeholders.
	 * @param hologram
	 *            -> The hologram to add the line to.
	 * @param line
	 *            -> The line to add.
	 * 
	 * @see #getMaterial(String)
	 * @see Ut#placeHolder(String, Map)
	 * @see Hologram#appendItemLine(ItemStack)
	 * @see Hologram#appendTextLine(String)
	 * @see Material#matchMaterial(String)
	 */
	private void addLine(final Generator generator, final FileConfiguration file, final Hologram hologram,
			final String line) {
		final String mat = this.getMaterialPlaceholder(line);

		if (mat == null) {
			final Map<String, String> map = new HashMap<>();
			map.put("%item-" + generator.getId() + "%",
					Ut.capFirst(generator.getItem().getType().name().replace("_", " ").toLowerCase(), true));
			map.put("%generator%", generator.getId());
			map.put("%online%", Bukkit.getOnlinePlayers().size() + "");
			map.put("%needed%", generator.getPlayerLimit() + "");

			hologram.appendTextLine(Ut.tr(Ut.placeHolder(line, map)));
		} else {
			hologram.appendItemLine(
					new ItemStack(Material.matchMaterial(file.getString("Holograms.materials." + mat))));
		}
	}

	/**
	 * Listens on player quit event. Refreshes all holograms so as to make sure
	 * there are enough players for the generator to work.
	 * 
	 * @see #refreshLater()
	 */
	@EventHandler
	private void onQuit(final PlayerQuitEvent e) {
		this.refreshAllLater();
	}

	/**
	 * Listens on player join event. Refreshes all holograms so as to make sure
	 * there are enough players for the generator to work if it doesn't.
	 * 
	 * @see #refreshLater()
	 */
	@EventHandler
	private void onJoin(final PlayerJoinEvent e) {
		this.refreshAllLater();
	}

	/**
	 * Refreshes all generators holograms.
	 * 
	 * @see #refresh(Generator)
	 */
	public void refreshAll() {
		Generator.gens.values().stream().filter(gen -> gen instanceof HoloGenerator)
				.forEach(generator -> refresh((HoloGenerator) generator));
	}

	/**
	 * Refreshes all generators holograms a tick later.
	 * 
	 * @see #refreshAll()
	 * @see BukkitRunnable#runTaskLater(org.bukkit.plugin.Plugin, long)
	 */
	public void refreshAllLater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				refreshAll();
			}
		}.runTaskLater(this.main, 1);
	}

	/**
	 * Refreshes a generator a tick later.
	 * 
	 * @param generator
	 *            -> The generator to refresh.
	 * @see #refresh(Generator)
	 * @see BukkitRunnable#runTaskLater(org.bukkit.plugin.Plugin, long)
	 */
	public void refreshLater(final HoloGenerator generator) {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh(generator);
			}
		}.runTaskLater(this.main, 1);
	}

	/**
	 * Refreshses a generator by deleting all lines and then re-adding the correct
	 * ones.
	 * 
	 * @param generator
	 *            -> The generator to refresh.
	 * @see Hologram#clearLines()
	 * @see Generator#isWorking()
	 * @see Generator#areEnoughPlayers()
	 * @see Generator#getMaxTime()
	 * @see #addLine(Generator, FileConfiguration, Hologram, String)
	 */
	public void refresh(final HoloGenerator generator) {
		final FileConfiguration file = this.main.getSettings().getFile();

		/* Is the file in any case null or are there no generators? */
		if (file == null || Generator.getGens().isEmpty()) {
			return;
		}

		/* Let's delete all lines of the hologram. */
		final Hologram hologram = generator.getHolo();
		hologram.clearLines();

		/**
		 * @ENABLED If the generator is not working the disabled hologram will be used.
		 * 
		 * @NOT_ENOUGH If the generator is working and there are not enough players the
		 *             not enough players hologram will be used.
		 * 
		 * @BIG_TIME If the generator is working and its max time is greater than 20 the
		 *           big time hologram will be used
		 * 
		 * @SMALL_TIME If the generator is working and its max time is less than 20 the
		 *             small time hologram will be used
		 */

		/* If the generator is not working we neend't continue... */
		if (!generator.isWorking()) {
			final List<String> disabled = file.getStringList("Holograms.Disabled");
			if (!disabled.isEmpty()) {
				disabled.forEach(line -> addLine(generator, file, hologram, line));
			}
			return;
		}

		/* The generator should be working, are there not enough players? */
		if (!generator.areEnoughPlayers()) {
			final List<String> enough = file.getStringList("Holograms.Not-Enough-Players");
			if (!enough.isEmpty()) {
				enough.forEach(line -> addLine(generator, file, hologram, line));
			}
			return;
		}

		/* Is the max time too big? Let's use the big-time from the file. */
		if (generator.getMaxTime() > file.getInt("Holograms.small-less-than")) {
			final List<String> big = file.getStringList("Holograms.Enabled.Big-Time");
			if (!big.isEmpty()) {
				big.forEach(line -> addLine(generator, file, hologram, line));
			}
			return;
		}

		/* Is the max time small? Let's use small-time from the file. */
		final List<String> small = file.getStringList("Holograms.Enabled.Small-Time");
		if (!small.isEmpty()) {
			small.forEach(line -> addLine(generator, file, hologram, line));
		}

	}

	/**
	 * @param id
	 *            -> The id to include.
	 * @return String -> A placeholder string containing an id
	 */
	public String getTimeStringPlaceholder(final String id) {
		return "%time-left-" + id + "%";
	}

	/**
	 * @param gen
	 *            -> The gen to retrieve the id from.
	 * @return String -> A placeholder string containing an id
	 * @see #getTimeStringPlaceholder(String)
	 */
	public String getTimeStringPlaceholder(final Generator gen) {
		return this.getTimeStringPlaceholder(gen.getId());
	}

}
