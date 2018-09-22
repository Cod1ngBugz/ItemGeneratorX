package net.shin1gamix.generators.Utilities;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import net.shin1gamix.generators.Core;

public class HoloGenerator extends SimpleGenerator implements Generator {

	private final Hologram holo;

	public HoloGenerator(final Core main, final Location loc, final String id, final ItemStack item, final int time,
			final int playerLimit, double velocity) {
		super(main, loc, id, item, time, playerLimit, velocity);
		this.holo = main.getHapi().createGeneratorHologram(this);
	}

	/**
	 * @return the holo
	 */
	public Hologram getHolo() {
		return this.holo;
	}

	@Override
	public void remove() {

	}

	/**
	 * Sets a generator in the config file while removing and unregistering its
	 * holograms.
	 * 
	 * @param file
	 *            -> The file to save the generator's stats.
	 */
	@Override
	public void saveGenerator(final Core main, final FileConfiguration file) {
		final String path = "Generators." + this.getId() + ".";
		file.set(path + "creation-time", this.getCreationDate());
		file.set(path + "time", this.getMaxTime()); // Setting the time
		file.set(path + "player-limit", this.getPlayerLimit()); // Setting player-limit
		file.set(path + "item", this.getItem()); // Settings the item
		file.set(path + "location", this.getLoc()); // Setting
		file.set(path + "velocity", this.getVelocity().getY());
		file.set(path + "using-hologram", true);
	}

}
