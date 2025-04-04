package pureplus;

public class SDParam
{
	String	prompt;
	String	negativePrompt;
	long	seed;
	int	width;
	int	height;
	String	sampler;
	int	cfgs;
	int	steps;

	public SDParam() {
	}

	/**
	 * Normal Prompt
	 */
	public void setPrompt(String prom) {
		this.prompt = prom;
	}

	/**
	 * Get Normal Prompt
	 */
	public String getPrompt() {
		return this.prompt;
	}

	/**
	 * Set Negative Prompt
	 */
	public void setNegativePrompt(String nprom) {
		this.negativePrompt = nprom;
	}

	/**
	 * Get Negative Prompt
	 */
	public String getNegativePrompt() {
		return this.negativePrompt;
	}

	/**
	 * Set Seed
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	/**
	 * Get Seed
	 */
	public long getSeed() {
		return this.seed;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return this.width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return this.height;
	}

	public void setSampler(String sampler) {
		this.sampler = sampler;
	}

	public String getSampler() {
		return this.sampler;
	}

	public void setCfgs(int cfgs) {
		this.cfgs = cfgs;
	}

	public int getCfgs() {
		return cfgs;
	}

	/**
	 * @param steps steps
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	/**
	 * @return steps
	 */
	public int getSteps() {
		return this.steps;
	}
}

