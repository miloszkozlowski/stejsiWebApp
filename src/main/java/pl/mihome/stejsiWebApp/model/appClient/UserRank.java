package pl.mihome.stejsiWebApp.model.appClient;

public enum UserRank {
	LESER("Leser", 1),
	BEGINNER("Żółtodziób", 2),
	FIT_MALINA("Fit Malina", 3),
	OSIŁEK("Osiłek", 4),
	KULTURYSTA("Kulturysta", 5);
	
	private final String description;
	private final int stars;
	
	private UserRank(String description, int stars) {
		this.description = description;
		this.stars = stars;
	}
	
	public String getDescription() {
		return description;
	}

	public int getStars() {
		return stars;
	}

}
