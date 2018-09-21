package net.shin1gamix.generators.Commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;
import net.shin1gamix.generators.Utilities.Generator;

public class GenCommand implements CommandExecutor {

	private final Core main;

	public GenCommand(final Core main) {
		this.main = main;
		main.getCommand("itemgeneratorx").setExecutor(this);
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command c, final String lb, final String[] args) {
		/* Is a non player attempting to use the command? */
		if (!(cs instanceof Player)) {
			MessagesX.PLAYER_ONLY.msg(cs);
			return true;
		}

		final Player p = (Player) cs;

		/* Is the player an operator? */
		if (!p.isOp()) {
			MessagesX.NO_PERMISSION.msg(p);
			return true;
		}

		switch (args.length) {

		case 0: // Assume player typed /gen

			/* Send help format message. */
			MessagesX.HELP_FORMAT.msg(p);
			return true;

		case 1: // Assume player typed /gen args0

			/* Send help format message. */
			if (args[0].equalsIgnoreCase("help")) {
				MessagesX.HELP_FORMAT.msg(p);
			}

			/* Disable all generators. */
			else if (args[0].equalsIgnoreCase("cancel")) {
				this.main.getGenUt().disableGenerators(p);
			}

			/* Enable all generators. */
			else if (args[0].equalsIgnoreCase("start")) {
				this.main.getGenUt().enableGenerators(p);
			}

			/* Reload all files. */
			else if (args[0].equalsIgnoreCase("reload")) {
				this.reloadFiles(p);
			}

			/* Send usage of remove command. */
			else if (args[0].equalsIgnoreCase("remove")) {
				MessagesX.GEN_REMOVE_HELP.msg(p);
			}

			/* No useful command found. Send invalid arg message. */
			else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;

		case 2: // Assume player typed /gen args0 args1

			/* Attempt to remove a generator completely. */
			if (args[0].equalsIgnoreCase("remove")) {
				this.main.getGenUt().removeGenerator(p, args[1]);
			}

			/* Attempt to teleport to a generator. */
			else if (args[0].equalsIgnoreCase("tp")) {
				this.genTeleport(p, args);
			}

			/* No useful command found. Send invalid arg message. */
			else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;

		/* Create a generator with all available arguements. */
		case 3: // Assume player typed /gen create <id>
			this.main.getGenUt().createGenerator(p, args[1], args[2]);
			return true;
		case 4: // Assume player typed /gen create <id> <max-time> <player-limit>
			this.main.getGenUt().createGenerator(p, args[1], args[2], args[3]);
			return true;
		case 5: // Assume player typed /gen create <id> <max-time> <player-limit> <velocity>
			this.main.getGenUt().createGenerator(p, args[1], args[2], args[3], args[4]);
			return true;

		/* No useful command found. Send invalid arg message. */
		default:
			MessagesX.INVALID_ARGUEMENTS.msg(p);
			return true;

		}
	}

	/* Reloads and fixes all files and holograms. */
	private void reloadFiles(final Player p) {
		/* Reload config.yml and Refresh holograms */
		this.main.getSettings().reloadFile();
		this.main.getHapi().refreshAll();

		/* Reload messages.yml and Repair message paths */
		this.main.getMessages().reloadFile();
		MessagesX.repairPaths(this.main.getMessages());

		/* Send reload message */
		MessagesX.PLUGIN_RELOAD.msg(p);
	}

	/* Attempts to teleport a player in the location of the generator. */
	private void genTeleport(final Player p, final String[] args) {
		final String id = args[1];

		/* Returns null if generator doesn't exist in map */
		final Generator generator = this.main.getGenUt().getGenerator(id);

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
