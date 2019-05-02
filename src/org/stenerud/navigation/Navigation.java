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

package org.stenerud.navigation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Superclass for all navigations. <br>
 * This class provides a mechanism for chaining navigations together, and then
 * invoking the navigation to achieve a result.
 * 
 * @author Karl Stenerud
 */
public abstract class Navigation
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Navigation.class.getName());

	/**
	 * Basic navigation context. <br>
	 * This context provides two separate maps, one for temporary objects, and
	 * one for pesistent objects. <br>
	 * <br>
	 * I'm still not sure I want to do it this way, but this is how it works for
	 * now: <br>
	 * The temporary map can be cleared at any time by a navigation, whereas the
	 * persistent map cannot. <br>
	 * I added this to support the deep and pattern "descriptive" navigator
	 * idiom. <br>
	 * <br>
	 * WARNING: This context gets cloned as the navigation progresses. The map
	 * cloning is shallow, and so only references to the objects you add will be
	 * copied! If you have to change the value of something on a context, please
	 * replace the object rather than modify it directly!
	 */
	protected static class NavigationContext implements Cloneable
	{
		private Map<String, Object> temporary = new HashMap<String, Object>();
		private Map<String, Object> persistent = new HashMap<String, Object>();

		public void setTemporary(String key, Object value)
		{
			temporary.put(key, value);
		}

		public void setTemporaryMarker(String key)
		{
			temporary.put(key, new Object());
		}

		public Object getTemporary(String key)
		{
			return temporary.get(key);
		}

		public boolean hasTemporary(String key)
		{
			return temporary.get(key) != null;
		}

		public void clearTemporary()
		{
			temporary.clear();
		}

		public void setPersistent(String key, Object value)
		{
			persistent.put(key, value);
		}

		public Object getPersistent(String key)
		{
			return persistent.get(key);
		}

		public Object clone()
		{
			try
			{
				NavigationContext clone = (NavigationContext)super.clone();

				// We're only cloning the references to the map contents.
				clone.temporary = new HashMap<String, Object>(temporary);
				clone.persistent = new HashMap<String, Object>(persistent);
				return clone;
			}
			catch ( CloneNotSupportedException e )
			{
				// Should never happen
				throw new RuntimeException(e);
			}
		}
	}

	private Navigation parent;
	private NavigationContext context;
	private boolean hasNavigated = false;
	private boolean navigationResult = false;

	/**
	 * Constructor
	 * 
	 * @param parent the parent to attach to
	 */
	protected Navigation(Navigation parent)
	{
		this.parent = parent;
	}

	/**
	 * Execute the navigation to this point and get the result.
	 * 
	 * @return this navigation, or null if the navigation could not reach this
	 *         point.
	 */
	public Navigation get()
	{
		// Just return cached value if we've already run the navigation to this
		// level.
		if ( hasNavigated )
			return navigationResult ? this : null;

		// Build an inverse list so we can run from the top down the chain
		List<Navigation> navList = new LinkedList<Navigation>();
		for ( Navigation directParent = this; directParent != null; directParent = directParent.parent )
		{
			navList.add(0, directParent);
			if ( log.isDebugEnabled() )
				log.debug("stacking " + directParent.toString());

			// No sense re-running navigations that are already complete.
			// Short-circuit at the last completed navigation.
			if ( directParent.hasNavigated )
			{
				if ( log.isDebugEnabled() )
					log.debug(directParent.toString() + " has alredy been navigated.  Shorting.");
				break;
			}
		}

		// This is the "top" of our chain.
		// Due to optimization, this may not be the actual top.
		Navigation top = navList.get(0);
		if ( log.isDebugEnabled() )
			log.debug("Top is " + top.toString());

		// Try for an existing context in case the top of the chain has
		// already run the navigation.
		// Failing that, create one
		NavigationContext currentContext = top.getContext();
		if ( null == currentContext )
		{
			log.debug("Top has no context.  Creating one.");
			currentContext = top.createInitialContext();
		}

		boolean failed = false;
		// Run through the navigation chain
		for ( Navigation current : navList )
		{
			if ( log.isDebugEnabled() )
				log.debug("processing " + current.toString());

			// Set a copy of the current context if there isn't one.
			// Any level that hasn't been navigated yet will be seeded
			// with a copy of the context from the previous operation.
			if ( null == current.getContext() )
			{
				log.debug("Current has no context.  Cloning.");
				current.setContext((NavigationContext)currentContext.clone());
			}

			if ( failed )
			{
				current.failNavigation();
			}
			else
			{
				// Try to navigate this level
				if ( !current.navigate() )
				{
					log.debug("Navigation failed.  Forcing the rest to fail also.");
					failed = true;
				}
			}

			// Keep a reference to the updated context for next iteration
			currentContext = current.getContext();
		}

		log.debug("Navigation complete.");
		// We only navigate down to "this" level.
		// There may be more underneath us, but we're not interested in them.
		return this;
	}

	/**
	 * Check if this level of the navigation exists. <br>
	 * This will run the navigation if it hasn't been run yet.
	 * 
	 * @return true if the navigation successfully reached this level.
	 */
	public boolean exists()
	{
		get();
		return navigationResult;
	}

	/**
	 * Get this navigation's parent.
	 * 
	 * @return this navigation's parent, or null if this is the top of the chain
	 */
	public Navigation getParent()
	{
		return parent;
	}

	private Navigation ultimateParent = null;

	/**
	 * Get the ultimate parent of this object (i.e. the real top of the chain)
	 * 
	 * @return this navigation's ultimate parent
	 */
	public Navigation getUltimateParent()
	{
		if ( null == ultimateParent )
		{
			Navigation lastParent = this;
			while ( null != lastParent.parent )
			{
				// Check for cached ultimate parent
				if ( null != lastParent.ultimateParent )
					lastParent = lastParent.ultimateParent;
				else
					lastParent = lastParent.parent;
			}
			ultimateParent = lastParent;
		}
		return ultimateParent;
	}

	private void setContext(NavigationContext contextIn)
	{
		if ( !hasNavigated )
			context = contextIn;
	}

	public NavigationContext getContext()
	{
		return context;
	}

	/**
	 * Navigate this level. The result of this navigation gets cached.
	 * 
	 * @return true if this level of the navigation was successful.
	 */
	private boolean navigate()
	{
		if ( !hasNavigated )
		{
			navigationResult = navigateThisLevel();
			if ( log.isDebugEnabled() )
				log.debug("New navigation for " + this.toString() + " resulted: " + navigationResult);
			hasNavigated = true;
			// Once navigation is complete, handle the context.
			// This is currently used in HtmlNavigation to clear
			// the temporary context of descriptive navigation
			// values when a real navigation takes place.
			handleContext();
		}
		else
		{
			if ( log.isDebugEnabled() )
				log.debug("Already navigated " + this.toString() + ".  Old result: " + navigationResult);
		}
		return navigationResult;
	}

	/**
	 * Automatically fail a navigation at this level. This is needed to
	 * propagate a failure down a navigation chain
	 */
	private void failNavigation()
	{
		if ( !hasNavigated )
		{
			navigationResult = false;
			if ( log.isDebugEnabled() )
				log.debug("Skipped navigation for " + this.toString() + ".  Forced result to: " + navigationResult);
			hasNavigated = true;
		}
		else
		{
			if ( log.isDebugEnabled() )
				log.debug("Already navigated " + this.toString() + ".  Old result: " + navigationResult);
		}
	}

	/**
	 * Create an initial context to start the navigation.
	 * 
	 * @return an initial context
	 */
	protected NavigationContext createInitialContext()
	{
		setContext(new NavigationContext());
		return getContext();
	}

	/**
	 * Do any post-navigation handling of the context. <br>
	 * This is a separate method to give support for "descriptive" navigations.
	 * <br>
	 * A descriptive navigation will not clear the temporary context, which
	 * allows multiple descriptive navigations to be chained together, to
	 * provide extra behavior specifications for the next "real" navigation in
	 * the chain.
	 */
	protected void handleContext()
	{
		// Descriptive navigations do no real navigation, but rather
		// leave descriptive objects on the temporary context for the next
		// real navigation to use when navigating.

		// Once a real navigation is complete, we clear the temporary context.
		getContext().clearTemporary();
	}

	/**
	 * Perform the actual navigation. <br>
	 * This method handles the concrete implementation of the navigation
	 * 
	 * @return true if the navigation was successful
	 */
	protected abstract boolean navigateThisLevel();
}
