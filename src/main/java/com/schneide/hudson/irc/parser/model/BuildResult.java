package com.schneide.hudson.irc.parser.model;

/**
 * All the build results that can be reported by the hudson IRC bot.
 * 
 * @author dsl
 */
public enum BuildResult {
	
	FIXED(true),
	SUCCESS(true),
	STILL_FAILING(false),
	FAILURE(false),
	STILL_UNSTABLE(false),
	UNSTABLE(false),
	ABORTED(false),
	NOT_BUILT(false);

	private final boolean isSuccessful;
		
	private BuildResult(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	
	/**
	 * Indicates if the build result denotes a successful build.
	 * Successful builds have completed without a complaint.
	 * Unstable builds are not considered successful.
	 */
	public boolean isSuccessful() {
		return this.isSuccessful;
	}
}
