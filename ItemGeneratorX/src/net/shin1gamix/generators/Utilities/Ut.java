package net.shin1gamix.generators.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * {@link https://www.spigotmc.org/members/2008choco.31119/}
 * 
 * Declared class as final so as to prevent it from being extended by a
 * subclass.
 */
public final class Ut {

	/*
	 * An empty, private constructor as suggested by (link above) to avoid
	 * instantiation.
	 */
	private Ut() {
		throw new UnsupportedOperationException();
	}

	public static boolean isInt(final String input) {
		try {
			Integer.valueOf(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDouble(final String input) {
		try {
			Double.valueOf(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String getIP() {
		try {
			return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))
					.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks whether a path from the file is an instance of ArrayList or not.
	 * 
	 * @param file
	 *            The file to use.
	 * @param path
	 *            The path to check.
	 * @see FileConfiguration#get(String arg0)
	 * @return boolean -> Whether the path is an array list or not
	 * @since 0.1
	 */
	public static boolean isList(final FileConfiguration file, final String path) {
		final boolean contains = file.contains(path);
		final boolean isList = isList(file.get(path));
		return contains && isList;
	}

	/**
	 * Checks whether a path from the file is an instanceof ArrayList or not.
	 * 
	 * @param obj
	 *            The object to compare
	 * @return boolean -> Whether the object is an instance of arraylist or not
	 * @since 0.1
	 */
	public static boolean isList(final Object obj) {
		return obj instanceof ArrayList;

	}

	public static void bcMsg(final String input) {
		Bukkit.broadcastMessage(tr(input));
	}

	public static void bcMsg(final List<String> input) {
		tr(input).forEach(Bukkit::broadcastMessage);
	}

	public static void bcMsg(final Object input) {
		bcMsg(String.valueOf(input));
	}

	public static void msgConsole(final String message) {
		Bukkit.getServer().broadcastMessage(tr(message));
	}

	public static void msgConsole(final List<String> message) {
		tr(message).forEach(Bukkit.getServer().getConsoleSender()::sendMessage);
	}

	public static void msg(final CommandSender target, final String message) {
		target.sendMessage(tr(message));
	}

	public static void msg(final CommandSender target, final List<String> message) {
		tr(message).forEach(target::sendMessage);
	}

	public static void msg(final CommandSender target, final String[] message) {
		Stream.of(message).forEach(loop -> msg(target, loop));
	}

	/*
	 * Hell starts here, enjoy. TODO add comments in ALL methods.
	 */

	public static void msg(final CommandSender target, final String message, final Map<String, String> map) {
		msg(target, placeHolder(message, map));
	}

	public static void msg(final CommandSender target, final String[] message, final Map<String, String> map) {
		msg(target, placeHolder(message, map));
	}

	public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map) {
		msg(target, placeHolder(message, map));
	}

	public static String placeHolder(String str, final Map<String, String> map) {
		if (map == null) {
			return str;
		}
		for (final Entry<String, String> entr : map.entrySet()) {
			str = str.replace(entr.getKey(), entr.getValue());
		}
		return str;
	}

	public static String[] placeHolder(final String[] str, final Map<String, String> map) {
		if (map == null) {
			return str;
		}
		for (final Entry<String, String> entr : map.entrySet()) {
			for (int i = 0; i < str.length; i++) {
				str[i] = str[i].replace(entr.getKey(), entr.getValue());
			}
		}
		return str;
	}

	public static List<String> placeHolder(final List<String> coll, final Map<String, String> map) {
		if (map == null) {
			return coll;
		}
		return coll.stream().map(x -> placeHolder(x, map)).collect(Collectors.toList());
	}

	/**
	 * Checks if the string provided has any illegal chars.
	 *
	 * @param message
	 *            Which chars to check
	 * 
	 * @return Boolean for whether the string hasn't any invalid characters.
	 * 
	 */
	public static boolean isAllowed(final String message) {
		return message.isEmpty() || message.replace(" ", "").equalsIgnoreCase("") ? true
				: Pattern.compile("[a-zA-Z0-9]*").matcher(message).matches();
	}

	/**
	 * Caps the first letter of a string.
	 *
	 * @param string
	 *            The string to be capped.
	 * @param reset
	 *            Boolean to if the string should be lowercase (reseted)
	 * 
	 * @return A given string with it's first letter capped.
	 * 
	 */
	public static String capFirst(String string, final boolean reset) {
		if (reset) {
			string = string.toLowerCase();
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * A random int between two values (minimum int, maximum int)
	 *
	 * @param min
	 *            The minimum amount the int can be
	 * @param max
	 *            The maxmimum amount the int can be
	 * @throws NullPointerException
	 *             If the minimum int is higher than max
	 * 
	 * @return A random number between two integers
	 * 
	 */
	public static int getRandomInt(final int min, final int max) {
		if (max <= min) {
			throw new NumberFormatException(
					"The minimum number can't be higher than the maximum, min:" + min + " - max:" + max);
		}
		return ThreadLocalRandom.current().nextInt(max - min) + min;
	}

	public static String tr(final String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String[] tr(final String[] msg) {
		for (int i = 0; i < msg.length; i++) {
			msg[i] = tr(msg[i]);
		}
		return msg;
	}

	/**
	 * Returns a collection to be translated using Java 8.
	 * 
	 * @param coll
	 *            The collection to be translated
	 * 
	 * @return A translated message
	 */
	public static List<String> tr(final List<String> coll) {
		return coll.stream().map(str -> tr(str)).collect(Collectors.toList());
	}

}
