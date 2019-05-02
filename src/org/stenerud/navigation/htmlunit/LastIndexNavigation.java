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

import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;

import org.stenerud.navigation.Navigation;

/**
 * Get the last entry of the context's node list
 * 
 * @author Karl Stenerud
 */
public class LastIndexNavigation extends HtmlNavigation
{
	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public LastIndexNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		List<DomNode> nodes = getNodeList();
		if ( nodes.size() == 0 )
			return false;
		DomNode node = nodes.get(nodes.size() - 1);
		nodes = new LinkedList<DomNode>();
		nodes.add(node);
		setNodeList(nodes);

		return true;
	}

	public String toString()
	{
		return "lastIndex";
	}
}
