/**
 * Created: 21.06.2004
 */
package com.schneide.hudson.irc.parser.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author dali
 * @since 21.06.2004
 */
@SuppressWarnings("nls")
public class StringChunkerTest extends TestCase {

    private static final String THREE = "3";
    private static final String TWO = "2";
    private static final String ONE = "1";
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private static final String CHUNK2 = "chunk2";
    private static final String CHUNK1_CHUNK2 = "chunk1||chunk2||";
    private static final String CHUNK1 = "chunk1";
    private static final String PIPE = "|";

    public StringChunkerTest() {
        super();
    }

    public void testNullObjectChunking() throws Exception {
        StringChunker chunker = new StringChunker(null, StringChunkerTest.PIPE);
        compareResults(chunker);
    }

    public void testEmptyStringChunking() throws Exception {
        StringChunker chunker = new StringChunker("", StringChunkerTest.PIPE);
        compareResults(chunker);
    }

    public void testOnlyDelimiterChunking() throws Exception {
        StringChunker singleDelimiterChunker = new StringChunker(StringChunkerTest.PIPE, StringChunkerTest.PIPE);
        compareResults(singleDelimiterChunker, "", "");
        StringChunker multiDelimiterChunker = new StringChunker(StringChunkerTest.PIPE, StringChunkerTest.PIPE, true);
        Assert.assertTrue(multiDelimiterChunker.ignoresMultipleDelimiters());
        compareResults(multiDelimiterChunker, "", "");
    }

    public void testSimplestChunking() throws Exception {
        StringChunker chunker = new StringChunker("h|a|l|l|o", StringChunkerTest.PIPE);
        compareResults(chunker, "h", "a", "l", "l", "o");
    }

    private void compareResults(StringChunker chunker, String... expectedChunks) {
        List<String> surplusChunks = new ArrayList<String>();
        int chunkNumber = 0;
        while (chunker.hasMoreChunks()) {
            String currentChunk = chunker.getNextChunk();
            if (chunkNumber < expectedChunks.length) {
                Assert.assertEquals("Falscher Chunk", expectedChunks[chunkNumber],
                        currentChunk);
            } else {
                surplusChunks.add(currentChunk);
            }
            chunkNumber++;
        }
        String message = getComparisonMessage(chunkNumber, surplusChunks, expectedChunks);
        Assert.assertEquals(message, expectedChunks.length, chunkNumber);
    }

    private String getComparisonMessage(int chunkNumber, List<String> surplusChunks, String[] expectedChunks) {
        if (chunkNumber > expectedChunks.length) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Mehr Chunks gefunden als erwartet:\n");
            for (String string : surplusChunks) {
                messageBuffer.append(string);
                messageBuffer.append("\n");
            }
            return messageBuffer.toString();
        } else if (chunkNumber < expectedChunks.length) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Es sind noch erwartete Chunks übrig:\n");
            for (int i = chunkNumber; i < expectedChunks.length; i++) {
                messageBuffer.append(expectedChunks[i]);
                messageBuffer.append("\n");
            }
            return messageBuffer.toString();
        }
        return "";
    }

    public void testSeveralDelimiters() throws Exception {
        compareResults(new StringChunker("chunk1#chunk2|chunk3%chunk4", "#|%"),
                StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2, "chunk3", "chunk4");
    }

    public void testMultipleDelimiters() throws Exception {
        testMultipleDelimitersWith(true, StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2);
    }

    private void testMultipleDelimitersWith(boolean ignoreMultipleDelimiters, String... expectedChunks) {
        StringChunker chunker = new StringChunker("chunk1||chunk2", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(ignoreMultipleDelimiters);
        compareResults(chunker, expectedChunks);
    }

    public void testMultipleDelimitersIgnored() throws Exception {
        testMultipleDelimitersWith(false,
                new String[] {StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2});
    }

    public void testMultipleDelimitersAtEnd() throws Exception {
        StringChunker chunker = new StringChunker("chunk1|||chunk2||", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2, "");
    }

    public void testIgnoredDelimitersAtEnd() throws Exception {
        StringChunker chunker = new StringChunker(StringChunkerTest.CHUNK1_CHUNK2, StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(false);
        chunker.setIgnoreTrailingDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2, "");
    }

    public void testIgnoredMultipleDelimitersAtEnd() throws Exception {
        StringChunker chunker = new StringChunker(StringChunkerTest.CHUNK1_CHUNK2, StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(true);
        chunker.setIgnoreTrailingDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2);
    }

    public void testDelimitersAtEnd() throws Exception {
        StringChunker chunker = new StringChunker(StringChunkerTest.CHUNK1_CHUNK2, StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(false);
        compareResults(chunker, StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2, "", "");
    }

    public void testDelimitersAtStart() throws Exception {
        StringChunker chunker = new StringChunker("|chunk1||chunk2|", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(false);
        compareResults(chunker, "", StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2, "");
    }

    public void testIgnoredDelimitersAtStart() throws Exception {
        StringChunker chunker = new StringChunker("|chunk1||chunk2|", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(false);
        chunker.setIgnoreLeadingDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2, "");
    }

    public void testIgnoredMultipleDelimitersAtStart() throws Exception {
        StringChunker chunker = new StringChunker("|||chunk1||chunk2|", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(false);
        chunker.setIgnoreLeadingDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, "", StringChunkerTest.CHUNK2, "");
    }

    public void testMultipleDelimitersAtStart() throws Exception {
        StringChunker chunker = new StringChunker("||chunk1|chunk2|||chunk3|", StringChunkerTest.PIPE);
        chunker.setIgnoreMultipleDelimiters(true);
        compareResults(chunker, "", StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2, "chunk3", "");
    }

    public void testIgnoreBothState() throws Exception {
        StringChunker chunker = new StringChunker("|chunk1|chunk2|", StringChunkerTest.PIPE);
        chunker.setIgnoreLeadingAndTrailingDelimiters(true);
        compareResults(chunker, StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2);
        StringChunker chunker2 = new StringChunker("|chunk1|chunk2|", StringChunkerTest.PIPE);
        chunker2.setIgnoreLeadingAndTrailingDelimiters(false);
        compareResults(chunker2, "", StringChunkerTest.CHUNK1, StringChunkerTest.CHUNK2, "");
    }

    public void testGetRestText() throws Exception {
        StringChunker chunker = new StringChunker("Test|Rest|des|Tests", StringChunkerTest.PIPE);
        Assert.assertEquals("Falscher Chunk", "Test", chunker.getNextChunk());
        Assert.assertEquals("Falscher Resttext", "Rest|des|Tests", chunker.getRemainingString());
    }

    public void testForIssueELE27() throws Exception {
        StringChunker chunker = new StringChunker("modul.product.fehle==", "=");
        Assert.assertEquals("Key falsch eingelesen", "modul.product.fehle", chunker.getNextChunk());
        Assert.assertEquals("Wert falsch ausgelesen", "=", chunker.getRemainingString());
    }

    public void testRemainingStringBehaviourWithDelimiter() throws Exception {
        StringChunker chunker = new StringChunker("?   +0.00 m");
        chunker.setIgnoreMultipleDelimiters(true);
        Assert.assertEquals("?", chunker.getNextChunk());
        Assert.assertEquals("+0.00", chunker.getNextChunk());
        Assert.assertEquals("m", chunker.getRemainingString());
    }

    public void testRemoveIsUnsupported() {
        StringChunker chunker = new StringChunker("not important");
        try {
            chunker.remove();
            Assert.fail("Exception expected");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("remove is not supported", e.getMessage());
        }
    }

    /**
     * See if hasNext() and next() are delegated properly.
     */
    public void testIteratorMethods() {
        StringChunker chunker = new StringChunker("1;2;3", StringChunkerTest.SEMICOLON);
        Assert.assertEquals(true, chunker.hasNext());
        Assert.assertEquals(true, chunker.hasMoreChunks());
        String chunk1 = chunker.next();
        Assert.assertEquals(StringChunkerTest.ONE, chunk1);
        Assert.assertEquals(true, chunker.hasNext());
        String chunk2 = chunker.next();
        Assert.assertEquals(StringChunkerTest.TWO, chunk2);
        Assert.assertEquals(true, chunker.hasNext());
        String chunk3 = chunker.next();
        Assert.assertEquals(StringChunkerTest.THREE, chunk3);
        Assert.assertEquals(false, chunker.hasNext());
        Assert.assertEquals(false, chunker.hasMoreChunks());
    }

    public void testGetLastChunk() {
        StringChunker chunker = new StringChunker("1;2;3;4", StringChunkerTest.SEMICOLON);
        Assert.assertEquals("4", chunker.getLastChunk());
        Assert.assertEquals(false, chunker.hasMoreChunks());
    }

    public void testGetAllChunks() {
        StringChunker chunker = new StringChunker("1;2;3;4", StringChunkerTest.SEMICOLON);
        String[] chunks = chunker.getAllChunks();
        Assert.assertEquals(false, chunker.hasMoreChunks());
        Assert.assertEquals(4, chunks.length);
        Assert.assertEquals(StringChunkerTest.ONE, chunks[0]);
        Assert.assertEquals(StringChunkerTest.TWO, chunks[1]);
        Assert.assertEquals(StringChunkerTest.THREE, chunks[2]);
        Assert.assertEquals("4", chunks[3]);
    }

    public void testReturningOfNullChunks() {
        StringChunker chunker = new StringChunker("1,2", StringChunkerTest.COMMA);
        Assert.assertEquals(true, chunker.canReturnNull());
        String chunk1 = chunker.getNextChunk();
        Assert.assertEquals(StringChunkerTest.ONE, chunk1);
        String chunk2 = chunker.getNextChunk();
        Assert.assertEquals(StringChunkerTest.TWO, chunk2);
        String chunkNull = chunker.getNextChunk();
        Assert.assertEquals(null, chunkNull);
    }

    public void testReturningOfChunksWithoutNull() {
        StringChunker chunker = new StringChunker("1,2", StringChunkerTest.COMMA);
        chunker.setCanReturnNull(false);
        Assert.assertEquals(false, chunker.canReturnNull());
        String chunk1 = chunker.getNextChunk();
        Assert.assertEquals(StringChunkerTest.ONE, chunk1);
        String chunk2 = chunker.getNextChunk();
        Assert.assertEquals(StringChunkerTest.TWO, chunk2);
        Assert.assertEquals("", chunker.getNextChunk());
        Assert.assertEquals("", chunker.getNextChunk());
        Assert.assertEquals("", chunker.getNextChunk());
    }

    public void testCreationWithOnlyDelimiterSwitch() {
        StringChunker chunkerTrue = new StringChunker("irrelevant", true);
        Assert.assertEquals(true, chunkerTrue.ignoresMultipleDelimiters());
        StringChunker chunkerFalse = new StringChunker("irrelevant", false);
        Assert.assertEquals(false, chunkerFalse.ignoresMultipleDelimiters());
    }

    public void testChunkingWithNonHiddenDelimiters() {
        DelimiterSet delimiters = new DelimiterSet(StringChunkerTest.COMMA);
        delimiters.setHidden(false);
        StringChunker chunker = new StringChunker("1,2,3", delimiters);
        Assert.assertEquals("1,", chunker.getNextChunk());
        Assert.assertEquals("2,", chunker.getNextChunk());
        Assert.assertEquals(StringChunkerTest.THREE, chunker.getNextChunk());
    }

    public void testIgnoredTrailingDelimiter() {
        StringChunker chunker = new StringChunker("1,,", StringChunkerTest.COMMA);
        Assert.assertEquals(false, chunker.ignoresTrailingDelimiters());
        chunker.setIgnoreTrailingDelimiters(true);
        Assert.assertEquals(false, chunker.ignoresMultipleDelimiters());
        Assert.assertEquals(StringChunkerTest.ONE, chunker.getNextChunk());
        Assert.assertEquals("", chunker.getNextChunk());
        Assert.assertEquals(null, chunker.getNextChunk());
    }

    public void testNotIgnoredTrailingDelimiter() {
        StringChunker chunker = new StringChunker("1,,", StringChunkerTest.COMMA);
        Assert.assertEquals(false, chunker.ignoresTrailingDelimiters());
        Assert.assertEquals(false, chunker.ignoresMultipleDelimiters());
        Assert.assertEquals(StringChunkerTest.ONE, chunker.getNextChunk());
        Assert.assertEquals("", chunker.getNextChunk());
        Assert.assertEquals("", chunker.getNextChunk());
        Assert.assertEquals(null, chunker.getNextChunk());
    }

    public void testIgnoredMultipleTrailingDelimiters() {
        StringChunker chunker = new StringChunker("A,,", StringChunkerTest.COMMA);
        Assert.assertEquals(false, chunker.ignoresTrailingDelimiters());
        chunker.setIgnoreTrailingDelimiters(true);
        chunker.setIgnoreMultipleDelimiters(true);
        Assert.assertEquals("A", chunker.getNextChunk());
        Assert.assertEquals(null, chunker.getNextChunk());
    }

    public void testDelimitersAtEndWithGetAllChunks() throws Exception {
        StringChunker chunker = new StringChunker("chunk1|chunk2|", "|");
        chunker.setIgnoreMultipleDelimiters(false);
        Assert.assertEquals(3, chunker.getAllChunks().length);
    }
}
