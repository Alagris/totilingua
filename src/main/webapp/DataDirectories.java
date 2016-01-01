package main.webapp;

public enum DataDirectories
{

	IMAGES("images"),MAPS("maps");
	final String path;
	private DataDirectories(String path)
	{
		this.path=path;
	}
}
