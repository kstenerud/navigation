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
 * Test that the example code on the readme page actually works! Note that the
 * readme page does not include the extra gotoUrl() calls to avoid cluttering
 * it up. They're meant to show various different ways to reach the same
 * destination from the same source anyway.
 * 
 * @author Karl Stenerud
 */
public class ExampleTest extends TestCase
{
	public ExampleTest(String name)
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

		suite.addTest(new ExampleTest("testExamplePage"));

		return suite;
	}

	// Helper Methods & Data
	// -------------------------------------------------------------------------
	private static final String BASE_URL = new File("html/example.html").toURI().toString();

	// Tests
	// -------------------------------------------------------------------------

	/**
	 * Test deep searches
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testExamplePage() throws Exception
	{
		WebNavigator nav = new WebNavigator();

		nav.gotoUrl(BASE_URL);
		nav.bodyChildren().deep().id("randomLink").activate();

		// Same as above, but the search for id "randomLink" starts at the page
		// level
		nav.gotoUrl(BASE_URL);
		nav.page().deep().id("randomLink").activate();

		// Gets the first element whose id matches starts with "random" and
		// attempts to activate it.
		// Note that if we had, say, a BR tag earlier in the page that had an id
		// of "randomBreak",
		// the navigation would attempt to activate it and throw an exception.
		nav.gotoUrl(BASE_URL);
		nav.page().deep().pattern().id("random.*").activate();

		// Search for the anchor by href. Here you assume that the href will
		// always be "goRandom.html".
		nav.gotoUrl(BASE_URL);
		nav.page().deep().href("goRandom.html").activate();

		// Do a step-by-step navigation down the dom. This assumes a lot about
		// the page layout,
		// and makes for a very brittle navigation.
		nav.gotoUrl(BASE_URL);
		nav.bodyChildren().div().id("section1").children().div().id("subsectionA").children().a().activate();

		// Do a deep search for an anchor that contains the exact text "Click
		// here to go a random location"
		nav.gotoUrl(BASE_URL);
		nav.page().deep().a().text("Click here to go a random location").activate();

		// Do a deep search for an anchor whose text contains "random" at any
		// point.
		nav.gotoUrl(BASE_URL);
		nav.page().deep().a().pattern().text(".*random.*").activate();
	}
}
