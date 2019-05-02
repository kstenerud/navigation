/*
 * Copyright 2005 Karl Stenerud Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.stenerud.navigation.htmlunit;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The WebNavigator wraps a WebClient and provides the head navigation nodes to
 * navigate through html documents. <br>
 * Generally, you create a WebNavigator, call gotoUrl(), then generate
 * navigation chains from the page() method. <br>
 * <br>
 * An example navigation given WebNavigator nav after calling gotoUrl():
 * nav.page().deep().id("someId").children().index(5).exists() <br>
 * <br>
 * This will look for id "someId" anywhere in the page, then get the 5th child
 * of that element. The call to exists() tests if the navigation is possible.
 * 
 * @see HtmlNavigation
 * @author Karl Stenerud
 */
public class WebNavigator
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WebNavigator.class.getName());

	/** Context identifier for WebNavigator */
	public static final String CONTEXTID_WEBNAVIGATOR = WebNavigator.class.getName();

	/**
	 * Navigation created by this WebNavigator when a client calls page(). <br>
	 * This serves as the top object on the chain, and places the WebNavigator
	 * on the context, as well as setting the top level node of the last http
	 * request.
	 */
	public static class WebNavigatorNavigation extends HtmlNavigation
	{
		private List<DomNode> initialNodes = new LinkedList<DomNode>();
		private WebNavigator WebNavigator;

		/**
		 * Constructor
		 * 
		 * @param WebNavigator the WebNavigator to put on the context
		 * @param initialFocus the node that will be considered "top level",
		 *           which will be placed on the initial context.
		 */
		public WebNavigatorNavigation(WebNavigator WebNavigator, DomNode initialFocus)
		{
			super(null);
			this.WebNavigator = WebNavigator;
			this.initialNodes.add(initialFocus);
		}

		protected NavigationContext createInitialContext()
		{
			NavigationContext context = super.createInitialContext();
			context.setPersistent(CONTEXTID_WEBNAVIGATOR, WebNavigator);
			setNodeList(initialNodes);
			return context;
		}

		protected boolean navigateThisLevel()
		{
			// This navigation is only used to create an initial context.
			return true;
		}

		public String toString()
		{
			return "WebNavigation";
		}
	}

	/** The page we are currently on. */
	private HtmlPage currentPage;

	/** The main workhorse for all web operations. */
	private WebClient webClient = new WebClient();

	/**
	 * Default Constructor. You still need to call gotoUrl or setPage before
	 * doing anything else.
	 */
	public WebNavigator()
	{
		webClient.setConfirmHandler(new ConfirmHandler()
		{
			public boolean handleConfirm(Page page, String message)
			{
				return true;
			}
		});
	}

	/**
	 * Constructor.
	 * 
	 * @param url url to start at.
	 * @throws IOException if an IO error occurs.
	 */
	public WebNavigator(String url) throws IOException
	{
		this();
		gotoUrl(url);
	}

	/**
	 * Set the user agent string.
	 * 
	 * @param userAgent the user agent.
	 */
	public void setUserAgent(String userAgent)
	{
		webClient.addRequestHeader("User-Agent", userAgent);
	}

	/**
	 * Add a custom HTTP request header.
	 * 
	 * @param header the header name
	 * @param value the header value
	 */
	public void addRequestHeader(String header, String value)
	{
		webClient.addRequestHeader(header, value);
	}

	/**
	 * Get a Navigation pointing to the current page.
	 * 
	 * @return a top level WebNavigatorNavigation pointing to the current page
	 */
	public WebNavigatorNavigation page()
	{
		return new WebNavigatorNavigation(this, currentPage);
	}

	/**
	 * Get a Navigation pointing to the current page's html tag.
	 * 
	 * @return an HtmlNavigation pointing to the current page's html tag
	 */
	public HtmlNavigation html()
	{
		return page().children().element("html");
	}

	/**
	 * Get a Navigation pointing to the children of the current page's html
	 * tag.
	 * 
	 * @return an HtmlNavigation pointing to the children current page's html
	 *         tag
	 */
	public HtmlNavigation htmlChildren()
	{
		return html().children();
	}

	/**
	 * Get a Navigation pointing to the current page's head tag.
	 * 
	 * @return an HtmlNavigation pointing to the current page's head tag
	 */
	public HtmlNavigation head()
	{
		return htmlChildren().element("head");
	}

	/**
	 * Get a Navigation pointing to the children of the current page's head
	 * tag.
	 * 
	 * @return an HtmlNavigation pointing to the children current page's head
	 *         tag
	 */
	public HtmlNavigation headChildren()
	{
		return head().children();
	}

	/**
	 * Get a Navigation pointing to the current page's title tag.
	 * 
	 * @return an HtmlNavigation pointing to the current page's title tag
	 */
	public HtmlNavigation title()
	{
		return headChildren().element("title");
	}

	/**
	 * Get a Navigation pointing to the current page's body tag.
	 * 
	 * @return an HtmlNavigation pointing to the current page's body tag
	 */
	public HtmlNavigation body()
	{
		return htmlChildren().element("body");
	}

	/**
	 * Get a Navigation pointing to the children of the current page's body
	 * tag.
	 * 
	 * @return an HtmlNavigation pointing to the children of the current page's
	 *         body tag
	 */
	public HtmlNavigation bodyChildren()
	{
		return body().children();
	}

	/**
	 * Get a Navigation pointing to the current page's frame tags
	 * 
	 * @return an HtmlNavigation pointing to the current page's frame tags
	 */
	public HtmlNavigation frames()
	{
		return htmlChildren().element("frameset").children().element("frame");
	}

	/**
	 * Go to a URL.
	 * 
	 * @param url the url to go to.
	 * @throws IOException if an IO exception occurs.
	 */
	public void gotoUrl(String url) throws IOException
	{
		log.debug("gotoUrl: " + url);
		doSetPage(webClient.getPage(new URL(url)));
	}

	/**
	 * Real page setter. This does some sanity checks on the page.
	 * 
	 * @param page the page to set
	 */
	private void doSetPage(Page page) throws IOException
	{
		if ( page instanceof HtmlPage )
		{
			currentPage = (HtmlPage)page;
		}
		else if ( page instanceof UnexpectedPage )
		{
			UnexpectedPage response = (UnexpectedPage)page;
			throw new IOException("Unexpected page.  Code=" + response.getWebResponse().getStatusCode() + ", message="
					+ response.getWebResponse().getStatusMessage());
		}
	}

	/**
	 * Set the current page. <br>
	 * NOTE: this only supports HtmlPage at this time!
	 * 
	 * @param page the page to set
	 * @throws IOException if an IO exception occurs.
	 */
	public void setPage(Page page) throws IOException
	{
		doSetPage(page);
	}

	/**
	 * Get the underlying web client.
	 * 
	 * @return the web client.
	 */
	public WebClient getWebClient()
	{
		return webClient;
	}
}
