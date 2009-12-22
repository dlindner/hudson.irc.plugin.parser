/**
 * (C) Copyright Softwareschneiderei GmbH, Karlsruhe, Germany
 * @author User
 * @since 13.06.2009
 */
package com.schneide.hudson.irc.parser.util;

import junit.framework.Assert;
import junit.framework.TestCase;

@SuppressWarnings("nls")
public class DelimiterSetTest extends TestCase {

    public DelimiterSetTest(String name) {
        super(name);
    }

    public void testCreationByChars() {
        DelimiterSet set = new DelimiterSet('a', 'b', 'c');
        Assert.assertEquals(true, set.isDelimiter('a'));
        Assert.assertEquals(true, set.isDelimiter('b'));
        Assert.assertEquals(true, set.isDelimiter('c'));
        Assert.assertEquals(false, set.isDelimiter('d'));
    }

    public void testCreationByString() {
        DelimiterSet set = new DelimiterSet("abc");
        Assert.assertEquals(true, set.isDelimiter('a'));
        Assert.assertEquals(true, set.isDelimiter('b'));
        Assert.assertEquals(true, set.isDelimiter('c'));
        Assert.assertEquals(false, set.isDelimiter('d'));
    }

    public void testHiddenState() {
        DelimiterSet set = new DelimiterSet();
        Assert.assertEquals(true, set.isHidden());
        set.setHidden(false);
        Assert.assertEquals(false, set.isHidden());
        set.setHidden(true);
        Assert.assertEquals(true, set.isHidden());
    }

    public void testEnabledState() {
        DelimiterSet set = new DelimiterSet();
        Assert.assertEquals(true, set.isEnabled());
        set.setEnabled(false);
        Assert.assertEquals(false, set.isEnabled());
        set.setEnabled(true);
        Assert.assertEquals(true, set.isEnabled());
    }
}
