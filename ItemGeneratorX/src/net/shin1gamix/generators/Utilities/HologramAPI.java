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

public class HologramAPI implements Listener {

	private final Core main;

	public Core getCore() {
		return this.main;
	}

	public HologramAPI(final Core main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	private String getMaterial(String line) {
		line = line.replace("%", "");
		if (line.contains(" ")) {
			return null;
		}
		final FileConfiguration file = this.getCore().getSettings().getFile();
		final Set<String> mats = file.getConfigurationSection("Holograms.materials").getKeys(false);
		final String path = mats.stream().filter(line::equalsIgnoreCase).findFirst().orElse(null);

		if (path == null) {
			return null;
		}

		final Material mat = Material.matchMaterial(file.getString("Holograms.materials." + path));

		return mat == null ? null : path;
	}

	public Hologram startHoloTasks(final Generator generator) {
		final Location loc = generator.getLoc().clone();

		final Hologram hologram = HologramsAPI.createHologram(this.getCore(), loc.add(0, 4, 0));
		hologram.setAllowPlaceholders(true);

		final String plc = this.getTimeString(generator.getId());

		HologramsAPI.registerPlaceholder(this.getCore(), plc, .1, new PlaceholderReplacer() {
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

	private void addLine(final Generator mach, final FileConfiguration file, final Hologram hologram,
			final String line) {
		final String mat = this.getMaterial(line);

		if (mat == null) {
			final Map<String, String> map = new HashMap<>();
			map.put("%item-" + mach.getId() + "%",
					Ut.capFirst(mach.getItem().getType().name().replace("_", " ").toLowerCase(), true));
			map.put("%generator%", mach.getId());
			map.put("%online%", Bukkit.getOnlinePlayers().size() + "");
			map.put("%needed%", mach.getPlayerLimit() + "");

			hologram.appendTextLine(Ut.tr(Ut.placeHolder(line, map)));
		} else {
			hologram.appendItemLine(
					new ItemStack(Material.matchMaterial(file.getString("Holograms.materials." + mat))));
		}
	}

	public String getTimeString(final String id) {
		return "%time-left-" + id + "%";
	}

	public String getItemString(final String id) {
		return "%item-" + id + "%";
	}

	@EventHandler
	private void onQuit(final PlayerQuitEvent e) {
		this.refreshLater();
	}

	@EventHandler
	private void onJoin(final PlayerJoinEvent e) {
		this.refreshLater();
	}

	public void refreshAll() {
		Generator.getGens().values().forEach(generator -> refresh(generator));
	}

	public void refreshLater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				refreshAll();
			}
		}.runTaskLater(this.main, 1);
	}

	public void refreshLater(final Generator generator) {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh(generator);
			}
		}.runTaskLater(this.main, 1);
	}

	public void refresh(final Generator generator) {
		final FileConfiguration file = this.getCore().getSettings().getFile();

		/* Is the file in any case null or are there no generators? */
		if (file == null || Generator.getGens().isEmpty()) {
			return;
		}

		/* Let's delete all lines of the hologram. */
		final Hologram hologram = generator.getHolo();
		hologram.clearLines();

		/*
		 * NOT WORKING.
		 * 
		 * WORKING -> areEnoughPlayers ? (SIZE ? BIG : SMALL) : NOT_ENOUGH
		 */

		/* If the generator is not working we neend't continue... */
		if (!generator.isWorking()) {
			final List<String> disabled = file.getStringList("Holograms.Disabled");
			if (!disabled.isEmpty()) {
				disabled.forEach(line -> addLine(generator, file, hologram, line));
			}
			return;
		}

		/* The generator should be working, are there enough players? */
		if (!generator.areEnoughPlayers()) {
			final List<String> enough = file.getStringList("Holograms.Not-Enough-Players");
			if (!enough.isEmpty()) {
				enough.forEach(line -> addLine(generator, file, hologram, line));
			}
			return;
		}

		/* Is the max time too big? Let's use the big-time from the file. */
		if (generator.getMaxTime() > 20) {
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

}
