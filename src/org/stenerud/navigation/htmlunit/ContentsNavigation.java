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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.BaseFrame;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Get the contents of a frame or iframe
 * 
 * @author Karl Stenerud
 */
public class ContentsNavigation extends HtmlNavigation
{
	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public ContentsNavigation(Navigation parent)
	{
		super(parent);
	}

	protected boolean navigateThisLevel()
	{
		List<DomNode> nodes = getNodeList();
		DomNode node = nodes.get(0);
		if ( !(node instanceof BaseFrame) )
			throw new RuntimeException("Element " + node.getClass().getName() + " is not a BaseFrame");

		Page page = ((BaseFrame)node).getEnclosedPage();
		if ( !(page instanceof HtmlPage) )
			throw new RuntimeException("Resulting page is of unsupported type " + page.getClass().getName());

		List<Page> results = new LinkedList<Page>();
		results.add(page);
		setNodeList(results);
		return results.size() != 0;
	}

	public String toString()
	{
		return "contents()";
	}
}
