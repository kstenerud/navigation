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

import org.stenerud.navigation.Navigation;

/**
 * Superclass for descriptive navigations. <br>
 * A descriptive navigation will not clear the temporary context after
 * navigation, whereas a non-descriptive one will. <br>
 * This allows chaining of multiple "descriptive" HttpNavigations to
 * collectively dictate behavior on the next "real" navigation encountered in
 * the chain. <br>
 * Descriptive navigations should not modify the permanent context. <br>
 * <br>
 * This would feel a lot nicer subclassed off Navigator directly, but doing so
 * would lose the ability to use HtmlNavigation's factory methods, and Java
 * doesn't support multiple inheritance.
 * 
 * @author Karl Stenerud
 */
public abstract class DescriptiveNavigation extends HtmlNavigation
{
	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public DescriptiveNavigation(Navigation parent)
	{
		super(parent);
	}

	protected void handleContext()
	{
		// A descriptive navigation doesn't clear the temporary context
	}
}
