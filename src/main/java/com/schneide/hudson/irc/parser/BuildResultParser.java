package com.schneide.hudson.irc.parser;

import java.text.ParseException;

import com.schneide.hudson.irc.parser.model.BuildResult;

public class BuildResultParser {
	
	public BuildResultParser() {
		super();
	}

	public BuildResult parse(String representation) throws ParseException {
		try {
			return BuildResult.valueOf(
					replaceBlanksWithUnderscores(representation));
		} catch (IllegalArgumentException e) {
			throw new ParseException("Could not parse '" + representation + "' as a build result.", 0); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	protected String replaceBlanksWithUnderscores(String text) {
		return text.replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
