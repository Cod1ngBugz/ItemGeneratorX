package net.shin1gamix.generators.Commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;
import net.shin1gamix.generators.Utilities.GenScheduler;

public class Generator implements CommandExecutor {

	private Core main;

	public Generator(Core main) {
		this.main = main;
		main.getCommand("generator").setExecutor(this);
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command c, final String lb, final String[] args) {
		if (!(cs instanceof Player)) {
			MessagesX.PLAYER_ONLY.msg(cs);
			return true;
		}

		final Player p = (Player) cs;

		if (!p.isOp()) {
			MessagesX.NO_PERMISSION.msg(p);
			return true;
		}

		switch (args.length) {

		case 0:

			MessagesX.HELP_FORMAT.msg(p);
			return true;

		case 1:
			if (args[0].equalsIgnoreCase("help")) {
				MessagesX.HELP_FORMAT.msg(p);
			} else if (args[0].equalsIgnoreCase("cancel")) {
				this.main.getGenUt().disableGenerators();
				MessagesX.TASKS_CANCELLED.msg(p);
			} else if (args[0].equalsIgnoreCase("reload")) {
				this.reloadFiles(p);
			} else if (args[0].equalsIgnoreCase("start")) {
				this.main.getGenUt().enableGenerators();
				MessagesX.TASKS_CONTINUE.msg(p);
			} else if (args[0].equalsIgnoreCase("remove")) {
				MessagesX.GEN_REMOVE_HELP.msg(p);
			} else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;
		case 2:

			if (args[0].equalsIgnoreCase("remove")) {
				this.main.getGenUt().removeGenerator(p, args[1]);
			} else if (args[0].equalsIgnoreCase("tp")) {
				this.genTeleport(p, args);
			} else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;

		case 3:
			this.main.getGenUt().createGenerator(p, p.getItemInHand().clone(), args[1], args[2]);
			return true;
		case 4:
			this.main.getGenUt().createGenerator(p, p.getItemInHand().clone(), args[1], args[2], args[3]);
			return true;
		case 5:
			this.main.getGenUt().createGenerator(p, p.getItemInHand().clone(), args[1], args[2], args[3], args[4]);
			return true;
		default:
			MessagesX.INVALID_ARGUEMENTS.msg(p);
			return true;

		}
	}

	private void reloadFiles(final Player p) {
		/* Reload config.yml and Refresh holograms */
		this.main.getSettings().reloadFile();
		this.main.getHapi().refresh();

		/* Reload messages.yml and Repair message paths */
		this.main.getMessages().reloadFile();
		MessagesX.repairPaths(this.main.getMessages());

		/* Send reload message */
		MessagesX.PLUGIN_RELOAD.msg(p);
	}

	private void genTeleport(final Player p, final String[] args) {
		final String id = args[1];

		/* Returns null if generator doesn't exist in map */
		final GenScheduler generator = this.main.getGenUt().getGenerator(id);

		if (generator == null) {
			MessagesX.INVALID_ID.msg(p);
			return;
		}

		p.teleport(generator.getLoc());

		final Map<String, String> map = new HashMap<>();
		map.put("%id%", args[1]);
		MessagesX.PLAYER_TELEPORT.msg(p, map);

	}

}
