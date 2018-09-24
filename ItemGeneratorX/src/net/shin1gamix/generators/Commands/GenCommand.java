package net.shin1gamix.generators.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.MessagesX;
import net.shin1gamix.generators.Generators.Generator;
import net.shin1gamix.generators.Generators.HoloGenerator;
import net.shin1gamix.generators.Generators.SimpleGenerator;
import net.shin1gamix.generators.Utilities.Ut;

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
				this.main.getGenUt().cancelGenerators(p);
			}

			/* Enable all generators. */
			else if (args[0].equalsIgnoreCase("start")) {
				this.main.getGenUt().enableGenerators(p);
			}

			/* Reload all files. */
			else if (args[0].equalsIgnoreCase("reload")) {
				this.reloadFiles(p);
			}

			/* Saves in file all generators. */
			else if (args[0].equalsIgnoreCase("save")) {
				this.main.getGenUt().saveGenerators();
			}

			/* Send usage of remove command. */
			else if (args[0].equalsIgnoreCase("remove")) {
				MessagesX.GEN_REMOVE_HELP.msg(p);
			}

			/* Send usage of toggle hologram command. */
			else if (args[0].equalsIgnoreCase("toggleholo")) {
				MessagesX.GEN_TOGGLEHOLO_HELP.msg(p);
			}

			/* Send usage of move command. */
			else if (args[0].equalsIgnoreCase("move")) {
				MessagesX.GEN_MOVE_HELP.msg(p);
			}

			/* Send usage of teleport command. */
			else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
				MessagesX.GEN_TP_HELP.msg(p);
			}

			/* Lists all nearby generators. */
			else if (args[0].equalsIgnoreCase("near")) {

				this.getNearByGenerators(p, null);

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
			else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
				this.genTeleport(p, args);
			}

			/* Attempt to teleport to a generator. */
			else if (args[0].equalsIgnoreCase("near")) {
				this.getNearByGenerators(p, args[1]);
			}

			/* Attempt to toggle the hologram. */
			else if (args[0].equalsIgnoreCase("toggleholo")) {
				this.toggleHolo(p, args);
			}

			/* Disable a generator. */
			else if (args[0].equalsIgnoreCase("cancel")) {
				this.main.getGenUt().cancelGenerator(p, args[1]);
			}

			/* Enable a generator. */
			else if (args[0].equalsIgnoreCase("start")) {
				this.main.getGenUt().enableGenerator(p, args[1]);
			}

			/* Moves a generator */
			else if (args[0].equalsIgnoreCase("move")) {
				this.moveGenerator(p, args);
			}

			/* No useful command found. Send invalid arg message. */
			else {
				MessagesX.INVALID_ARGUEMENTS.msg(p);
			}

			return true;

		/* Create a generator with all available arguements. */
		case 3: // Assume player typed /gen create <id>
			/* Attempt to set a new player limit. */
			if (args[0].equalsIgnoreCase("playerlimit")) {
				this.changePlayerLimit(p, args);
				return true;
			}

			/* Creates a new generator. */
			else if (args[0].equalsIgnoreCase("create")) {
				this.main.getGenUt().createGenerator(p, args[1], args[2]);
				return true;
			}

		case 4: // Assume player typed /gen create <id> <max-time> <player-limit>

			/* Creates a new generator. */
			if (args[0].equalsIgnoreCase("create")) {
				this.main.getGenUt().createGenerator(p, args[1], args[2], args[3]);
				return true;
			}

		case 5: // Assume player typed /gen create <id> <max-time> <player-limit> <velocity>

			/* Creates a new generator. */
			if (args[0].equalsIgnoreCase("create")) {
				this.main.getGenUt().createGenerator(p, args[1], args[2], args[3]);
				return true;
			}

			/* No useful command found. Send invalid arg message. */
		default:
			MessagesX.INVALID_ARGUEMENTS.msg(p);
			return true;

		}
	}

	private void getNearByGenerators(final Player p, final String radius) {
		final Location ploc = p.getLocation();

		String amount = radius;

		if (amount == null) {
			amount = "50";
		}

		if (!Ut.isInt(amount)) {
			amount = "50";
		}

		final int amountx = Integer.valueOf(amount);

		final Map<String, String> map = new HashMap<>();
		map.put("%range%", String.valueOf(amountx));

		final String result = Generator.getGens().values().stream()
				.filter(generator -> generator.getLoc().getWorld().equals(ploc.getWorld()))
				.filter(generator -> generator.getLoc().distance(ploc) <= amountx)
				.sorted(Comparator.comparingDouble(generator -> generator.getLoc().distance(ploc)))
				.map(Generator::getId).collect(Collectors.joining(", "));

		if (result.isEmpty()) {
			MessagesX.NO_GENERATOR_NEARBY.msg(p, map);
			return;
		}

		map.put("%generators%", result);
		MessagesX.GEN_NEAR.msg(p, map);

	}

	private void moveGenerator(final Player p, final String[] args) {
		final String id = args[1];
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		final Generator gen = this.main.getGenUt().getGenerator(id);

		if (gen == null) {
			MessagesX.NOT_GENERATOR.msg(p, map);
			return;
		}

		map.put("%id%", gen.getId());

		gen.setLoc(p.getLocation());
		MessagesX.GEN_MOVED.msg(p, map);

	}

	private void changePlayerLimit(final Player p, final String[] args) {
		final String id = args[1];
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		final Generator gen = this.main.getGenUt().getGenerator(id);

		if (gen == null) {
			MessagesX.NOT_GENERATOR.msg(p, map);
			return;
		}

		map.put("%id%", gen.getId());

		final String amount = args[2];
		if (!Ut.isInt(amount) || amount == null) {
			MessagesX.INVALID_PLAYER_AMOUNT.msg(p);
			return;
		}

		final int playerAmount = Integer.valueOf(amount);
		if (playerAmount <= 0) {
			MessagesX.INVALID_PLAYER_AMOUNT.msg(p);
			return;
		}

		map.put("%amount%", amount);

		if (playerAmount == gen.getPlayerLimit()) {
			MessagesX.PLAYER_AMOUNT_SAME.msg(p, map);
			return;
		}

		gen.setPlayerLimit(playerAmount);
		if (gen instanceof HoloGenerator) {
			this.main.getHapi().refresh(gen);
		}

		MessagesX.PLAYER_AMOUNT_SET.msg(p, map);

	}

	private void toggleHolo(final Player p, final String[] args) {
		final String id = args[1];
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);
		if (!this.main.getGenUt().isGenerator(id, true)) {
			MessagesX.NOT_GENERATOR.msg(p, map);
			return;
		}

		final Generator generator = this.main.getGenUt().getGenerator(id);
		generator.stopTaskAndRemoveFile();
		generator.removeFromMap();
		if (generator instanceof HoloGenerator) {
			new SimpleGenerator(this.main, generator.getLoc(), generator.getId(), generator.getItem(),
					generator.getMaxTime(), generator.getPlayerLimit(), generator.getVelocity().getY());
		} else {
			new HoloGenerator(this.main, generator.getLoc(), generator.getId(), generator.getItem(),
					generator.getMaxTime(), generator.getPlayerLimit(), generator.getVelocity().getY());
		}

	}

	/* Reloads and fixes all files and holograms. */
	private void reloadFiles(final Player p) {
		/* Reload config.yml */
		this.main.getSettings().reloadFile();

		/* Reload messages.yml and Repair message paths */
		this.main.getMessages().reloadFile();
		MessagesX.repairPaths(this.main.getMessages());

		final Collection<Generator> gens = new ArrayList<>(Generator.getGens().values());

		gens.forEach(gen -> {
			gen.getTask().cancel();
			if (gen instanceof HoloGenerator) {
				((HoloGenerator) gen).removeLines();
				((HoloGenerator) gen).unregisterPlaceHolder();
			}
			gen.removeFromMap();
		});

		this.main.getGenUt().initGenerators();

		/* Send reload message */
		MessagesX.PLUGIN_RELOAD.msg(p);
	}

	/* Attempts to teleport a player in the location of the generator. */
	private void genTeleport(final Player p, final String[] args) {
		final String id = args[1];

		/* Returns null if generator doesn't exist in map */
		final Generator generator = this.main.getGenUt().getGenerator(id);

		if (generator == null) {
			MessagesX.NOT_GENERATOR.msg(p);
			return;
		}

		final Location loc = generator.getLoc();
		loc.setPitch(0);
		loc.setYaw(0);
		p.teleport(loc);
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", generator.getId());
		MessagesX.PLAYER_TELEPORT.msg(p, map);

	}

}
