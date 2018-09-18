package net.shin1gamix.generators.Utilities;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CFG {

	private final JavaPlugin p;
	private final String filename;
	private FileConfiguration config;
	private File cfile;

	private final boolean saveResource;

	public CFG(final JavaPlugin p, final String string, final boolean saveResource) {
		this.p = p;
		this.filename = string + ".yml";
		this.saveResource = saveResource;
	}

	public void setup() {

		if (!this.p.getDataFolder().exists()) {
			this.p.getDataFolder().mkdir();
		}

		this.cfile = new File(this.p.getDataFolder(), this.filename);

		if (!this.getCfile().exists()) {
			try {
				this.getCfile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (this.saveResource) {
				this.p.saveResource(this.filename, true);
			}

		}

		this.reloadFile();
	}

	public void saveFile() {
		try {
			this.getFile().save(this.getCfile());
		} catch (IOException e) {
			// TODO File couldn't be saved for ? reason.
			e.printStackTrace();
		}
	}

	public void reloadFile() {
		this.setConfig(YamlConfiguration.loadConfiguration(this.getCfile()));
	}

	public FileConfiguration getFile() {
		return this.config;
	}

	private void setConfig(final FileConfiguration config) {
		this.config = config;
	}

	private File getCfile() {
		return this.cfile;
	}

}