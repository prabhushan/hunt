package com.fun.hunt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	private static final String prefix = "http://prod-intranet/portal/";
	private static final List<String> filterPrefix = new ArrayList<String>();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	loadFilterUrls();
    	assertTrue(simpleTest("http://prod-intranet/portal/issdsdsd"));
    	assertTrue(simpleTest("http://prod-intranet/portal/sysops/??"));
    	assertTrue(simpleTest("http://prod-intranet/portal/profile/?profile=gpsingh1"));
    	assertFalse(simpleTest("http://prod-intranet/portal/self-help-faqs/"));
    }
    
    private boolean simpleTest(String strLink){
    	if(StringUtils.equalsIgnoreCase(strLink, prefix)){
    		return true;
    	}
		for (Iterator iterator = filterPrefix.iterator(); iterator.hasNext();) {
			String strFilter = (String) iterator.next();
			if (StringUtils.startsWithIgnoreCase(strLink, strFilter)) {
				return true;
			}
		}

		return false;
    }
    
    private static void loadFilterUrls() {
		filterPrefix.add("http://prod-intranet/portal/is");
		filterPrefix.add("http://prod-intranet/portal/sysops");
		//filterPrefix.add(prefix);
		filterPrefix.add("http://prod-intranet/portal/profile/");
	}
}
