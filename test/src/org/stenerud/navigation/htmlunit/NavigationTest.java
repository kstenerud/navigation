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
 * Test basic navigation
 * 
 * @author Karl Stenerud
 */
public class NavigationTest extends TestCase
{

	public NavigationTest(String name)
	{
		super(name);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite()
	{
		junit.framework.TestSuite suite = new junit.framework.TestSuite("Test Navigation");

		suite.addTest(new NavigationTest("testNavigation"));
		suite.addTest(new NavigationTest("testNavigation2"));
		suite.addTest(new NavigationTest("testUltimateParent"));
		suite.addTest(new NavigationTest("testUltimateParent2"));

		return suite;
	}

	// Helper Methods & Data
	// -------------------------------------------------------------------------
	private static final String BASE_URL = new File("html/test.html").toURI().toString();

	// Tests
	// -------------------------------------------------------------------------

	/**
	 * Test normal navigation and deep mavigation to the same node
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testNavigation() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		// Navigate: body -> first div -> div
		HtmlNavigation div1 = nav.bodyChildren().div().index(0).children().div();
		assertTrue(div1.exists());

		// Deep search for div, refined to the div with id "level2Div".
		// This could be optimized to nav.bodyChildren().deep().id("level2Div")
		HtmlNavigation div2 = nav.page().deep().div().id("level2Div");
		assertTrue(div2.exists());

		assertEquals(div1.getText(), div2.getText());

		// Make sure exists() returns false when it should
		assertFalse(div1.attribute("asdfasfvavrer", "drgasrgwer").exists());
	}

	/**
	 * Test normal navigation
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testNavigation2() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		// Deep search for id "level2Div".
		HtmlNavigation div = nav.page().deep().id("level2Div");
		assertTrue(div.exists());
		HtmlNavigation anchor = div.children().a();
		assertTrue(anchor.exists());
		HtmlNavigation form = div.children().form();
		assertTrue(form.exists());

		// assertEquals("Link to test 3", anchor.getText());
		assertTrue(form.exists());
		assertTrue(form.children().exists());
		assertTrue(form.children().input().exists());
		assertTrue(form.children().input().type("text").exists());
	}

	/**
	 * Test getting the ultimate parent
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testUltimateParent() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		// Navigate: body -> first div -> div -> form -> input with type text
		HtmlNavigation input = nav.bodyChildren().div().index(0).children().div().children().form().children().input()
				.type("text");
		assertTrue(input.exists());
		assertTrue(input.getUltimateParent() != null);
	}

	/**
	 * Test getting the ultimate parent
	 * 
	 * @throws Exception if an exception occurs
	 */
	public void testUltimateParent2() throws Exception
	{
		WebNavigator nav = new WebNavigator(BASE_URL);

		// Navigate: body -> first div -> div
		HtmlNavigation div = nav.bodyChildren().div().index(0).children().div();
		assertTrue(div.exists());
		// Navigate: div -> form -> input with type text
		HtmlNavigation input = div.children().form().children().input().type("text");
		assertTrue(input.exists());

		assertTrue(div.getUltimateParent() == input.getUltimateParent());
	}
}
