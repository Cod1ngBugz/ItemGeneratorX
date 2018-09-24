package net.shin1gamix.generators.Generators;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.shin1gamix.generators.Core;
import net.shin1gamix.generators.Utilities.CFG;

public class HoloGenerator extends SimpleGenerator implements Generator {

	private Hologram holo;

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
	public void setLoc(final Location loc) {
		super.setLoc(loc);
		this.getHolo().delete();
		this.holo = this.getCore().getHapi().createGeneratorHologram(this);
	}

	@Override
	public void stopTaskAndRemoveFile() {
		super.stopTaskAndRemoveFile();
		this.removeLines();
		this.unregisterPlaceHolder();
	}

	public void removeLines() {
		this.getHolo().clearLines();
	}

	public void unregisterPlaceHolder() {
		HologramsAPI.unregisterPlaceholder(this.getCore(),
				this.getCore().getHapi().getTimeStringPlaceholder(this.getId()));
	}

	/**
	 * Sets a generator in the config file while removing and unregistering its
	 * holograms.
	 * 
	 * @param file
	 *            -> The file to save the generator's stats.
	 */
	@Override
	public void saveGenerator() {
		final CFG cfg = this.getCore().getSettings();
		final FileConfiguration file = cfg.getFile();
		final String path = "Generators." + this.getId() + ".";
		if (!file.contains(path + "creation-time")) {
			file.set(path + "creation-time", this.getCreationDate());
		}
		file.set(path + "time", this.getMaxTime()); // Setting the time
		file.set(path + "player-limit", this.getPlayerLimit()); // Setting player-limit
		file.set(path + "item", this.getItem()); // Settings the item
		file.set(path + "working", this.isWorking());
		file.set(path + "location", this.getLoc()); // Setting
		file.set(path + "velocity", this.getVelocity().getY());
		file.set(path + "using-hologram", true);
		cfg.saveFile();
	}

}
