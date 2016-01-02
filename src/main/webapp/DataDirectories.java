package main.webapp;

public enum DataDirectories
{
	/** start and end without file separator */
	IMAGES("images"), MAPS("maps");
	/** start and end without file separator */
	final String path;

	private DataDirectories(String path)
	{
		this.path = path;
	}
}
