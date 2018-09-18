package net.shin1gamix.generators;

import java.util.Map;

import org.bukkit.command.CommandSender;

import net.shin1gamix.generators.Utilities.CFG;
import net.shin1gamix.generators.Utilities.Ut;

public enum MessagesX {

	NO_PERMISSION("Messages.No-Permission", "&cYou are not allowed to use this command."), NO_TIME_INSERTED(
			"Messages.No-Time-Inserted",
			"&cYou'll need to specify the time required for the generator to produce items!"), INVALID_ITEM(
					"Messages.Invalid-Item",
					"&cYou are currently holding an invalid item or none at all."), HELP_FORMAT("Messages.Help-Format",
							"&7&m-------------------", "&3/gen help &7- Shows this menu.",
							"&3/gen tp <gen> &7- Teleports you to a gen.", "&3/gen remove <gen> &7- Removes a gen.",
							"&3/gen create <gen> <time> <required-players-online> &7- Creates a gen.",
							"&3/gen help &7- Shows this menu.");

	public static void repairPaths(final CFG cfg) {
		boolean altered = false;
		for (MessagesX mX : MessagesX.values()) {
			if (cfg.getFile().contains(mX.getPath())) {
				if (Ut.isList(cfg.getFile(), mX.getPath())) {
					mX.setMessages(cfg.getFile().getStringList(mX.getPath()).toArray(new String[0]));
				} else {
					mX.setMessages(cfg.getFile().getString(mX.getPath()));
				}
				continue;
			}
			cfg.getFile().set(mX.getPath(), mX.getMessages());
			altered = true;
		}
		if (altered) {
			cfg.saveFile();

		}
	}

	private String[] messages;
	private final String path;

	MessagesX(final String path, final String... messages) {
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

}
