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

import org.stenerud.navigation.Navigation;

import com.gargoylesoftware.htmlunit.html.DomNode;

/**
 * Generate a list of all children of the first node in the context. <br>
 * Only the first node of the list in in the context will be searched. All
 * others will be ignored.
 * 
 * @author Karl Stenerud
 */
public class ParentNavigation extends HtmlNavigation
{
	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public ParentNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		// TODO: handle more than the first entry in the node list

		DomNode node = getNodeList().get(0);
		List<DomNode> nodes = new LinkedList<DomNode>();
		DomNode parentNode = node.getParentNode();
		if ( null != parentNode )
			nodes.add(parentNode);
		setNodeList(nodes);
		return nodes.size() != 0;
	}

	public String toString()
	{
		return "parent";
	}
}
