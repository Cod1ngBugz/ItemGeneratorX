package net.shin1gamix.generators;

import org.bukkit.plugin.java.JavaPlugin;

import net.shin1gamix.generators.Commands.Generator;
import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.GenUtility;

public class Core extends JavaPlugin {

	private CFG settings = new CFG(this, "config", true);
	private CFG messages = new CFG(this, "messages", false);

	private final GenUtility genUt = new GenUtility(this);

	@Override
	public void onEnable() {

		this.saveDefaultConfig();
		this.settings.setup();
		this.messages.setup();

		new Generator(this);

		MessagesX.repairPaths(this.getMessages());
		this.getGenUt().startsMachines();
	}

	@Override
	public void onDisable() {
		this.getGenUt().saveMachines();
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

}
