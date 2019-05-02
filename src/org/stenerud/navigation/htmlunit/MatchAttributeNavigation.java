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
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

import org.stenerud.navigation.Navigation;

/**
 * Search for nodes matching an attribute and value. <br>
 * This navigator's behavior can be changed by placing descriptive navigations
 * before it in the navigation chain. <br>
 * <br>
 * Supported descriptive qualifiers are: <br>
 * DeepSearchNavigation: causes this navigation to search all nodes in the
 * context's list as well as all their descendants. <br>
 * PatternSearchNavigation: causes this navigation to treat the value as a
 * regular expression, interpreted using java.util.regex.Pattern. <br>
 * NegateNavigation: causes this navigation to negate its results, returning
 * nodes that do not match.
 * 
 * @see DeepSearchNavigation DeepSearchNavigation
 * @see PatternSearchNavigation PatternSearchNavigation
 * @see NegateNavigation NegateNavigation
 * @see java.util.regex.Pattern Pattern
 * @author Karl Stenerud
 */
public class MatchAttributeNavigation extends HtmlNavigation
{
	private String name;
	private String value;

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param name the name of the attribute to search for
	 * @param value the attribute value to search for
	 */
	public MatchAttributeNavigation(Navigation parent, String name, String value)
	{
		super(parent);
		this.name = name;
		this.value = value;
	}

	protected boolean navigateThisLevel()
	{
		NavigationContext ctx = getContext();
		List<DomNode> results = new LinkedList<DomNode>();
		boolean negate = ctx.hasTemporary(NegateNavigation.CONTEXTID_NEGATESEARCH);
		boolean deep = ctx.hasTemporary(DeepSearchNavigation.CONTEXTID_DEEPSEARCH);
		Pattern pattern = null;
		if ( ctx.hasTemporary(PatternSearchNavigation.CONTEXTID_PATTERNSEARCH) )
			pattern = Pattern.compile(value);

		List<DomNode> nodes = getNodeList();
		if ( deep )
		{
			// Deep search. Go through all nodes and their node trees
			for ( DomNode node : nodes )
			{
				if ( null != pattern )
					getElementsWithAttributePattern(node, name, pattern, results, negate);
				else
					getElementsWithAttributeValue(node, name, value, results, negate);
			}
		}
		else
		{
			// Normal search. Just search in the context's lists of nodes.
			if ( null != pattern )
				getElementsWithAttributePattern(nodes, name, pattern, results, negate);
			else
				getElementsWithAttributeValue(nodes, name, value, results, negate);
		}

		setNodeList(results);
		return results.size() != 0;
	}

	/**
	 * Search a list of nodes for elements with the specified attribute value
	 * 
	 * @param nodes the nodes to search
	 * @param nameIn the name of the attribute
	 * @param valueIn the value to search for
	 * @param results the list to place the results into
	 */
	private void getElementsWithAttributeValue(List<DomNode> nodes, String nameIn, String valueIn,
			List<DomNode> results, boolean negate)
	{
		for ( DomNode node : nodes )
		{
			if ( node instanceof HtmlElement )
				if ( negate ^ valueIn.equals((((HtmlElement)node)).getAttributeValue(nameIn)) )
					results.add(node);
		}
	}

	/**
	 * Search a list of nodes for elements whose attribute value matches a
	 * pattern
	 * 
	 * @param nodes the nodes to search
	 * @param nameIn the name of the attribute
	 * @param pattern the pattern to search against
	 * @param results the list to place the results into
	 */
	private void getElementsWithAttributePattern(List<DomNode> nodes, String nameIn, Pattern pattern,
			List<DomNode> results, boolean negate)
	{
		for ( DomNode node : nodes )
		{
			if ( node instanceof HtmlElement )
				if ( negate ^ pattern.matcher((((HtmlElement)node)).getAttributeValue(nameIn)).matches() )
					results.add(node);
		}
	}

	/**
	 * Search a node and its descendants for an attribute with the specified
	 * value
	 * 
	 * @param node the node to search
	 * @param nameIn the name of the attribute
	 * @param valueIn the value to search for
	 * @param results the list to place the results into
	 */
	private void getElementsWithAttributeValue(DomNode node, String nameIn, String valueIn, List<DomNode> results,
			boolean negate)
	{
		if ( node instanceof HtmlElement )
		{
			if ( negate ^ valueIn.equals((((HtmlElement)node)).getAttributeValue(nameIn)) )
				results.add(node);
		}

		for ( Iterator iter = node.getChildIterator(); iter.hasNext(); )
		{
			getElementsWithAttributeValue((DomNode)iter.next(), nameIn, valueIn, results, negate);
		}
	}

	/**
	 * Search a node and its descendants for elements whose attribute value
	 * matches a pattern
	 * 
	 * @param node the node to search
	 * @param nameIn the name of the attribute
	 * @param pattern the pattern to match against
	 * @param results the list to place the results into
	 */
	private void getElementsWithAttributePattern(DomNode node, String nameIn, Pattern pattern, List<DomNode> results,
			boolean negate)
	{
		if ( node instanceof HtmlElement )
		{
			if ( negate ^ pattern.matcher((((HtmlElement)node)).getAttributeValue(nameIn)).matches() )
				results.add(node);
		}

		for ( Iterator iter = node.getChildIterator(); iter.hasNext(); )
		{
			getElementsWithAttributePattern((DomNode)iter.next(), nameIn, pattern, results, negate);
		}
	}

	public String toString()
	{
		return "attribute(" + name + ", " + value + ")";
	}
}
