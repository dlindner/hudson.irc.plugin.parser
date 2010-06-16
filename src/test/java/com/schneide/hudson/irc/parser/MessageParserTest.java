package com.schneide.hudson.irc.parser;

import java.text.ParseException;

import junit.framework.TestCase;

import com.schneide.hudson.irc.parser.model.BuildResult;
import com.schneide.hudson.irc.parser.model.BuildResultMessage;

@SuppressWarnings("nls")
public class MessageParserTest extends TestCase {

	public MessageParserTest(String name) {
		super(name);
	}

	public void testIsMessage() {
		MessageParser parser = new MessageParser();
		assertEquals(false, parser.mightBeAMessage(""));
		assertEquals(false, parser.mightBeAMessage("some weird text"));
		assertEquals(false, parser.mightBeAMessage("Project"));
		assertEquals(true, parser.mightBeAMessage("Project "));
		assertEquals(true, parser.mightBeAMessage("Project jobname build (1):"));
	}

	public void testExtractJobName() throws ParseException {
		MessageParser parser = new MessageParser();
		assertEquals("somejob",
				parser.extractJobName("somejob build (1): SUCCESS in 0,15 Sekunden: http://localhost:8080/job/somejob/1/"));
		assertEquals("somejob",
				parser.extractJobName("somejob build ("));
		assertEquals("whitespace in name",
				parser.extractJobName("whitespace in name build ("));
	}

	public void testExtractJobNameFailure() {
		MessageParser parser = new MessageParser();
		try {
			assertEquals("somejob",
					parser.extractJobName("somejob build not a valid message"));
			fail("Exception expected, but now missing.");
		} catch (ParseException e) {
			assertEquals("Cannot find end of job name token.", e.getMessage());
		}
	}

	public void testExtractBuildResult() throws ParseException {
		MessageParser parser = new MessageParser();
		assertEquals("SUCCESS",
				parser.extractBuildResultRepresentation(" SUCCESS in 0,15 Sekunden: http://localhost:8080/job/somejob/1/"));
		assertEquals("STILL UNSTABLE",
				parser.extractBuildResultRepresentation(" STILL UNSTABLE in 0,15 Sekunden: http://localhost:8080/job/somejob/1/"));
		assertEquals("STILL FAILING",
				parser.extractBuildResultRepresentation(" STILL FAILING in "));
	}

	public void testExtractBuildResultFailure() {
		MessageParser parser = new MessageParser();
		try {
			parser.extractBuildResultRepresentation(" FAILURE because this message isn't valid");
			fail("Exception expected here");
		} catch (ParseException e) {
			assertEquals("Cannot find end of build result token.", e.getMessage());
		}
	}

	public void testExtractBuildURL() {
		MessageParser parser = new MessageParser();
		assertEquals("http://localhost:8080/job/somejob/1/",
				parser.extractBuildURL(" http://localhost:8080/job/somejob/1/"));
	}

	public void testExtractBuildURLWithoutPort() {
		MessageParser parser = new MessageParser();
		assertEquals("http://reportbox.intranet/job/ramses-visualizer-reports/1943/",
				parser.extractBuildURL(" http://reportbox.intranet/job/ramses-visualizer-reports/1943/"));
	}

	public void testSuccessMessage() throws ParseException {
		MessageParser parser = new MessageParser();
		BuildResultMessage message = parser.parse("Project somejob build (1): SUCCESS in 0,15 Sekunden: http://localhost:8080/job/somejob/1/");
		assertEquals("somejob", message.getJobName());
		assertSame(BuildResult.SUCCESS, message.getBuildResult());
	}

	public void testRealMessage1() throws ParseException {
		MessageParser parser = new MessageParser();
		BuildResultMessage message = parser.parse("Project bergen-client-reports build (67): STILL UNSTABLE in 3 min 26 sec: http://obrien:8080/job/bergen-client-reports/67/");
		assertEquals("bergen-client-reports", message.getJobName());
		assertSame(BuildResult.STILL_UNSTABLE, message.getBuildResult());
	}

	public void testRealMessage2() throws ParseException {
		MessageParser parser = new MessageParser();
		BuildResultMessage message = parser.parse("Project ramses-visualizer-reports build (1943): SUCCESS in 10 min: http://reportbox.intranet/job/ramses-visualizer-reports/1943/");
		assertEquals("ramses-visualizer-reports", message.getJobName());
		assertEquals(BuildResult.SUCCESS, message.getBuildResult());
		assertEquals("http://reportbox.intranet/job/ramses-visualizer-reports/1943/", message.getBuildURL());
	}
}
