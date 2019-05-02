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
 * Make sure the context node list contains exactly a specified number of
 * entries. <br>
 * Supported descriptive qualifiers are: <br>
 * NegateNavigation: causes this navigation to negate its results, returning
 * nodes that do not match.
 * 
 * @see NegateNavigation NegateNavigation
 * @author Karl Stenerud
 */
public class ContainsExactlyNavigation extends HtmlNavigation
{
	private int numEntries;

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param numEntries the number of entries to check against
	 */
	public ContainsExactlyNavigation(Navigation parent, int numEntries)
	{
		super(parent);
		this.numEntries = numEntries;
	}

	protected boolean navigateThisLevel()
	{
		NavigationContext ctx = getContext();
		boolean negate = ctx.hasTemporary(NegateNavigation.CONTEXTID_NEGATESEARCH);
		return negate ^ getNodeList().size() == numEntries;
	}

	public String toString()
	{
		return "exactly(" + numEntries + ")";
	}
}
