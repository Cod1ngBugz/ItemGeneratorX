package net.shin1gamix.generators;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.google.common.base.Preconditions;

import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.Ut;

public enum MessagesX {

	NO_PERMISSION("Messages.No-Permission", "&cYou are not allowed to use this command."),
	NOT_MACHINE("Messages.Not-Machine", "&cThe id: &e%id% &cis not a machine!"),
	INVALID_ID("Messages.Invalid-Id", "&cThe id: &e%id% &ccontains some invalid characters."),
	GEN_REMOVED("Messages.Generator-Removed", "&cThe generator &e%id% &chas been removed."),
	GEN_CREATED("Messages.Generator-Created", "&7A generator has been created with the id &e%id%&7."),
	GEN_EXISTS("Messages.Generator-Already-Exists","&cThe generator with id &e%id% &calready exists!"),
	NO_TIME_INSERTED("Messages.No-Time-Inserted","&cYou'll need to specify the time required for the generator to produce items!"),
	INVALID_TIME("Messages.Invalid-Time-Inserted","&cThe time needs to be an integer!"),
	INVALID_ITEM("Messages.Invalid-Item","&cYou are currently holding an invalid item or none at all."),
	INVALID_ARGUEMENTS("Messages.Invalid-Arguements","&cYou must have made a typo, take a look at your command!"),
	GEN_REMOVE_HELP("Messages.Generator-Remove-Help","&cUsage: &e/gen remove <id>"),
	TASKS_CANCELLED("Messages.Generators-Disabled","&cAll generators have been disabled!"),
	TASKS_CONTINUE("Messages.Generators-Enabled","&aAll generators have been enabled!"),
	PLAYER_TELEPORT("Messages.Player-Teleport","&7You've been teleported to the generator &e%id%&7."),
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

	private String[] messages;
	private final String path;

	MessagesX(final String path, final String... messages) {
		Preconditions.checkArgument(messages.length >= 1, "messages array is empty or no message was found.");
		this.messages = messages;
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(final String[] messages) {
		this.messages = messages;
	}

	public void setMessages(final String messages) {
		this.messages[0] = messages;
	}

	public void msg(final CommandSender target) {
		msg(target, null);
	}

	public void msg(final CommandSender target, final Map<String, String> map) {
		if (this.isMultiLined()) {
			Ut.msg(target, this.getMessages(), map);
		} else {
			Ut.msg(target, this.getMessages()[0], map);
		}
	}

	private boolean isMultiLined() {
		return messages.length > 1;
	}

	public static void repairPaths(final CFG cfg) {
		boolean altered = false;

		for (MessagesX mX : MessagesX.values()) {

			if (!cfg.getFile().contains(mX.getPath())) {
				if (mX.getMessages().length > 1) {
					cfg.getFile().set(mX.getPath(), mX.getMessages());
				} else {
					cfg.getFile().set(mX.getPath(), mX.getMessages()[0]);
				}
				altered = true;
				continue;
			}

			if (Ut.isList(cfg.getFile(), mX.getPath())) {
				mX.setMessages(cfg.getFile().getStringList(mX.getPath()).toArray(new String[0]));
			} else {
				mX.setMessages(cfg.getFile().getString(mX.getPath()));
			}
		}

		if (altered) {
			cfg.saveFile();
		}

	}
}
