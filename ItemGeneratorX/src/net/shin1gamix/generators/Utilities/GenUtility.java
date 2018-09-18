package net.shin1gamix.generators.Utilities;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;

public class GenUtility {

	private final Core main;

	public GenUtility(Core main) {
		this.main = main;

	}

	// 0 1 2 3
	// /gen create boss 0.01 0

	public void createGenX(final Player p, final String id, final String timeAmount) {
		createGenX(p, p.getItemInHand().clone(), id, timeAmount, null);
	}

	public void createGenX(final Player p, final ItemStack item, final String id, final String timeAmount,
			String playerLimitAmount) {

		if (!Ut.isAllowed(id)) {

			return;
		}

		if (isGenerator(id)) {

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

		final int time = Integer.valueOf(timeAmount);
		if (time < 1) {

			return;
		}

		if (playerLimitAmount != null && !Ut.isInt(playerLimitAmount)) {

			return;
		}

		if (playerLimitAmount == null) {
			playerLimitAmount = "1";
		}

		final int playerLimit = Integer.valueOf(playerLimitAmount);
		final Location loc = p.getLocation();

		final GenScheduler gensch = new GenScheduler(this.main, loc, id, item, time, playerLimit);
		GenScheduler.getGens().put(id, gensch);
		gensch.runTaskTimer(this.main, 20, 1);
	}

	public boolean isGenerator(final String id) {
		return GenScheduler.getGens().keySet().stream().anyMatch(id::equalsIgnoreCase);
	}

	public void removeGeneratorX(final String id) {
		final Map<String, GenScheduler> gens = GenScheduler.getGens();
		gens.get(id).cancel();
		gens.remove(id);
	}

	public void saveMachines() {
		final FileConfiguration file = this.main.getSettings().getFile();
		for (GenScheduler machine : GenScheduler.getGens().values()) {
			if (file.contains("Generators." + machine.getId())) {
				continue;
			}
			final String path = "Generators." + machine.getId() + ".";
			file.set(path + "creation-time", machine.getCreationDate());
			file.set(path + "time", machine.getStartTime()); // Setting the time
			file.set(path + "player-limit", machine.getPlayerLimit());
			file.set(path + "item", machine.getItem());
			file.set(path + "location", machine.getLoc());
		}
		this.main.getSettings().saveFile();
	}

	public void cancelTasks() {
		final Map<String, GenScheduler> gens = GenScheduler.getGens();
		if (gens.isEmpty()) {
			// TODO No generators were working.
			return;
		}
		gens.values().forEach(task -> task.cancel());

		// Iterator<GenScheduler> itr = gens.values().iterator();
		// while (itr.hasNext()) {
		// final GenScheduler genx = itr.next();
		// genx.cancel();
		// }

		// TODO all generators were cancelled.

	}

	public void startsMachines() {
		final FileConfiguration file = this.main.getSettings().getFile();
		final Set<String> generators = file.getConfigurationSection("Generators").getKeys(false);
		generators.forEach(id -> {
			final String path = "Generators." + id + ".";
			final Location loc = (Location) file.get(path + "location");
			final ItemStack item = file.getItemStack(path + "item");
			final int time = file.getInt(path + "time");
			final int playerLimit = file.getInt(path + "player-limit");
			final GenScheduler gensch = new GenScheduler(this.main, loc, id, item, time, playerLimit);
			GenScheduler.getGens().put(id, gensch);
			gensch.runTaskTimer(this.main, 20, 1);;
		});

	}
}
