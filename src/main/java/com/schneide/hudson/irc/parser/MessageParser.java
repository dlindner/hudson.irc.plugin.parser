package com.schneide.hudson.irc.parser;

import java.text.ParseException;

import com.schneide.hudson.irc.parser.internal.ParsedBuildResultMessage;
import com.schneide.hudson.irc.parser.model.BuildResult;
import com.schneide.hudson.irc.parser.model.BuildResultMessage;
import com.schneide.hudson.irc.parser.util.StringChunker;

public class MessageParser {

	private static final String MESSAGE_PREFIX = "Project "; //$NON-NLS-1$

	public MessageParser() {
		super();
	}

	public boolean mightBeAMessage(String message) {
		return (message.startsWith(MESSAGE_PREFIX));
	}

	public BuildResultMessage parse(String message) throws ParseException {
		if (!mightBeAMessage(message)) {
			throw new ParseException("This isn't a valid hudson ircbot message: " + message, 0); //$NON-NLS-1$
		}
		String truncatedMessage = message.substring(MESSAGE_PREFIX.length());
		StringChunker chunker = new StringChunker(truncatedMessage, ":"); //$NON-NLS-1$
		String jobName = extractJobName(chunker.getNextChunk());
		String buildResultText = extractBuildResultRepresentation(chunker.getNextChunk());
		BuildResultParser resultParser = new BuildResultParser();
		BuildResult buildResult = resultParser.parse(buildResultText);
		String buildURL = extractBuildURL(chunker.getRemainingString());
		return new ParsedBuildResultMessage(jobName, buildResult, buildURL);
	}

	protected String extractJobName(String truncatedMessage) throws ParseException {
		int endOfName = truncatedMessage.indexOf("build ("); //$NON-NLS-1$
		if (endOfName < 0) {
			throw new ParseException("Cannot find end of job name token.", 0); //$NON-NLS-1$
		}
		return truncatedMessage.substring(0, endOfName).trim();
	}

	protected String extractBuildResultRepresentation(final String truncatedMessage) throws ParseException {
		int endOfResult = truncatedMessage.indexOf(" in "); //$NON-NLS-1$
		if (endOfResult < 0) {
			throw new ParseException("Cannot find end of build result token.", 0); //$NON-NLS-1$
		}
		return truncatedMessage.substring(0, endOfResult).trim();
	}

	protected String extractBuildURL(final String truncatedMessage) {
		StringChunker chunker = new StringChunker(truncatedMessage.trim(), " "); //$NON-NLS-1$
		return chunker.getNextChunk();
	}
}
