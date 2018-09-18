package net.shin1gamix.generators.Utilities;

import java.util.Map;

import org.bukkit.Bukkit;
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
		createGenX(p, id, timeAmount, null);
	}

	public void createGenX(final Player p, final String id, final String timeAmount, String playerLimitAmount) {

		if (!Ut.isAllowed(id)) {

			return;
		}

		if (isGenerator(id)) {

			return;
		}

		final ItemStack item;
		if (Bukkit.getVersion().contains("1.8")) {

			item = p.getItemInHand();
		} else {
			item = p.getInventory().getItemInMainHand();
		}

		if (item.getType() == Material.AIR || item == null) {
			MessagesX.INVALID_ITEM.msg(p);
			return;
		}

		if (!Ut.isDouble(timeAmount)) {
			MessagesX.NO_TIME_INSERTED.msg(p);
			return;
		}

		if (playerLimitAmount != null && !Ut.isInt(playerLimitAmount)) {

			return;
		}

		if (playerLimitAmount == null) {
			playerLimitAmount = "0";
		}

		final double time = Double.valueOf(timeAmount);
		final int playerLimit = Integer.valueOf(playerLimitAmount);
		final Location loc = p.getLocation();

		final GenScheduler gensch = new GenScheduler(this.main, loc, id, item, time, playerLimit);
		gensch.runTaskTimer(this.main, 0, 1);
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
		GenScheduler.getGens().values().forEach(machine -> {
			final String path = "Generators." + machine.getId() + ".";
			file.set(path + "time", machine.getStartTime()); // Setting the time
			file.set(path + "player-limit", machine.getPlayerLimit());
			file.set(path + "item", machine.getItem());
			file.set(path + "location", machine.getLoc());
		});
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
}
