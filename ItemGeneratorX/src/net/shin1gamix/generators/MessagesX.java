package net.shin1gamix.generators;

import java.util.Map;

import org.bukkit.command.CommandSender;

import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.Ut;

public enum MessagesX {

	NO_PERMISSION("Messages.No-Permission", "&cYou are not allowed to use this command."),
	
	NOT_GENERATOR("Messages.Not-Generator", "&cThe id: &e%id% &cis not a generator!"),   
	NO_TIME_INSERTED("Messages.No-Time-Inserted","&cYou'll need to specify the time required for the generator to produce items!"),
	
	INVALID_ID("Messages.Invalid-Id","&cThe id: &e%id% &ccontains some invalid characters."),
	INVALID_TIME("Messages.Invalid-Time-Inserted","&cThe time needs to be an integer!"), 
	INVALID_ITEM("Messages.Invalid-Item","&cYou are currently holding an invalid item or none at all."),
	
	PLAYER_ONLY("Messages.Players-Only","&cOnly players are allowed to do this!"),
	PLAYER_AMOUNT_SET("Messages.Player-Amount-Set","&7Successfully set the player amount for: &e%id% &7to &e%amount%&7."),
	PLAYER_AMOUNT_SAME("Messages.Player-Amount-Same","&7The player amount for &e%id% &7is already &e%amount%&7."),
	
	INVALID_ARGUEMENTS("Messages.Invalid-Arguements","&cYou must have made a typo, take a look at your command!"),
	INVALID_PLAYER_AMOUNT("Messages.Invalid-Player-Amount","&cYou need to set a valid amount. This should be a positive number!"),
	NO_AVAILABLE_GENERATOR("Messages.No-Available-Generator", "&7No generator found. Try creating one first."),
	
	GEN_REMOVED("Messages.Generator-Removed","&cThe generator &e%id% &chas been removed."), 
	GEN_CREATED("Messages.Generator-Created","&7A generator has been created with the id &e%id%&7."), 
	GEN_ALREADY_EXISTS("Messages.Generator-Already-Exists","&cThe generator with id &e%id% &calready exists!"),
	GEN_MOVED("Messages.Generator-Moved","&7The generator: &e%id% &7has been moved to your location!"),
	
	GEN_NEAR("Messages.Generator-Near","&7Nearby generators in a range of &e%range%&7, sorted by distance: &e%generators%"),
	NO_GENERATOR_NEARBY("Messages.No-Generator-Nearby","&cNo generator was found in a range of &6%range% &cblocks."),
	
	GEN_REMOVE_HELP("Messages.Generator-Remove-Help","&cUsage: &e/gen remove <id>"),
	GEN_TOGGLEHOLO_HELP("Messages.Generator-ToggleHolo-Help","&cUsage: &e/gen toggleholo <id>"),
	GEN_MOVE_HELP("Messages.Generator-Move-Help","&cUsage: &e/gen move <id>"),
	GEN_TP_HELP("Messages.Generator-Teleport-Help","&cUsage: &e/gen tp <id>"),
	
	NO_GENERATOR_TO_CANCEL("Messages.No-Generator-To-Cancel", "&cThere are no generators to cancel!"),
	NO_GENERATOR_TO_CONTINUE("Messages.No-Generator-To-Continue", "&cThere are no generators to continue!"),
	
	GENERATOR_CANCEL_ALL("Messages.Generators-Cancel-All","&cAn amount of &6%amount% &cgenerators have been disabled!"), 
	GENERATOR_CONTINUE_ALL("Messages.Generators-Enable-All","&aAn amount of &6%amount% &agenerators have been enabled!"),
	
	GENERATOR_CONTINUE("Messages.Generator-Enable","&7The generator: &e%id% &7has been &aenabled&7."),
	GENERATOR_CANCEL("Messages.Generator-Cancel","&7The generator: &e%id% &7has been &ccancelled&7."),
	
	GENERATOR_ALREADY_CONTINUE("Messages.Generator-Already-Enable","&cThe generator: &6%id% &cis already enabled."),
	GENERATOR_ALREADY_CANCEL("Messages.Generator-Already-Cancel","&cThe generator: &6%id% &cis already cancelled."),
	
	PLAYER_TELEPORT("Messages.Player-Teleport","&7You've been teleported to the generator: &e%id%&7."), 
	PLUGIN_RELOAD("Messages.Reload","&aThe plugin has been reloaded successfully."), 
	HELP_FORMAT("Messages.Help-Format",
			"&7&m----------------------------------",
			"&3/gen help &7- Shows this menu.",
			"&3/gen tp <gen> &7- Teleports you to a gen.",
			"&3/gen toggleholo <gen> &7- Toggles a gen's hologram.",
			"&3/gen playerlimit <gen> <amount> &7- Change a playerlimit for a gen.",
			"&3/gen move <gen> &7- Moves a generator at your location.",
			"&3/gen near &7- Scans for nearby generators with a range of 50.",
			"&3/gen near <range> &7- Scans for nearby generators.",
			"&3/gen remove <gen> &7- Removes a gen.",
			"&3/gen cancel &7- Cancel all running gens.",
			"&3/gen cancel <gen> &7- Cancel a running gen.",
			"&3/gen start &7- Continue running all gens.",
			"&3/gen start <gen> &7- Continue running a gen.",
			"&3/gen save &7- Saves all generators in file.",
			"&3/gen reload &7- Reloads all generators.",
			"&3/gen create <gen> <time> &7- Creates a gen.",
			"&3/gen create <gen> <time> <players-online> &7- Creates a gen.",
            "&3/gen create <gen> <time> <players-online> <velocity>&7- Creates a gen.");
	
	/** @see #getMessages() */
	private String[] messages;
	private final String path;

	MessagesX(final String path, final String... messages) {
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
		if (target == null) {
			return;
		}
		if (this.isMultiLined()) {
			Ut.msg(target, this.getMessages(), map);
		} else {
			Ut.msg(target, this.getMessages()[0], map);
		}
	}

}
