package net.shin1gamix.generators.Commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;
import net.shin1gamix.generators.Utilities.GenScheduler;

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
				this.getCore().getGenUt().cancelTasks();
				MessagesX.TASKS_CANCELLED.msg(p);
			} else if (args[0].equalsIgnoreCase("reload")) {
				this.getCore().getSettings().reloadFile();
				this.getCore().getMessages().reloadFile();
				MessagesX.repairPaths(this.getCore().getMessages());
				this.getCore().getGenUt().cancelTasks();
				this.getCore().getGenUt().startTasks();
				MessagesX.PLUGIN_RELOAD.msg(p);
			} else if (args[0].equalsIgnoreCase("start")) {
				this.getCore().getGenUt().startTasks();
				MessagesX.TASKS_CONTINUE.msg(p);
			} else if (args[0].equalsIgnoreCase("remove")) {
				MessagesX.GEN_REMOVE_HELP.msg(p);
			} else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;
		case 2:
			if (args[0].equalsIgnoreCase("remove")) {
				this.getCore().getGenUt().removeGenX(p, args[1]);
			} else if (args[0].equalsIgnoreCase("tp")) {
				this.genTeleport(p, args);
			} else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;
		case 3:
			this.getCore().getGenUt().createGenX(p, p.getItemInHand().clone(), args[1], args[2]);
			return true;
		case 4:
			this.getCore().getGenUt().createGenX(p, p.getItemInHand().clone(), args[1], args[2], args[3]);
			return true;
		case 5:
			this.getCore().getGenUt().createGenX(p, p.getItemInHand().clone(), args[1], args[2], args[3], args[4]);
			return true;
		default:
			MessagesX.INVALID_ARGUEMENTS.msg(p);
			return true;

		}
	}

	private void genTeleport(final Player p, final String[] args) {
		if (this.getCore().getGenUt().isGenerator(args[1])) {

			final Location loc;

			if (this.getCore().getSettings().getFile().contains("Generators." + args[1] + ".location")) {
				loc = (Location) this.getCore().getSettings().getFile().get("Generators." + args[1] + ".location");
			} else {
				loc = GenScheduler.getGens().get(args[1]).getLoc();
			}

			p.teleport(loc);
			Map<String, String> map = new HashMap<>();
			map.put("%id%", args[1]);
			MessagesX.PLAYER_TELEPORT.msg(p, map);
		} else {
			MessagesX.INVALID_ID.msg(p);
		}
	}

}
