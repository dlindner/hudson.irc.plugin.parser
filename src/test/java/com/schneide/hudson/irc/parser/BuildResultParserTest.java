package com.schneide.hudson.irc.parser;

import java.text.ParseException;

import com.schneide.hudson.irc.parser.model.BuildResult;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class BuildResultParserTest extends TestCase {

	public BuildResultParserTest(String name) {
		super(name);
	}
	
	public void testDifferentBuildResults() throws ParseException {
		BuildResultParser parser = new BuildResultParser();
		assertSame(BuildResult.SUCCESS, parser.parse("SUCCESS"));
		assertSame(BuildResult.FAILURE, parser.parse("FAILURE"));
		assertSame(BuildResult.UNSTABLE, parser.parse("UNSTABLE"));
	}
	
	public void testWhitespaceRepresentation() throws ParseException {
		BuildResultParser parser = new BuildResultParser();
		assertSame(BuildResult.STILL_UNSTABLE, parser.parse("STILL UNSTABLE"));
	}
	
	public void testInvalidRepresentations() {
		try {
			new BuildResultParser().parse("not a valid representation!");
			fail("expected exception not thrown.");
		} catch (ParseException e) {
			assertEquals("Could not parse 'not a valid representation!' as a build result.", e.getMessage());
		}
	}
}
