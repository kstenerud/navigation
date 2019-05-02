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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

import org.stenerud.navigation.Navigation;

/**
 * Generate a list of all children of the first node in the context. <br>
 * Only the first node of the list in in the context will be searched. All
 * others will be ignored.
 * 
 * @author Karl Stenerud
 */
public class ChildrenNavigation extends HtmlNavigation
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ChildrenNavigation.class.getName());

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public ChildrenNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		// TODO: handle more than the first entry in the node list

		DomNode parentNode = getNodeList().get(0);
		List<DomNode> nodes = new LinkedList<DomNode>();
		for ( Iterator iter = parentNode.getChildIterator(); iter.hasNext(); )
		{
			DomNode current = (DomNode)iter.next();
			if ( current instanceof HtmlElement )
			{
				log.debug("Adding " + current.getNodeName());
				nodes.add(current);
			}
		}
		setNodeList(nodes);
		return nodes.size() != 0;
	}

	public String toString()
	{
		return "children";
	}
}
