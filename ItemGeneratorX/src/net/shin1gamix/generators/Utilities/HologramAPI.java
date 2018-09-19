package net.shin1gamix.generators.Utilities;

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
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
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

		final FileConfiguration file = this.getCore().getSettings().getFile();

		final String plc = "%time-left-" + machine.getId() + "%";
		HologramsAPI.registerPlaceholder(this.getCore(), plc, .1, new PlaceholderReplacer() {

			@Override
			public String update() {
				final int max = machine.getMaxTime();
				final double current = machine.getCurrentTime();
				return (int) Math.floor((current / max) * 100) + "%";
			}
		});

		if (machine.isWorking()) {
			if (machine.areEnoughPlayers()) {
				if (machine.getMaxTime() < 20) {
					file.getStringList("Holograms.Enabled.Small-Time")
							.forEach(line -> addLine(machine, file, hologram, line));
				} else {
					file.getStringList("Holograms.Enabled.Big-Time")
							.forEach(line -> addLine(machine, file, hologram, line));
				}
			} else {
				file.getStringList("Holograms.Not-Enough-Players")
						.forEach(line -> addLine(machine, file, hologram, line));
			}
		} else {
			file.getStringList("Holograms.Disabled").forEach(line -> addLine(machine, file, hologram, line));
		}

		this.refresh(true);

		return hologram;
	}

	private void fixLines(final Hologram holo, final GenScheduler machine) {
		if (machine.getMaxTime() < 20) {
			return;
		}
		for (int i = 0; i < holo.size(); i++) {
			final HologramLine hl = holo.getLine(i);
			if (!(hl instanceof TextLine))
				continue;
			final TextLine tl = (TextLine) hl;
			final String text = tl.getText();
			holo.removeLine(i);
			holo.insertTextLine(i, text);
		}
	}

	private void addLine(final GenScheduler mach, final FileConfiguration file, final Hologram hologram,
			final String line) {
		final String mat = this.getMaterial(line);

		if (mat == null) {
			hologram.appendTextLine(Ut.tr(line
					.replace("%item-%machine%%", mach.getItem().getType().name().replace("_", " ").toLowerCase())
					.replace("%machine%", mach.getId()).replace("%online%", Bukkit.getOnlinePlayers().size() + "")
					.replace("%needed%", Bukkit.getMaxPlayers() + "")));
		} else {
			hologram.appendItemLine(
					new ItemStack(Material.matchMaterial(file.getString("Holograms.materials." + mat))));
		}
	}

	public void refresh(final boolean runnable) {
		final FileConfiguration file = this.getCore().getSettings().getFile();
		for (final GenScheduler machine : GenScheduler.getGens().values()) {
			final Hologram hologram = machine.getHolo();
			hologram.clearLines();
			if (machine.isWorking()) {
				if (machine.areEnoughPlayers()) {
					if (machine.getMaxTime() < 20) {
						file.getStringList("Holograms.Enabled.Small-Time")
								.forEach(line -> addLine(machine, file, hologram, line));
					} else {
						file.getStringList("Holograms.Enabled.Big-Time")
								.forEach(line -> addLine(machine, file, hologram, line));
					}
				} else {
					file.getStringList("Holograms.Not-Enough-Players")
							.forEach(line -> addLine(machine, file, hologram, line));
				}
			} else {
				file.getStringList("Holograms.Disabled").forEach(line -> addLine(machine, file, hologram, line));
			}
			if (!runnable) {
				this.fixLines(hologram, machine);
				continue;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					fixLines(hologram, machine);
				}
			}.runTaskTimer(this.getCore(), 0, 5);
		}

	}

	@EventHandler
	private void onJoin(final PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh(false);
			}
		}.runTaskLater(main, 1);

	}

	@EventHandler
	private void onQuit(final PlayerQuitEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				refresh(false);
			}
		}.runTaskLater(main, 1);
	}
}
