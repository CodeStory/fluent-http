# Code-Story Http

This is the simplest fastest full fledged http server we could come up with.

# Build status

[![Build Status](https://api.travis-ci.org/CodeStory/code-story-http.png)](https://api.travis-ci.org/CodeStory/code-story-http.png)

# Build instructions

Prerequisites
- Java 1.8
- Apache Maven 3

Build the project

```bash
mvn verify
```

## Generate missing licenses

```bash
mvn license:format
```

# Usage

One of our goals was to make it as easy as possible to start with.

## Maven

Release versions are deployed on Maven Central:

```xml
<dependency>
  <groupId>net.code-story</groupId>
  <artifactId>http</artifactId>
  <version>1.27</version>
</dependency>
```

## Hello World

Starting a web server that responds `Hello World` on `/` uri is as simple as that:

```java
import net.codestory.http.*;

public class HelloWorld {
  public static void main(String[] args) {
    new WebServer(routes -> routes.get("/", "Hello World")).start(8080);
  }
}
```

Adding more routes is not hard either:

```java
new WebServer(routes -> routes.
    get("/", "Hello World").
    get("/Test", "Test").
    get("/OtherTest", "Other Test")
).start(8080);
```

## Path parameters

Routes can have path parameters:

```java
get("/hello/:who", (context, name) -> "Hello " + name)
get("/add/:first/to/:second", (context, first, second) -> Integer.parseInt(first) + Integer.parseInt(second))
```

Notice that path parameters have to be of type `String`. For other kinds, we'll see later how it can be achieved.

## Static pages

When a web server is started. It automatically treats files found in `app` folder as static pages. The `app` folder
is searched first on the classpath and then in the working directory.
So the simplest way to start a web server is in fact:

```java
import net.codestory.http.*;

public class HelloWorld {
  public static void main(String[] args) {
    new WebServer().start(8080);
  }
}
```

## Random port

Instead of providing a fixed port, you can also let the web server find a tcp port available.

```java
int port = new WebServer().startOnRandomPort().port();
```

This is specially helpful for integration tests running in parallel. Not that the way it finds a port available is
bulletproof on every OS. It chooses a random port, tries to start the web server and retries with a different port
in case of error. This is much more reliable than the usual technique that relies on:

```java
ServerSocket serverSocket = new ServerSocket(0);
int port = serverSocket.getLocalPort();
serverSocket.close();
```

## NOHTML (Not Only HTML)

The web server recognizes html files but not only. It is also able to transform more user-friendly file formats on the fly:

 + Html (`.html`)
 + Markdown (`.md` or `.markdown`) -> Html
 + Asciidoc (`.asciidoc`) -> Html
 + Xml (`.xml`)
 + Css (`.css`)
 + Less (`.less`) -> Css
 + Javascript (`.js`)
 + Coffeescript (`.coffee` or `litcoffee`) -> Javascript
 + Zip (`.zip`)
 + Gz (`.gz`)
 + Pdf (`.pdf`)
 + Gif (`.gif`)
 + Jpeg (`.jpeg` or `jpg`)
 + Png (`.png`)
 + All other files are treated as plain text.

## Yaml Front Matter

Static pages can use Yaml Front Matter as in [Jekyll](http://jekyllrb.com/docs/frontmatter/). For example this `index.md`
file:

```markdown
---
greeting: Hello
to: World
---
[[greeting]] [[to]]
```

Will be rendered as:

```html
<p>Hello World</p>
```

## Handlebars

To make Yaml Front Matter even more useful, static pages can use [HandleBar](http://handlebarsjs.com/) template engine.

```markdown
---
names: [Doc, Grumpy, Happy]
---
[[#each names]]
 - [[.]]
[[/each]]
```

Will be rendered as:

```html
<ul>
<li><p>Doc</p>
</li>
<li><p>Grumpy</p>
</li>
<li><p>Happy</p>
</li>
</ul>
```

## Layouting

Like in [Jekyll](http://jekyllrb.com/), pages can be decorated with a layout. The name of the layout should be configured
in the Yaml Front Matter section.

For example, given this `app/_layouts/default.html` file:

```html
<!DOCTYPE html>
<html lang="en">
<body>
[[body]]
</body>
</html>
```

and this `app/index.md` file:

```markdown
Hello World
```

A request to `/` will give this result:

```html
<!DOCTYPE html>
<html lang="en">
<body>
<p>Hello World</p>
</body>
</html>
```

A layout file can be a `.html`, `.md`, `.markdown`, `.txt` or `.asciidoc` file. It should be put in `app/_layouts` folder.
The layout name used in the Yaml Front Matter section can omit the layout file extension.
Layouts are recursive, ie a layout file can have a layout.

A layout can use variables defined in the rendered file. Here's an example with an html title:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <title>[[title]]</title>
</head>
<body>
  [[body]]
</body>
</html>
```

and this `app/index.md` file:

```markdown
---
title: Greeting
---
Hello World
```

A request to `/` will give this result:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Greeting</title>
</head>
<body>
  <p>Hello World</p>
</body>
</html>
```

## Webjars

We also support [WebJars](http://www.webjars.org/) to server static assets.
Just add a maven dependency to a WebJar and reference the static resource in your pages with the `/webjars/` suffix.

Here's an example with Bootstrap:

```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>bootstrap</artifactId>
  <version>3.0.3</version>
</dependency>
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="/webjars/bootstrap/3.0.3/css/bootstrap.min.css">
</head>
<body>
  <p>Hello World</p>
</body>
</html>
```

## Dynamic pages

TODO

## POST

TODO

## Resources

TODO

## Json support

TODO

## Cookies

TODO

## Errors

TODO

## Filters

TODO

## Dependency Injection

TODO

## With syntax

TODO

## Basic Auth

TODO

## Twitter Auth

TODO

## SSL

TODO

## Markdown extensions

TODO (Formulas, Tables, ...)

## Site variables

TODO

## HandleBars extensions

TODO

## Manual payload

TODO

## Etag

TODO

# Deploy on Maven Central

Build the release:

```bash
mvn release:clean
mvn release:prepare
mvn release:perform
```

Go to [https://oss.sonatype.org/](https://oss.sonatype.org/), log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

# TODO

 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Javadoc
 + PROD_MODE is not really convenient. It forces to have more complex prod script
 + Add some sort of http standard log
 + Cleanup Payload class. Make Payload immutable?
 + Cors support
 + Principal
 + monitoring
 + Optional Payload?
