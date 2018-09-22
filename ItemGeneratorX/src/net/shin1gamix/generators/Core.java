package net.shin1gamix.generators;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.shin1gamix.generators.Commands.GenCommand;
import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.GenUtility;
import net.shin1gamix.generators.Utilities.HologramAPI;
import net.shin1gamix.generators.Utilities.Ut;

public class Core extends JavaPlugin {

	/* The files used to handle messages and generators. */
	private CFG settings = new CFG(this, "config");
	private CFG messages = new CFG(this, "messages");

	/** @see #getGenUt() */
	private final GenUtility genUt = new GenUtility(this);
	/** @see #getHapi() */
	private HologramAPI hapi;

	/**
	 * @see CFG#setup(boolean)
	 * @see #loadMessages()
	 * @see GenUtility#startGenerators()
	 * @see HologramAPI
	 * @see GenCommand
	 */
	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().getPlugin("HolographicDisplays").isEnabled()) {
			Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
				Ut.msg(player, "&cWarning! Dependancy: &3HolographicDisplays &cwasn't found...");
				Ut.msg(player, "&cDisabling &4" + this.getDescription().getName() + " &cso as to avoid errors.");
			});
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* Load all files */
		this.saveDefaultConfig();
		this.settings.setup(true);
		this.loadMessages();

		/* Since files are loaded, load all generators */
		this.genUt.initGenerators();

		/* Initialize the HAPI */
		hapi = new HologramAPI(this);

		new GenCommand(this);
	}

	/* Attempts to load the messages file and repair/load it's paths. */
	private void loadMessages() {
		this.messages.setup(false);
		MessagesX.repairPaths(this.messages);
	}

	/**
	 * @see GenUtility#saveGenerators()
	 * @see HologramsAPI#unregisterPlaceholder(org.bukkit.plugin.Plugin, String)
	 */
	@Override
	public void onDisable() {
		this.genUt.saveGenerators();
		HologramsAPI.unregisterPlaceholders(this);

	}

	/**
	 * @return the settings -> The CFG class which can be used to retrieve the
	 *         config file.
	 */
	public CFG getSettings() {
		return this.settings;
	}

	/**
	 * @return the messages -> The CFG class which can be used to retrieve the
	 *         messages file.
	 */
	public CFG getMessages() {
		return this.messages;
	}

	/**
	 * @return the genUt -> The class with various methods for a generator.
	 */
	public GenUtility getGenUt() {
		return this.genUt;
	}

	/**
	 * @return the hapi -> The class with various methods for a generator's
	 *         hologram.
	 */
	public HologramAPI getHapi() {
		return this.hapi;
	}
}
