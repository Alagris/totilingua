package main.webapp;

public enum LanguageTags {
	POLISH("pl"),ENGLISH("en"),GERMAN("de"),RUSSSIAN("ru"),SPANISH("es");
	
	public final String tag;
	private LanguageTags(String tag) {
		this.tag=tag;
	}
}
