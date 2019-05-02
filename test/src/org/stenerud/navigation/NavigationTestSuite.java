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

package org.stenerud.navigation;

import org.stenerud.navigation.htmlunit.ExampleTest;
import org.stenerud.navigation.htmlunit.HtmlNavigationTest;
import org.stenerud.navigation.htmlunit.NavigationTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test suite
 * 
 * @author Karl Stenerud
 */
public class NavigationTestSuite extends TestCase
{
	/**
	 * Constructor.
	 * 
	 * @param name the name of this test suite.
	 */
	public NavigationTestSuite(String name)
	{
		super(name);
	}

	/**
	 * Command-line interface.
	 * 
	 * @param args the arguments to this program.
	 */
	public static void main(String[] args)
	{
		TestRunner.run(suite());
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite();

		suite.setName("Test Unit Tests");
		suite.addTest(NavigationTest.suite());
		suite.addTest(HtmlNavigationTest.suite());
		suite.addTest(ExampleTest.suite());
		return suite;
	}
}
