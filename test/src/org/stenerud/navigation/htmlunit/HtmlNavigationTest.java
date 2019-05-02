/*
 * Copyright 2005 Karl Stenerud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.stenerud.navigation.htmlunit;

import java.io.File;

import junit.framework.TestCase;

/**
 * Test various HtmlNavigation functionalities
 * 
 * @author Karl Stenerud
 */
public class HtmlNavigationTest extends TestCase
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HtmlNavigationTest.class.getName());

	public HtmlNavigationTest(String name)
	{
		super(name);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite()
	{
		junit.framework.TestSuite suite = new junit.framework.TestSuite("Test HtmlNavigation");

		suite.addTest(new HtmlNavigationTest("testBadNavigation"));
		suite.addTest(new HtmlNavigationTest("testDeepSearch"));
		suite.addTest(new HtmlNavigationTest("testPatternSearch"));
		suite.addTest(new HtmlNavigationTest("testSetValue"));
		suite.addTest(new HtmlNavigationTest("testActivate"));
		suite.addTest(new HtmlNavigationTest("testFrames"));
		suite.addTest(new HtmlNavigationTest("testCount"));
		suite.addTest(new HtmlNavigationTest("testParent"));
		suite.addTest(new HtmlNavigationTest("testNegate"));
		suite.addTest(new HtmlNavigationTest("testAfter"));
		suite.addTest(new HtmlNavigationTest("testBefore"));

		return suite;
	}

	// Helper Methods & Data
	// -------------------------------------------------------------------------
	private static final String BASE_URL = new File("html/test.html").toURI().toString();
	private static final String FRAMES_URL = new File("html/frames.html").toURI().toString();

	// Tests
	// -------------------------------------------------------------------------

	/**
	 * Test deep searches
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testDeepSearch() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		assertTrue(nav.page().deep().div().id("level1DivA").exists());

		assertTrue(nav.page().deep().div().id("level2Div").exists());

		assertTrue(nav.page().deep().div().id("level1DivB").exists());

		assertFalse(nav.page().deep().div().id("sadfasdfasd").exists());

		assertTrue(nav.page().deep().a().href("test3.html").exists());

		assertFalse(nav.page().deep().a().href("asdfasdf.html").exists());
	}

	/**
	 * Test pattern based searches
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testPatternSearch() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		assertTrue(nav.page().deep().div().pattern().id("level1DivA").exists());

		assertFalse(nav.page().deep().pattern().id("aqwergaehbae.*aasdd?..+").exists());

		assertTrue(nav.page().deep().div().pattern().id("level.Div.*").exactly(3).exists());
	}

	/**
	 * Test setting a value
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testSetValue() throws Exception
	{
		String testValue = "newvalue";
		String trueValue = "true";
		String falseValue = "false";
		String checkedValue = "checked";
		String uncheckedValue = "";

		WebNavigator nav = new WebNavigator(BASE_URL);

		assertTrue(nav.page().deep().input().type("text").setValue(testValue).exists());
		assertEquals(testValue, nav.page().deep().input().type("text").getValue());

		assertTrue(nav.page().deep().input().type("checkbox").setValue(trueValue).exists());
		assertEquals(checkedValue, nav.page().deep().input().type("checkbox").getValue());

		assertTrue(nav.page().deep().input().type("checkbox").setValue(falseValue).exists());
		assertEquals(uncheckedValue, nav.page().deep().input().type("checkbox").getValue());

		assertTrue(nav.page().deep().input().type("radio").setValue(trueValue).exists());
		assertEquals(checkedValue, nav.page().deep().input().type("radio").getValue());

		assertTrue(nav.page().deep().input().type("radio").setValue(falseValue).exists());
		assertEquals(uncheckedValue, nav.page().deep().input().type("radio").getValue());

		assertTrue(nav.page().deep().textarea().setValue(testValue).exists());
		assertEquals(testValue, nav.page().deep().textarea().getValue());
	}

	/**
	 * Test activating a control. This test contacts monash university, and so
	 * it's REALLY slow compared to the other tests (10-20 times slower than the
	 * rest combined) since it requires http connections rather than filesystem
	 * lookups.
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testActivate() throws Exception
	{
		log.warn("testActivate is currently disabled.");
		/*
		 * // We can't test form activation using only the filesystem with
		 * htmlunit, // so instead go to a well-known site. WebNavigator nav =
		 * new
		 * WebNavigator("http://www.csse.monash.edu.au/cgi-bin/cgiwrap/jwb/wwwjdic?1C"); //
		 * Activate a form HtmlNavigation form = nav.page().deep().form();
		 * assertTrue(form.deep().input().name("dsrchkey").setValue("asdf").exists());
		 * form.activate(); assertTrue(nav.page().pattern().text(".*longest match
		 * found.*").exists()); // Activate an anchor
		 * nav.page().deep().a().text("Documentation").activate();
		 * assertTrue(nav.page().pattern().text(".*INTRODUCTION.*").exists());
		 */
	}

	/**
	 * Test navigating through frames.
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testFrames() throws Exception
	{
		WebNavigator nav = new WebNavigator(FRAMES_URL);

		HtmlNavigation frames = nav.frames();
		assertTrue(frames.exists());

		assertTrue(frames.id("leftFrame").contents().deep().element("h1").text("Welcome to test 2").exists());
		assertTrue(frames.id("middleFrame").contents().deep().element("h1").text("Welcome to test 3").exists());

		// Try navigating through two frames (a frameset frame, then an inline
		// frame)
		assertTrue(frames.id("rightFrame").contents().deep().iframe().contents().deep().element("h1").text(
				"Welcome to test 2").exists());
	}

	/**
	 * Test counting elements
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testCount() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		assertEquals(4, nav.page().deep().action("test2.html").children().input().nodeCount());
		assertEquals(1, nav.page().deep().id("level1DivA").children().nodeCount());
	}

	/**
	 * Test getting a parent element
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testParent() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		assertEquals("test3.html", nav.page().deep().action("test2.html").parent().children().a().getAttribute("href"));
	}

	/**
	 * Test using negative matches
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testNegate() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		HtmlNavigation formChildren = nav.page().deep().action("test2.html").children();

		assertEquals(4, formChildren.not().textarea().nodeCount());
		assertEquals(3, formChildren.not().textarea().not().type("radio").nodeCount());
	}

	/**
	 * Test getting nodes after the current one
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testAfter() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);
		HtmlNavigation after = nav.page().deep().type("checkbox").after();

		assertTrue(after.id("level1DivB").children().id("span1").exists());
		assertFalse(after.name("textparam").exists());
		assertFalse(after.type("checkbox").exists());
		assertFalse(after.action("test2.html").exists());
		assertFalse(after.id("level1DivA").exists());
	}

	/**
	 * Test getting nodes before the current one
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testBefore() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);
		HtmlNavigation before = nav.page().deep().type("checkbox").before();

		assertFalse(before.id("level1DivB").children().id("span1").exists());
		assertTrue(before.name("textparam").exists());
		assertFalse(before.type("checkbox").exists());
		assertTrue(before.element("h1").exists());
		assertTrue(before.element("head").exists());
	}

	public void testBadNavigation() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);
		assertEquals(0, nav.page().deep().pattern().href("blahblahblahblah.*").after().table().children().children()
				.nodeCount());
		assertFalse(nav.page().deep().pattern().href("blahblahblahblah.*").after().table().children().children().exists());
	}
}
