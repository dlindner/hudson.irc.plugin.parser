package com.schneide.hudson.irc.parser.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dali
 * @since 21.06.2004
 */
public class StringChunker implements Iterator<String> {

    private static final DelimiterSet DEFAULT_DELIMITERS = new DelimiterSet(" \t\n\r\f"); //$NON-NLS-1$

    private final String text;
    private final DelimiterSet delimiters;

    private boolean ignoreMultipleDelimiters;
    private boolean canReturnNull;

    private boolean ignoreLeadingDelimiters;
    private boolean ignoreTrailingDelimiters;

    private int position;
    private String nextChunk;
    private boolean lastPositionChunkProvided;

    public StringChunker(String text) {
        this(text, StringChunker.DEFAULT_DELIMITERS);
    }

    public StringChunker(String text, boolean ignoreMultipleDelimiters) {
        this(text, StringChunker.DEFAULT_DELIMITERS, ignoreMultipleDelimiters);
    }

    public StringChunker(String text, String delimiters) {
        this(text, new DelimiterSet(delimiters));
    }

    public StringChunker(String text, String delimiters, boolean ignoreMultipleDelimiters) throws IllegalArgumentException {
        this(text, new DelimiterSet(delimiters), ignoreMultipleDelimiters);
    }

    public StringChunker(String text, DelimiterSet delimiters) {
        this(text, delimiters, false);
    }

    public StringChunker(String text, DelimiterSet delimiters, boolean ignoreMultipleDelimiters) {
        super();
        this.text = text;
        this.delimiters = delimiters;
        this.ignoreMultipleDelimiters = ignoreMultipleDelimiters;
        this.canReturnNull = true;
        this.position = 0;
        this.nextChunk = null;
        this.lastPositionChunkProvided = false;
        this.ignoreLeadingDelimiters = false;
        this.ignoreTrailingDelimiters = false;
    }

    public boolean ignoresMultipleDelimiters() {
        return this.ignoreMultipleDelimiters;
    }

    public void setIgnoreMultipleDelimiters(boolean ignoreMultipleDelimiters) {
        this.ignoreMultipleDelimiters = ignoreMultipleDelimiters;
    }

    private int getDelimiterCount() {
        int result = 0;
        while (hasCharactersLeft() && isCurrentCharacterDelimiter()) {
            this.position++;
            result++;
        }
        return result;
    }

    public void setIgnoreLeadingDelimiters(boolean ignoreLeadingDelimiters) {
        this.ignoreLeadingDelimiters = ignoreLeadingDelimiters;
    }

    public void setIgnoreTrailingDelimiters(boolean ignoreTrailingDelimiters) {
        this.ignoreTrailingDelimiters = ignoreTrailingDelimiters;
    }

    public void setIgnoreLeadingAndTrailingDelimiters(boolean ignoranceMode) {
        setIgnoreLeadingDelimiters(ignoranceMode);
        setIgnoreTrailingDelimiters(ignoranceMode);
    }

    public boolean ignoresLeadingDelimiters() {
        return this.ignoreLeadingDelimiters;
    }

    public boolean ignoresTrailingDelimiters() {
        return this.ignoreTrailingDelimiters;
    }

    private boolean hasCharactersLeft() {
        return (this.position < this.text.length());
    }

    public boolean hasMoreChunks() {
        if (null == this.text) {
            return false;
        }
        if (null != this.nextChunk) {
            return true;
        }
        if ((0 == this.position) && (ignoresLeadingDelimiters())) {
            this.position = getDelimiterCount();
        }
        if (hasCharactersLeft()) {
            return parseNextChunk();
        } else if (wasLastCharacterDelimiter()
                && (!this.lastPositionChunkProvided)
                && (!this.ignoreTrailingDelimiters)) {
            this.lastPositionChunkProvided = true;
            this.nextChunk = ""; //$NON-NLS-1$
            return true;
        }
        this.nextChunk = null;
        return false;
    }

    private boolean parseNextChunk() {
        int startPosition = this.position;
        int delimiterCount = 0;
        while (hasCharactersLeft()) {
            if (isCurrentCharacterDelimiter()) {
                this.position++;
                delimiterCount++;
                if (ignoresMultipleDelimiters()) {
                    delimiterCount += getDelimiterCount();
                }
                break;
            }
            this.position++;
        }
        if (startPosition < this.position) {
            int offset = getOffset(delimiterCount);
            this.nextChunk = this.text.substring(startPosition, this.position + offset);
            if (isIgnoredTrailingDelimiterChunk()) {
                this.nextChunk = null;
                return false;
            }
            return true;
        }
        this.nextChunk = null;
        return false;
    }

    private boolean isIgnoredTrailingDelimiterChunk() {
        return (ignoresTrailingDelimiters())
        && ((this.position > this.text.length()))
        && (0 == this.nextChunk.length());
    }

    private boolean wasLastCharacterDelimiter() {
        if (this.position > 0) {
            return isDelimiter(this.text.charAt(this.position - 1));
        }
        return false;
    }

    private boolean isDelimiter(char character) {
        return this.delimiters.isDelimiter(character);
    }

    private boolean isCurrentCharacterDelimiter() {
        char currentCharacter = this.text.charAt(this.position);
        return isDelimiter(currentCharacter);
    }

    private int getOffset(int delimiterCount) {
        if (this.delimiters.isHidden()) {
            return -delimiterCount;
        }
        return 0;
    }

    public String getNextChunk() {
        if (hasMoreChunks()) {
            String result = this.nextChunk;
            this.nextChunk = null;
            return result;
        }
        return getNullChunk();
    }

    private String getNullChunk() {
        if (canReturnNull()) {
            return null;
        }
        return ""; //$NON-NLS-1$
    }

    public void setCanReturnNull(boolean canReturnNull) {
        this.canReturnNull = canReturnNull;
    }

    public boolean canReturnNull() {
        return this.canReturnNull;
    }

    public int getPosition() {
        return this.position;
    }

    public String getRemainingString() {
        return this.text.substring(getPosition());
    }

    public String getLastChunk() {
        String result = null;
        while (hasMoreChunks()) {
            result = getNextChunk();
        }
        return result;
    }

    public boolean hasNext() {
        return hasMoreChunks();
    }

    public String next() {
        return getNextChunk();
    }

    public void remove() {
        throw new UnsupportedOperationException("remove is not supported"); //$NON-NLS-1$
    }

    public String[] getAllChunks() {
        List<String> result = new ArrayList<String>();
        while (hasMoreChunks()) {
            result.add(getNextChunk());
        }
        return result.toArray(new String[result.size()]);
    }
}
