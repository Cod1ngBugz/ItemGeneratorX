package net.shin1gamix.generators;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

import net.shin1gamix.generators.Commands.Generator;
import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.GenUtility;
import net.shin1gamix.generators.Utilities.HologramAPI;

public class Core extends JavaPlugin {

	private CFG settings = new CFG(this, "config");
	private CFG messages = new CFG(this, "messages");

	private final GenUtility genUt = new GenUtility(this);
	private HologramAPI hapi;

	@Override
	public void onEnable() {

		this.saveDefaultConfig();
		this.settings.setup(true);
		this.messages.setup(false);

		new Generator(this);

		MessagesX.repairPaths(this.getMessages());
		hapi = new HologramAPI(this);
		this.getGenUt().startsMachines();
		/*
		 * new BukkitRunnable() {
		 * 
		 * @Override public void run() { for (final GenScheduler mach :
		 * GenScheduler.getGens().values()) { final Hologram hologram = mach.getHolo();
		 * for (int i = 0; i < hologram.size(); i++) { final HologramLine hl =
		 * hologram.getLine(i); if (!(hl instanceof TextLine)) continue; final TextLine
		 * line = (TextLine) hl; if (line.getText().contains("%time-left%")) {
		 * hologram.insertTextLine(i, line.getText()); } } } } }.runTaskTimer(this,
		 * 100,50);
		 */

		HologramsAPI.registerPlaceholder(this, "%online%", .5, new PlaceholderReplacer() {

			@Override
			public String update() {
				return Bukkit.getOnlinePlayers().size() + "";
			}
		});
		HologramsAPI.registerPlaceholder(this, "%needed%", .5, new PlaceholderReplacer() {

			@Override
			public String update() {
				return Bukkit.getMaxPlayers() + "";
			}
		});
	}

	@Override
	public void onDisable() {
		this.getGenUt().saveMachines();
		try {
			HologramsAPI.getRegisteredPlaceholders(this).clear();
			HologramsAPI.unregisterPlaceholders(this);
		} catch (Exception e) {
		}
	}

	public CFG getSettings() {
		return this.settings;
	}

	public CFG getMessages() {
		return this.messages;
	}

	/**
	 * @return the genUt
	 */
	public GenUtility getGenUt() {
		return this.genUt;
	}

	/**
	 * @return the hapi
	 */
	public HologramAPI getHapi() {
		return hapi;
	}

}
