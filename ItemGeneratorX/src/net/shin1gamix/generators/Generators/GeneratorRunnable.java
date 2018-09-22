package net.shin1gamix.generators.Generators;

public class GeneratorRunnable implements Runnable {

	private final Generator generator;

	public GeneratorRunnable(Generator generator) {
		this.generator = generator;
	}

	public void run() {
		/* Is the generator working? */
		if (!this.generator.isWorking()) {
			return;
		}

		/* Are there enough players? */
		if (!this.generator.areEnoughPlayers()) {
			return;
		}

		/* Is the current time less than max? Meaning it's not ready to drop yet. */
		if (this.generator.getCurrentTime() < this.generator.getMaxTime()) {
			this.generator.setCurrentTime(this.generator.getCurrentTime() + 1);
			return;
		}

		/* Rest the time and drop item */
		this.generator.setCurrentTime(0);
		this.generator.getLoc().getWorld().dropItemNaturally(this.generator.getLoc(), this.generator.getItem())
				.setVelocity(this.generator.getVelocity()); // Drop item
	}

}