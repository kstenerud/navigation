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
 * Descriptive navigation that adds a "pattern search" qualifier to the
 * temporary context. <br>
 * Search navigations such as MatchAttributeNavigation will alter their
 * behavior to search through subnodes if this qualifier is present.
 * 
 * @author Karl Stenerud
 */
public class PatternSearchNavigation extends DescriptiveNavigation
{
	public static final String CONTEXTID_PATTERNSEARCH = PatternSearchNavigation.class.getName();

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public PatternSearchNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		// Set the "pattern search" qualifier in the temporary context
		getContext().setTemporaryMarker(CONTEXTID_PATTERNSEARCH);

		// No actual navigation takes place here.
		return true;
	}

	public String toString()
	{
		return "pattern";
	}
}
