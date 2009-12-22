package com.schneide.hudson.irc.parser.internal;

import com.schneide.hudson.irc.parser.model.BuildResult;
import com.schneide.hudson.irc.parser.model.BuildResultMessage;

public class ParsedBuildResultMessage implements BuildResultMessage {
	
	private final String jobName;
	private final BuildResult buildResult;
	private final String buildURL;
	
	public ParsedBuildResultMessage(String jobName, BuildResult buildResult,
			String buildURL) {
		super();
		this.jobName = jobName;
		this.buildResult = buildResult;
		this.buildURL = buildURL;
	}

	@Override
	public BuildResult getBuildResult() {
		return this.buildResult;
	}

	@Override
	public String getBuildURL() {
		return this.buildURL;
	}

	@Override
	public String getJobName() {
		return this.jobName;
	}
}
