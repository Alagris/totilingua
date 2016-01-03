package main.webapp;

import java.io.File;

public enum DataDirectories
{
	/** starts and ends without file separator */
	IMAGES("images"), MAPS("maps");
	/** starts and ends without file separator */
	final String path;

	private DataDirectories(String path)
	{
		this.path = path;
	}

	/** starts without file separator */
	public static String getPathTo(DataDirectories category, String fileName)
	{
		return category.path + File.separatorChar + fileName;
	}
}
