package org.jfrog.hudson.generic;

import org.jfrog.build.api.dependency.DownloadableArtifact;

public class FilteredDownloadableArtifact extends DownloadableArtifact {

	String shortenedRelativeDirPath;
	
	public FilteredDownloadableArtifact(DownloadableArtifact da)
	{
		super(
				da.getRepoUrl(), 
				da.getTargetDirPath(), 
				da.getFilePath(), 
				da.getMatrixParameters(), 
				da.getSourcePattern(), 
				da.getPatternType());
		
		int firstSlash = super.getRelativeDirPath().indexOf('/');
		if(firstSlash >= 1)
		{
			shortenedRelativeDirPath = super.getRelativeDirPath().substring(firstSlash+1);
		}
		else
			shortenedRelativeDirPath = super.getRelativeDirPath();
		
		
	}
	
	@Override
	public String getRelativeDirPath()
	{
		return shortenedRelativeDirPath;
	}
	
	public String toString()
	{
		String retval = "***\r\nFilteredDownloadableArtifact:";
		retval += "\r\nFilePath:        " + getFilePath();
		retval += "\r\nSourcePattern:   " + getSourcePattern();
		retval += "\r\nTargetDirPath:   " + getTargetDirPath();
		retval += "\r\nRelativeDirPath: " + getRelativeDirPath();
		retval += "\r\n***";
		return retval;
	}
}
