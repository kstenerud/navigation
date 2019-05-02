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
 * Generate a list of all nodes after the current node in the document
 * 
 * @author Karl Stenerud
 */
public class BeforeNavigation extends HtmlNavigation
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BeforeNavigation.class.getName());

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public BeforeNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		DomNode currentNode = getNodeList().get(0);
		List<DomNode> nodes = new LinkedList<DomNode>();

		while ( null != currentNode )
		{
			DomNode parentNode = currentNode.getParentNode();
			if ( null == parentNode )
				break;

			Iterator iter = parentNode.getChildIterator();

			// Add all nodes until the current node
			while ( iter.hasNext() )
			{
				DomNode node = (DomNode)iter.next();
				if ( node == currentNode )
					break;
				if ( node instanceof HtmlElement )
				{
					log.debug("Adding " + node.getNodeName());
					nodes.add(node);
				}
			}

			// Prepare to rerun on this node's parent
			currentNode = parentNode;
		}

		setNodeList(nodes);
		return nodes.size() != 0;
	}

	public String toString()
	{
		return "after";
	}
}
