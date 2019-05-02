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

import org.stenerud.navigation.Navigation;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Modify the value of a settable element.
 * 
 * @author Karl Stenerud
 */
public class SetValueNavigation extends HtmlNavigation
{
	private String value;

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, String value)
	{
		super(parent);
		this.value = value;
	}

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, boolean value)
	{
		super(parent);
		this.value = String.valueOf(value);
	}

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, int value)
	{
		super(parent);
		this.value = String.valueOf(value);
	}

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, long value)
	{
		super(parent);
		this.value = String.valueOf(value);
	}

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, float value)
	{
		super(parent);
		this.value = String.valueOf(value);
	}

	/**
	 * Constructor
	 * 
	 * @param parent this navigation's parent
	 * @param value the value to set
	 */
	public SetValueNavigation(Navigation parent, double value)
	{
		super(parent);
		this.value = String.valueOf(value);
	}

	private boolean allElementsSettable()
	{
		for ( DomNode node : getNodeList() )
		{
			if ( !(node instanceof HtmlCheckBoxInput || node instanceof HtmlFileInput || node instanceof HtmlPasswordInput
					|| node instanceof HtmlRadioButtonInput || node instanceof HtmlTextInput || node instanceof HtmlTextArea) )
				return false;
		}
		return true;
	}

	protected boolean navigateThisLevel()
	{

		if ( !allElementsSettable() )
			return false;

		for ( DomNode node : getNodeList() )
		{
			if ( node instanceof HtmlCheckBoxInput )
			{
				((HtmlCheckBoxInput)node).setChecked(Boolean.valueOf(value).booleanValue());
			}
			else if ( node instanceof HtmlFileInput )
			{
				((HtmlInput)node).setValueAttribute(value);
			}
			else if ( node instanceof HtmlPasswordInput )
			{
				((HtmlInput)node).setValueAttribute(value);
			}
			else if ( node instanceof HtmlRadioButtonInput )
			{
				((HtmlRadioButtonInput)node).setChecked(Boolean.valueOf(value).booleanValue());
			}
			else if ( node instanceof HtmlTextInput )
			{
				((HtmlInput)node).setValueAttribute(value);
			}
			else if ( node instanceof HtmlTextArea )
			{
				((HtmlTextArea)node).setText(value);
			}
			else
			{
				throw new RuntimeException("Element " + node.getClass().getName() + " has no settable attributes.");
			}

		}
		return true;
	}

	public String toString()
	{
		return "setValue";
	}
}
