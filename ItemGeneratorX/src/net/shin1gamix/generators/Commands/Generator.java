package net.shin1gamix.generators.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;

public class Generator implements CommandExecutor {

	private Core main;

	public final Core getCore() {
		return this.main;
	}

	public Generator(Core main) {
		this.main = main;
		main.getCommand("generator").setExecutor(this);
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command c, final String lb, final String[] args) {

		if (!(cs instanceof Player)) {
			return true;
		}

		final Player p = (Player) cs;

		switch (args.length) {

		case 0:
			MessagesX.HELP_FORMAT.msg(p);
			return true;

		case 1:
			if (args[0].equalsIgnoreCase("help")) {
				MessagesX.HELP_FORMAT.msg(p);
				return true;
			}
			return true;

		case 3:
			this.getCore().getGenUt().createGenX(p, args[1], args[2], null);
			return true;
		case 4:
			this.getCore().getGenUt().createGenX(p, args[1], args[2], args[3]);
			return true;
		default:
			MessagesX.HELP_FORMAT.msg(p);
			return true;

		}
	}

}
