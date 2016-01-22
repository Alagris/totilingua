package main.webapp;

public enum LanguageTags {
	POLISH("pl"),ENGLISH("en"),GERMAN("de"),RUSSSIAN("ru"),SPANISH("es");
	
	public final String tag;
	private LanguageTags(String tag) {
		this.tag=tag;
	}
	public static String[] getTags()
	{
		LanguageTags[] lt = values();
		String[] tags = new String[lt.length];
		for(int i =0;i<lt.length;i++)
			tags[i]=lt[i].tag;
		return tags;
	}
}
