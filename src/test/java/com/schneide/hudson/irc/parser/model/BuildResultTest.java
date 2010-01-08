package com.schneide.hudson.irc.parser.model;

import junit.framework.TestCase;

public class BuildResultTest extends TestCase {

	public BuildResultTest(String name) {
		super(name);
	}

	public void testEnsureSuccessfulFlag() {
		assertEquals(true, BuildResult.FIXED.isSuccessful());
		assertEquals(true, BuildResult.SUCCESS.isSuccessful());

		assertEquals(false, BuildResult.STILL_FAILING.isSuccessful());
		assertEquals(false, BuildResult.FAILURE.isSuccessful());
		assertEquals(false, BuildResult.STILL_UNSTABLE.isSuccessful());
		assertEquals(false, BuildResult.UNSTABLE.isSuccessful());
		assertEquals(false, BuildResult.ABORTED.isSuccessful());
		assertEquals(false, BuildResult.NOT_BUILT.isSuccessful());
	}
}
