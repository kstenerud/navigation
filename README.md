Navigation
==========

Archived on GitHub. Original (from 2005): http://navigation.sourceforge.net/


What it is
----------

Navigation implements a design pattern that allows you to navigate through complex systems by means of "navigation chains".

Each node in the navigation chain is represented as its own object, and can have any number of child links attached to it (done through upwards linking; navigations have no knowledge of their children). This means that you can create a navigation chain to a common point in your system, and then attach multiple subchains at that point. The navigation to that point will only be resolved once; subchains will use the already-resolved context and work from there.


Proof of Concept
----------------

The Html navigations are included as a proof of concept, and sit on top of HtmlUnit to provide an easy mechanism to navigate html documents.


Html Navigation Concepts
------------------------

I've had to take a few liberties that would otherwise be considered bad design in order to provide an intuitive calling scheme in a typesafe language that has no true multiple inheritance mechanism.

First, `HtmlNavigation` contains a LOT of factory methods for identifying elements by tag name and by attribute name.
It is entirely possible to attempt to navigate to a `div` element by its `href` attribute for example (the navigation will fail, mind you). This calls for diligence on the part of the developer in choosing which factory methods to chain together.

As well, the concept of descriptions, navigations, and actions (see below for an explanation) should really be handled above the html-specific navigations, but I could think of no clean way to implement it in a single-inheritance language without putting an extra burden on navigation developers.

Bear in mind that this is a work in progress. I don't have all the answers on this, so if you have a good idea on how to improve the architecture, please drop me a line.


HtmlNavigation
--------------

HtmlNavigation forms the base class of all html navigations. It provides factory methods for all common element types and attributes, and some others for node collection and indexing. In a loosely typed language, many of these methods would be moved to various subclasses. I decided to trade off ease of development (any new factory methods MUST be placed in HtmlNavigation) for ease of use (intuitive method chaining).


Navigation Types
----------------

There are three types of methods available through HtmlNavigation:

### Description

This "navigation" does not actually move you to a new navigation point, but rather provides hints on a temporary context for the next real navigation to use when resolving the next navigation point.

### Navigation

Navigates from its parent's context point in the system to another point. Navigations may use temporary context hints provided by previous descriptions when navigating, but they must always clear the temporary context after navigation.

### Action

This is not really a navigation per-se. Action methods do not return a navigation object; they are used to test for success, get representations of the current point in other data formats, or alter the state of the system being navigated.


WebNavigator
------------

WebNavigator adapts WebClient, providing you with the ability to mimic a web browser, and providing the initial context for html page navigations.

There are two ways to create an initial context with WebNavigator:

* `gotoUrl()`: use this to cause WebNavigator to resolve a url and load the page into its context. Note that the page MUST be html or it will throw an exception. I have not put in support for other page types.
* `activate()`: Calling this on an activatable node (buttons, links and such) will cause the WebNavigator's page to change. Note that any previously created navigations will still point to the old page!

Once the navigator is on an html page, you can build navigation chains from it. `page()` builds a navigation that points to the page itself, which is always of type HtmlPage (other page types are not supported - yet).

WebNavigator also provides some convenience methods:

* `html()`: navigates to the html tag
* `htmlChildren()`: navigates to a list of all children of the html tag
* `head()`: navigates to the html tag
* `headChildren()`: navigates to a list of all children of the html tag
* `title()`: navigates to the title tag
* `body()`: navigates to the body tag
* `bodyChildren()`: navigates to a list of all children of the body tag
* `frames()`: navigates to a list of all of the frames in this page


Example Usage
-------------

Given the following html page:

```html
<html>
  <head>
    <title>This is a title</title>
  </head>
  <body>
    <h1>This is a header</h1>
    Here is some text
    <br>
    Here is an <a id="nextPage" href="page2.html">anchor</a>
    <div id="section1">
      <span class="highlighted">This is a section</span>
      <div id="subsectionA">
        <h2>This is a subsection</h2>
        Some more text in the subsection
        <br>
        <a id="randomLink" href="goRandom.html">Click here to go a random location</a>
      </div>
      <div id="subsectionB">
          Now for an image: <img src="someImage.png" alt="some image">
      </div>
    </div>
  </body>
</html>
```    

You would set up an initial context like this:

```java
WebNavigator nav = new WebNavigator();
nav.gotoUrl(someUrl);
```    

Suppose you wanted to click the anchor that claims to take you to a random location. There are many ways to navigate to it:

```java
nav.bodyChildren().deep().id("randomLink").activate();

// Same as above, but the search for id "randomLink" starts at the page level
nav.page().deep().id("randomLink").activate();

// Gets the first element whose id matches starts with "random" and attempts to activate it.
// Note that if we had, say, a BR tag earlier in the page that had an id of "randomBreak",
// the navigation would attempt to activate it and throw an exception.
nav.page().deep().pattern().id("random.*").activate();

// Search for the anchor by href.  Here you assume that the href will always be "goRandom.html".
nav.page().deep().href("goRandom.html").activate();

// Do a step-by-step navigation down the dom.  This assumes a lot about the page layout,
// and makes for a very brittle navigation.
nav.bodyChildren().div().id("section1").children().div().id("subsectionA").children().a().activate();

// Do a deep search for an anchor that contains the exact text "Click here to go a random location"
nav.page().deep().a().text("Click here to go a random location").activate();

// Do a deep search for an anchor whose text contains "random" at any point.
nav.page().deep().a().pattern().text(".*random.*").activate();
```


Further Examples
----------------

There are many more things you can do with the html navigations, such as fill out and submit forms, get attributes and text representations, and check if a particular navigation chain is resolvable. See the unit tests in the source distribution as they contain tests for each of the navigations.


License and Copyright
---------------------

Copyright 2005 Karl Stenerud

Navigation is licensed under the Apache License, version 2.0


Downloads
---------

[Click here to go to the download page](https://sourceforge.net/project/showfiles.php?group_id=143763)


SourceForge
-----------

[Click here to go to the sourceforge project page](http://sourceforge.net/projects/navigation/)


Changes
-------

### Release 1.2:

* Updated to use Java 1.5
* Updated some libraries because the older ones were causing problems sometimes.

### Release 1.1:

* Fixed a bug that caused a null pointer exception if you used certain actions on an unnavigatable chain.
* Added getAttribute() action
* Added parent(), before(), after(), not() navigations

### Release 1.0:

* Initial public release

