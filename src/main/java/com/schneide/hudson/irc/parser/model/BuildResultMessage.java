package com.schneide.hudson.irc.parser.model;

/**
 * The information parts contained in an hudson IRC message
 * 
 * @author dsl
 */
public interface BuildResultMessage {
	
	/**
	 * Returns the job name this message was for
	 */
	public String getJobName();

	/**
	 * Returns the {@link BuildResult} that describes the build outcome
	 */
	public BuildResult getBuildResult();
	
	/**
	 * Returns a {@link String} that contains the URL to the build main page
	 */
	public String getBuildURL();
}
