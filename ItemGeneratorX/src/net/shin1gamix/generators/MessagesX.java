package net.shin1gamix.generators;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.google.common.base.Preconditions;

import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.Ut;

public enum MessagesX {

	NO_PERMISSION("Messages.No-Permission", "&cYou are not allowed to use this command."),
	NOT_GENERATOR("Messages.Not-Generator", "&cThe id: &e%id% &cis not a generator!"), 
	INVALID_ID("Messages.Invalid-Id","&cThe id: &e%id% &ccontains some invalid characters."), 
	GEN_REMOVED("Messages.Generator-Removed","&cThe generator &e%id% &chas been removed."), 
	GEN_CREATED("Messages.Generator-Created","&7A generator has been created with the id &e%id%&7."), 
	GEN_ALREADY_EXISTS("Messages.Generator-Already-Exists","&cThe generator with id &e%id% &calready exists!"), 
	NO_TIME_INSERTED("Messages.No-Time-Inserted","&cYou'll need to specify the time required for the generator to produce items!"), 
	INVALID_TIME("Messages.Invalid-Time-Inserted","&cThe time needs to be an integer!"), 
	INVALID_ITEM("Messages.Invalid-Item","&cYou are currently holding an invalid item or none at all."), 
	PLAYER_ONLY("Messages.Players-Only","&cOnly players are allowed to do this!"), 
	INVALID_ARGUEMENTS("Messages.Invalid-Arguements","&cYou must have made a typo, take a look at your command!"), 
	GEN_REMOVE_HELP("Messages.Generator-Remove-Help","&cUsage: &e/gen remove <id>"), 
	TASKS_CANCELLED("Messages.Generators-Disabled","&cAll generators have been disabled!"), 
	TASKS_CONTINUE("Messages.Generators-Enabled","&aAll generators have been enabled!"), 
	PLAYER_TELEPORT("Messages.Player-Teleport","&7You've been teleported to the generator &e%id%&7."), 
	PLUGIN_RELOAD("Messages.Reload","&aThe plugin has been reloaded successfully."), 
	HELP_FORMAT("Messages.Help-Format",
			"&7&m----------------------------------",
			"&3/gen help &7- Shows this menu.",
			"&3/gen tp <gen> &7- Teleports you to a gen.",
			"&3/gen remove <gen> &7- Removes a gen.",
			"&3/gen cancel &7- Cancel all running gens.",
			"&3/gen start &7- Continue running all gens.",
			"&3/gen create <gen> <time> &7- Creates a gen.",
			"&3/gen create <gen> <time> <players-online> &7- Creates a gen.",
			"&3/gen create <gen> <time> <players-online> <velocity>&7- Creates a gen.");

	/** @see #getMessages() */
	private String[] messages;
	private final String path;

	MessagesX(final String path, final String... messages) {
		/* Most likely never going to work, this may be removed in the future. */
		Preconditions.checkArgument(messages.length >= 1, "messages array is empty or no message was found.");
		this.messages = messages;
		this.path = path;
	}

	/**
	 * @return boolean -> Whether or not the messages array contains more than 1
	 *         element. If true, it's more than 1 message/string.
	 */
	private boolean isMultiLined() {
		return this.messages.length > 1;
	}

	/**
	 * @param cfg
	 * @see #setPathToFile(CFG, MessagesX)
	 * @see #setMessageToFile(CFG, MessagesX)
	 */
	public static void repairPaths(final CFG cfg) {
		for (MessagesX mX : MessagesX.values()) {
			if (cfg.getFile().contains(mX.getPath())) {
				setPathToFile(cfg, mX);
				continue;
			}
			setMessageToFile(cfg, mX);
		}
		cfg.saveFile();
	}

	/**
	 * Sets a message from the MessagesX enum to the file.
	 * 
	 * @param cfg
	 * @param mX
	 */
	private static void setMessageToFile(final CFG cfg, final MessagesX mX) {
		if (mX.getMessages().length > 1) {
			cfg.getFile().set(mX.getPath(), mX.getMessages());
		} else {
			cfg.getFile().set(mX.getPath(), mX.getMessages()[0]);
		}
	}

	/**
	 * Sets the current MessagesX messages to a string/list retrieved from the
	 * messages file.
	 * 
	 * @param cfg
	 * @param mX
	 */
	private static void setPathToFile(final CFG cfg, final MessagesX mX) {
		if (Ut.isList(cfg.getFile(), mX.getPath())) {
			mX.setMessages(cfg.getFile().getStringList(mX.getPath()).toArray(new String[0]));
		} else {
			mX.setMessages(cfg.getFile().getString(mX.getPath()));
		}
	}

	/**
	 * @return the path -> The path of the enum in the file.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the messages -> The messages array contains all string(s).
	 */
	public String[] getMessages() {
		return this.messages;
	}

	/**
	 * Sets the current messages to a different string array.
	 * 
	 * @param messages
	 */
	public void setMessages(final String[] messages) {
		this.messages = messages;
	}

	/**
	 * Sets the string message to a different string assuming that the array has
	 * only 1 element.
	 * 
	 * @param messages
	 */
	public void setMessages(final String messages) {
		this.messages[0] = messages;
	}

	/**
	 * @param target
	 * @see #msg(CommandSender, Map)
	 */
	public void msg(final CommandSender target) {
		msg(target, null);
	}

	/**
	 * Sends a translated message to a target commandsender with placeholders gained
	 * from a map. If the map is null, no placeholder will be set and it will still
	 * execute.
	 * 
	 * @param target
	 * @param map
	 */
	public void msg(final CommandSender target, final Map<String, String> map) {
		if (this.isMultiLined()) {
			Ut.msg(target, this.getMessages(), map);
		} else {
			Ut.msg(target, this.getMessages()[0], map);
		}
	}

}
