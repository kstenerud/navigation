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

import org.stenerud.navigation.Navigation;

/**
 * Search for nodes whose text representation matches a value. <br>
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
public class MatchTextNavigation extends HtmlNavigation
{
	private String value;

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the text to search for
	 */
	public MatchTextNavigation(Navigation parent, String value)
	{
		super(parent);
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
					getElementsWithTextPattern(node, pattern, results, negate);
				else
					getElementsWithTextValue(node, value, results, negate);
			}
		}
		else
		{
			// Normal search. Just search in the context's lists of nodes.
			if ( null != pattern )
				getElementsWithTextPattern(nodes, pattern, results, negate);
			else
				getElementsWithTextValue(nodes, value, results, negate);
		}

		setNodeList(results);
		return results.size() != 0;
	}

	/**
	 * Search a list of nodes for elements whose text representation matches a
	 * value
	 * 
	 * @param nodes the nodes to search
	 * @param valueIn the value to search for
	 * @param results the list to place the results into
	 * @param negate if true, negate the test.
	 */
	public void getElementsWithTextValue(List<DomNode> nodes, String valueIn, List<DomNode> results, boolean negate)
	{
		for ( DomNode node : nodes )
		{
			if ( negate ^ valueIn.equals(node.asText()) )
				results.add(node);
		}
	}

	/**
	 * Search a list of nodes for elements whose text representation matches a
	 * pattern
	 * 
	 * @param nodes the nodes to search
	 * @param pattern the pattern to search against
	 * @param results the list to place the results into
	 * @param negate if true, negate the test.
	 */
	public void getElementsWithTextPattern(List<DomNode> nodes, Pattern pattern, List<DomNode> results, boolean negate)
	{
		for ( DomNode node : nodes )
		{
			if ( negate ^ pattern.matcher(node.asText()).matches() )
				results.add(node);
		}
	}

	/**
	 * Search a node and its descendants for text representation matching the
	 * specified value
	 * 
	 * @param node the node to search
	 * @param valueIn the value to search for
	 * @param results the list to place the results into
	 * @param negate if true, negate the test.
	 */
	public void getElementsWithTextValue(DomNode node, String valueIn, List<DomNode> results, boolean negate)
	{
		if ( negate ^ valueIn.equals(node.asText()) )
			results.add(node);

		for ( Iterator iter = node.getChildIterator(); iter.hasNext(); )
		{
			getElementsWithTextValue((DomNode)iter.next(), valueIn, results, negate);
		}
	}

	/**
	 * Search a node and its descendants for elements whose text representation
	 * matches a pattern
	 * 
	 * @param node the node to search
	 * @param pattern the pattern to match against
	 * @param results the list to place the results into
	 * @param negate if true, negate the test.
	 */
	public void getElementsWithTextPattern(DomNode node, Pattern pattern, List<DomNode> results, boolean negate)
	{
		if ( negate ^ pattern.matcher(node.asText()).matches() )
			results.add(node);

		for ( Iterator iter = node.getChildIterator(); iter.hasNext(); )
		{
			getElementsWithTextPattern((DomNode)iter.next(), pattern, results, negate);
		}
	}

	public String toString()
	{
		return "attribute(" + value + ")";
	}
}
