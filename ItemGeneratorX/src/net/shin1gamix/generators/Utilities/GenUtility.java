package net.shin1gamix.generators.Utilities;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;

public class GenUtility {

	private final Core main;

	public GenUtility(Core main) {
		this.main = main;

	}

	public void createGenX(final Player p, final ItemStack item, final String id, final String timeAmount) {
		createGenX(p, p.getItemInHand().clone(), id, timeAmount, null);
	}

	public void createGenX(final Player p, final ItemStack item, final String id, final String timeAmount,
			String playerLimitAmount) {
		createGenX(p, item, id, timeAmount, playerLimitAmount, null);
	}

	public void createGenX(final Player p, final ItemStack item, final String id, final String timeAmount,
			String playerLimitAmount, String vector) {

		Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!Ut.isAllowed(id)) {
			MessagesX.INVALID_ID.msg(p, map);
			return;
		}

		if (isGenerator(id)) {
			MessagesX.GEN_EXISTS.msg(p, map);
			return;
		}

		if (item.getType() == Material.AIR || item == null) {
			MessagesX.INVALID_ITEM.msg(p);
			return;
		}

		if (!Ut.isInt(timeAmount)) {
			MessagesX.NO_TIME_INSERTED.msg(p);
			return;
		}

		final int playerLimit;

		if (playerLimitAmount == null || !Ut.isInt(playerLimitAmount)) {
			playerLimit = 1;
		} else {
			playerLimit = Integer.valueOf(playerLimitAmount);
		}

		final double velocity;

		if (vector == null || !Ut.isDouble(vector)) {
			velocity = 0.25;
		} else {
			velocity = Double.valueOf(vector);
		}

		final Location loc = p.getLocation();
		final int time = Integer.valueOf(timeAmount);

		final GenScheduler gensch = new GenScheduler(this.main, loc, id, item, time < 1 ? 1 : time, playerLimit,
				velocity);
		GenScheduler.getGens().put(id, gensch);
		gensch.runTaskTimer(this.main, 20, 1);
		MessagesX.GEN_CREATED.msg(p, map);
	}

	public boolean isGenerator(final String id) {
		return GenScheduler.getGens().keySet().stream().anyMatch(id::equalsIgnoreCase)
				|| this.main.getSettings().getFile().contains("Generators." + id);
	}

	public void removeGenX(final Player p, final String id) {
		final Map<String, GenScheduler> gens = GenScheduler.getGens();

		Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!isGenerator(id) && p != null) {
			MessagesX.NOT_MACHINE.msg(p, map);
			return;
		}

		gens.get(id).getHolo().delete();

		if (gens.containsKey(id)) {
			gens.get(id).cancel();
			gens.remove(id);
		}

		if (main.getSettings().getFile().contains("Generators." + id)) {
			main.getSettings().getFile().set("Generators." + id, null);
			main.getSettings().saveFile();
		}

		if (p != null) {
			MessagesX.GEN_REMOVED.msg(p, map);
		}
	}

	public void saveMachines() {
		if (!new File(this.main.getDataFolder(), "config.yml").exists()) {
			this.main.getSettings().setup(true);
			return;
		}

		final FileConfiguration file = this.main.getSettings().getFile();
		final Collection<GenScheduler> machines = GenScheduler.getGens().values();

		final Set<String> offMachines = new HashSet<>();
		for (final String confPath : file.getConfigurationSection("Generators").getKeys(false)) {
			machloop: for (final String mach : GenScheduler.getGens().values().stream().map(GenScheduler::getId)
					.collect(Collectors.toSet())) {
				if (confPath.equalsIgnoreCase(mach)) {
					continue machloop;
				}
				offMachines.add(confPath);
			}
		}

		offMachines.forEach(mach -> {
			file.set("Generators." + mach, null);
		});

		for (GenScheduler machine : machines) {
			final Hologram holo = machine.getHolo();
			holo.delete();
			HologramsAPI.unregisterPlaceholder(this.main, "%time-left" + machine.getId());
			if (file.contains("Generators." + machine.getId())) {
				continue;
			}
			final String path = "Generators." + machine.getId() + ".";
			file.set(path + "creation-time", machine.getCreationDate());
			file.set(path + "time", machine.getMaxTime()); // Setting the time
			file.set(path + "player-limit", machine.getPlayerLimit()); // Setting player-limit
			file.set(path + "item", machine.getItem()); // Settings the item
			file.set(path + "location", machine.getLoc()); // Setting
			file.set(path + "velocity", machine.getVelocity());
		}

		this.main.getSettings().saveFile();
	}

	public void cancelTasks() {
		final Map<String, GenScheduler> gens = GenScheduler.getGens();
		if (gens.isEmpty()) {
			// TODO No generators were working.
			return;
		}
		gens.values().forEach(gen -> gen.setWorking(false));
		this.main.getHapi().refresh(false);
	}

	public void startTasks() {
		final Map<String, GenScheduler> gens = GenScheduler.getGens();
		if (gens.isEmpty()) {
			// TODO No generators were working.
			return;
		}
		if (gens.values().stream().allMatch(gen -> gen.isWorking())) {
			// All gens are working
			return;
		}

		gens.values().forEach(gen -> gen.setWorking(true));
		this.main.getHapi().refresh(false);
	}

	public void startsMachines() {
		final FileConfiguration file = this.main.getSettings().getFile();
		final Set<String> generators = file.getConfigurationSection("Generators").getKeys(false);
		for (final String id : generators) {
			if (GenScheduler.getGens().containsKey(id)) {
				continue;
			}

			final String path = "Generators." + id + ".";
			final Location loc = (Location) file.get(path + "location");
			if (loc == null || loc.getWorld() == null) {
				this.removeGenX(null, id);
				continue;
			}
			final ItemStack item = file.getItemStack(path + "item");
			final int time = file.getInt(path + "time");
			final int playerLimit = file.getInt(path + "player-limit");
			final double velocity = file.getDouble(path + "velocity");
			final GenScheduler gensch = new GenScheduler(this.main, loc, id, item, time, playerLimit, velocity);
			GenScheduler.getGens().put(id, gensch);
			gensch.runTaskTimer(this.main, 20, 1);

		}

	}

}
