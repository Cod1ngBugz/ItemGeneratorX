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

		/* Load all files */
		this.saveDefaultConfig();
		this.settings.setup(true);
		this.loadMessages();

		/* Since files are loaded, load all generators*/
		this.getGenUt().startGenerators();
		
		/* Initialize the HAPI */
		hapi = new HologramAPI(this);

		new Generator(this);
	}

	private void loadMessages() {
		this.messages.setup(false);
		MessagesX.repairPaths(this.getMessages());
	}

	@Override
	public void onDisable() {
		this.getGenUt().saveGenerators();
		HologramsAPI.unregisterPlaceholders(this);

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
