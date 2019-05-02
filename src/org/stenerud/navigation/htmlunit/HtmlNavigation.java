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

import java.util.List;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import org.stenerud.navigation.Navigation;

/**
 * Implements methods to access DomNode and HtmlElement. <br>
 * Also contains factory methods to generate all HtmlElement subclasses
 * supported by HtmlHnit. <br>
 * <br>
 * I've implemented the factories at this level to allow chaining of
 * navigations together in a strongly typed language. <br>
 * <br>
 * See WebNavigator for an example of how navigations are chained together.
 * 
 * @see WebNavigator WebNavigator
 * @author Karl Stenerud
 */
public abstract class HtmlNavigation extends Navigation
{
	public static final String CONTEXTID_NODES = HtmlNavigation.class.getName() + ".nodelist";

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 */
	public HtmlNavigation(Navigation parent)
	{
		super(parent);
	}

	/**
	 * Set the context's node list
	 * 
	 * @param nodes the list of nodes to set
	 */
	protected void setNodeList(List nodes)
	{
		getContext().setPersistent(CONTEXTID_NODES, nodes);
	}

	/**
	 * Get the context's node list without calling get. This is used internally
	 * 
	 * @return the node list
	 */
	@SuppressWarnings("unchecked")
	protected List<DomNode> getNodeList()
	{
		if ( null == getContext() )
			throw new RuntimeException("BUG: No context!");
		return (List<DomNode>)getContext().getPersistent(CONTEXTID_NODES);
	}

	/**
	 * Get the WebNavigator that generated the dom tree being navigated
	 * 
	 * @return the WebNavigator
	 */
	protected WebNavigator getWebNavigator()
	{
		return (WebNavigator)getContext().getPersistent(WebNavigator.CONTEXTID_WEBNAVIGATOR);
	}

	/**
	 * Count the number of nodes in the current context
	 * 
	 * @return the number of nodes in the current context
	 */
	public int nodeCount()
	{
		return getNodes().size();
	}

	/**
	 * Get the nodes at this level of navigation, performing the navigation if
	 * necessary.
	 * 
	 * @return the list of nodes
	 */
	public List getNodes()
	{
		get();
		List nodes = getNodeList();
		if ( null == nodes )
			throw new RuntimeException("BUG: No nodelist on the context!");

		return nodes;
	}

	/**
	 * Get the first node resulting from navigation to this level. This will run
	 * the navigation if necessary.
	 * 
	 * @return the first node in the result
	 */
	public DomNode getNode()
	{
		return (DomNode)getNodes().get(0);
	}

	/**
	 * Get the text representation of the first node resulting from navigation
	 * to this level. <br>
	 * This will run the navigation if necessary.
	 * 
	 * @return the text representation
	 */
	public String getText()
	{
		return getNode().asText();
	}

	/**
	 * Get the value of an attribute. This will get the first node on the
	 * context and search for the attribute of the specified name
	 * 
	 * @param attributeName the attribute to search for
	 * @return the attribute value or null if the attribute doesn't exist
	 */
	public String getAttribute(String attributeName)
	{
		DomNode node = getNode();
		if ( !(node instanceof HtmlElement) )
			return null;
		HtmlElement element = (HtmlElement)node;
		String result = element.getAttributeValue(attributeName);
		if ( result.equals(HtmlElement.ATTRIBUTE_NOT_DEFINED) )
			return null;
		if ( result.equals(HtmlElement.ATTRIBUTE_VALUE_EMPTY) )
			return "";
		return result;
	}

	/**
	 * Get the name of this node. This will get the first node on the context
	 * and return its name
	 * 
	 * @return the node name
	 */
	public String getName()
	{
		return getNode().getNodeName();
	}

	/**
	 * Get the value of an input element.
	 * 
	 * @return the value of the input or text area element, or null if the
	 *         element is of the wrong type
	 */
	public String getValue()
	{
		DomNode node = getNode();

		if ( node instanceof HtmlCheckBoxInput )
		{
			return ((HtmlInput)node).getCheckedAttribute();
		}
		else if ( node instanceof HtmlFileInput )
		{
			return ((HtmlInput)node).getValueAttribute();
		}
		else if ( node instanceof HtmlPasswordInput )
		{
			return ((HtmlInput)node).getValueAttribute();
		}
		else if ( node instanceof HtmlRadioButtonInput )
		{
			return ((HtmlInput)node).getCheckedAttribute();
		}
		else if ( node instanceof HtmlTextInput )
		{
			return ((HtmlInput)node).getValueAttribute();
		}
		else if ( node instanceof HtmlTextArea )
		{
			return ((HtmlTextArea)node).getText();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Activate the first node of the result at this level. The node must be of
	 * type ClickableElement. <br>
	 * The WebNavigator that generated this page will have its current page set
	 * to the result of the click. <br>
	 * The resulting page can sometimes be the same page as before.
	 */
	public void activate()
	{
		try
		{
			DomNode node = getNode();
			WebNavigator nav = getWebNavigator();

			if ( !(node instanceof ClickableElement) )
				throw new RuntimeException("Element " + node.getClass().getName() + " is not a ClickableElement");

			Page page = null;
			if ( node instanceof HtmlForm )
				page = ((HtmlForm)node).submit();
			else
				page = ((ClickableElement)node).click();
			if ( !(page instanceof HtmlPage) )
				throw new RuntimeException("Resulting page is of unsupported type " + page.getClass().getName());

			nav.setPage(page);
		}
		catch ( java.io.IOException e )
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(boolean value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(int value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(long value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(float value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(double value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Set the value of the first node resulting from navigation to this level.
	 * <br>
	 * The node must be a type that accepts input such as checkboxes, input
	 * fields. and text areas.
	 * 
	 * @param value the value to set the node to
	 * @return the resultant navigation
	 */
	public SetValueNavigation setValue(String value)
	{
		return new SetValueNavigation(this, value);
	}

	/**
	 * Get all direct children of the last navigation's first entry.
	 * 
	 * @return the resultant navigation
	 */
	public ChildrenNavigation children()
	{
		return new ChildrenNavigation(this);
	}

	/**
	 * Get the parent of the last navigation's first entry.
	 * 
	 * @return the resultant navigation
	 */
	public ParentNavigation parent()
	{
		return new ParentNavigation(this);
	}

	/**
	 * Get the specified index into the previous navigation's results
	 * 
	 * @param idx the index of the node to get
	 * @return the resultant navigation
	 */
	public IndexNavigation index(int idx)
	{
		return new IndexNavigation(this, idx);
	}

	/**
	 * Get the first index into the previous navigation's results
	 * 
	 * @return the resultant navigation
	 */
	public IndexNavigation first()
	{
		return new IndexNavigation(this, 0);
	}

	/**
	 * Get the last index into the previous navigation's results
	 * 
	 * @return the resultant navigation
	 */
	public LastIndexNavigation last()
	{
		return new LastIndexNavigation(this);
	}

	/**
	 * Get all top level nodes that appear before this point in the document.
	 * 
	 * @return the resultant navigation
	 */
	public BeforeNavigation before()
	{
		return new BeforeNavigation(this);
	}

	/**
	 * Get all top level nodes that appear after this point in the document.
	 * 
	 * @return the resultant navigation
	 */
	public AfterNavigation after()
	{
		return new AfterNavigation(this);
	}

	/**
	 * Ensure that the previous navigation had the specified number of results
	 * 
	 * @param numEntries the number of entries to check against
	 * @return the resultant navigation
	 */
	public ContainsExactlyNavigation exactly(int numEntries)
	{
		return new ContainsExactlyNavigation(this, numEntries);
	}

	/**
	 * Ensure that the previous navigation had at least the specified number of
	 * results
	 * 
	 * @param numEntries the number of entries to check against
	 * @return the resultant navigation
	 */
	public ContainsAtLeastNavigation atLeast(int numEntries)
	{
		return new ContainsAtLeastNavigation(this, numEntries);
	}

	/**
	 * Ensure that the previous navigation had at most the specified number of
	 * results
	 * 
	 * @param numEntries the number of entries to check against
	 * @return the resultant navigation
	 */
	public ContainsAtMostNavigation atMost(int numEntries)
	{
		return new ContainsAtMostNavigation(this, numEntries);
	}

	/**
	 * Marker to make the next search navigation a negated search. The search
	 * navigation will change behavior, returning all nodes that do not match
	 * the search criteria.
	 * 
	 * @return the resultant navigation
	 */
	public NegateNavigation not()
	{
		return new NegateNavigation(this);
	}

	/**
	 * Marker to make the next search navigation a deep search. The search
	 * navigation will change behavior, recursing through the dom rather than
	 * searching only direct children..
	 * 
	 * @return the resultant navigation
	 */
	public DeepSearchNavigation deep()
	{
		return new DeepSearchNavigation(this);
	}

	/**
	 * Marker to make the next search navigation a regex pattern search.. <br>
	 * The search navigation will change behavior, interpreting the search
	 * argument as a pattern rather than a string literal.
	 * 
	 * @return the resultant navigation
	 */
	public PatternSearchNavigation pattern()
	{
		return new PatternSearchNavigation(this);
	}

	/**
	 * Search for an element by name. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param name the name of the element to search for
	 * @return the resultant navigation
	 */
	public MatchElementNavigation element(String name)
	{
		return new MatchElementNavigation(this, name);
	}

	/**
	 * Search for attributes by name and value. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param attribute the name of the attribute to search for
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation attribute(String attribute, String value)
	{
		return new MatchAttributeNavigation(this, attribute, value);
	}

	/**
	 * Get the contents of a frame or iframe
	 * 
	 * @return the resultant navigation
	 */
	public ContentsNavigation contents()
	{
		return new ContentsNavigation(this);
	}

	/**
	 * Search for text inside nodes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchTextNavigation text(String value)
	{
		return new MatchTextNavigation(this, value);
	}

	/**
	 * Search for action attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation action(String value)
	{
		return new MatchAttributeNavigation(this, "action", value);
	}

	/**
	 * Search for class attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation styleClass(String value)
	{
		return new MatchAttributeNavigation(this, "class", value);
	}

	/**
	 * Search for href attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation href(String value)
	{
		return new MatchAttributeNavigation(this, "href", value);
	}

	/**
	 * Search for id attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation id(String value)
	{
		return new MatchAttributeNavigation(this, "id", value);
	}

	/**
	 * Search for name attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation name(String value)
	{
		return new MatchAttributeNavigation(this, "name", value);
	}

	/**
	 * Search for src attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation src(String value)
	{
		return new MatchAttributeNavigation(this, "src", value);
	}

	/**
	 * Search for type attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation type(String value)
	{
		return new MatchAttributeNavigation(this, "type", value);
	}

	/**
	 * Search for value attributes. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @param value the value to match
	 * @return the resultant navigation
	 */
	public MatchAttributeNavigation value(String value)
	{
		return new MatchAttributeNavigation(this, "value", value);
	}

	/**
	 * Search for an a element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation a()
	{
		return new MatchElementNavigation(this, "a");
	}

	/**
	 * Search for a br element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation br()
	{
		return new MatchElementNavigation(this, "br");
	}

	/**
	 * Search for a div element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation div()
	{
		return new MatchElementNavigation(this, "div");
	}

	/**
	 * Search for a form element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation form()
	{
		return new MatchElementNavigation(this, "form");
	}

	/**
	 * Search for an hr element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation hr()
	{
		return new MatchElementNavigation(this, "hr");
	}

	/**
	 * Search for an iframe element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation iframe()
	{
		return new MatchElementNavigation(this, "iframe");
	}

	/**
	 * Search for an img element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation img()
	{
		return new MatchElementNavigation(this, "img");
	}

	/**
	 * Search for an input element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation input()
	{
		return new MatchElementNavigation(this, "input");
	}

	/**
	 * Search for a li element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation li()
	{
		return new MatchElementNavigation(this, "li");
	}

	/**
	 * Search for an object element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation object()
	{
		return new MatchElementNavigation(this, "object");
	}

	/**
	 * Search for an option element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation option()
	{
		return new MatchElementNavigation(this, "option");
	}

	/**
	 * Search for a p element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation p()
	{
		return new MatchElementNavigation(this, "p");
	}

	/**
	 * Search for a select element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation select()
	{
		return new MatchElementNavigation(this, "select");
	}

	/**
	 * Search for a span element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation span()
	{
		return new MatchElementNavigation(this, "span");
	}

	/**
	 * Search for a table element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation table()
	{
		return new MatchElementNavigation(this, "table");
	}

	/**
	 * Search for a tbody element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation tbody()
	{
		return new MatchElementNavigation(this, "tbody");
	}

	/**
	 * Search for a td element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation td()
	{
		return new MatchElementNavigation(this, "td");
	}

	/**
	 * Search for a textarea element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation textarea()
	{
		return new MatchElementNavigation(this, "textarea");
	}

	/**
	 * Search for a th element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation th()
	{
		return new MatchElementNavigation(this, "th");
	}

	/**
	 * Search for a thead element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation thead()
	{
		return new MatchElementNavigation(this, "thead");
	}

	/**
	 * Search for a tr element. <br>
	 * Supports not, deep, and pattern searches.
	 * 
	 * @return the resultant navigation
	 */
	public MatchElementNavigation tr()
	{
		return new MatchElementNavigation(this, "tr");
	}
}
