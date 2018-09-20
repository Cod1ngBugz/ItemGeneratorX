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

	public Hologram startHoloTasks(final GenScheduler machine) {
		final Location loc = machine.getLoc().clone();

		final Hologram hologram = HologramsAPI.createHologram(this.getCore(), loc.add(0, 4, 0));
		hologram.setAllowPlaceholders(true);

		final String plc = this.getTimeString(machine.getId());

		HologramsAPI.registerPlaceholder(this.getCore(), plc, .1, new PlaceholderReplacer() {
			@Override
			public String update() {
				final int max = machine.getMaxTime();
				final double current = machine.getCurrentTime();
				return ((int) Math.floor((current / max) * 100)) + "%";
			}
		});

		refreshLater(machine);
		return hologram;
	}

	private void addLine(final GenScheduler mach, final FileConfiguration file, final Hologram hologram,
			final String line) {
		final String mat = this.getMaterial(line);

		if (mat == null) {
			final Map<String, String> map = new HashMap<>();
			map.put("%item-" + mach.getId() + "%",
					Ut.capFirst(mach.getItem().getType().name().replace("_", " ").toLowerCase(), true));
			map.put("%machine%", mach.getId());
			map.put("%online%", Bukkit.getOnlinePlayers().size() + "");
			map.put("%needed%", mach.getPlayerLimit() + "");

			hologram.appendTextLine(Ut.tr(Ut.placeHolder(line, map)));
		} else {
			hologram.appendItemLine(
					new ItemStack(Material.matchMaterial(file.getString("Holograms.materials." + mat))));
		}
	}

	public void refresh(final GenScheduler machine) {
		final FileConfiguration file = this.getCore().getSettings().getFile();

		if (file == null || GenScheduler.getGens().isEmpty()) {
			return;
		}
		final Hologram hologram = machine.getHolo();
		hologram.clearLines();

		final List<String> small = file.getStringList("Holograms.Enabled.Small-Time");
		final List<String> big = file.getStringList("Holograms.Enabled.Small-Time");
		final List<String> disabled = file.getStringList("Holograms.Disabled");
		final List<String> enough = file.getStringList("Holograms.Not-Enough-Players");

		if (machine.isWorking()) {
			if (machine.areEnoughPlayers()) {
				if (machine.getMaxTime() < 20) {
					if (small.isEmpty()) {
						return;
					}
					small.forEach(line -> addLine(machine, file, hologram, line));
				} else {
					if (big.isEmpty()) {
						return;
					}
					big.forEach(line -> addLine(machine, file, hologram, line));
				}
			} else {
				if (enough.isEmpty()) {
					return;
				}
				enough.forEach(line -> addLine(machine, file, hologram, line));
			}
		} else {
			if (disabled.isEmpty()) {
				return;
			}
			disabled.forEach(line -> addLine(machine, file, hologram, line));
		}
	}

	public void refresh() {
		GenScheduler.getGens().values().forEach(mach -> refresh(mach));
	}

	public void refreshLater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh();
			}
		}.runTaskLater(this.main, 1);
	}

	public void refreshLater(final GenScheduler machine) {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh(machine);
			}
		}.runTaskLater(this.main, 1);
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
}
