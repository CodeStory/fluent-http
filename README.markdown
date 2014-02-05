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
routes.get("/hello/:who", (context, name) -> "Hello " + name);
routes.get("/add/:first/to/:second", (context, first, second) -> Integer.parseInt(first) + Integer.parseInt(second));
```

Notice that path parameters have to be of type `String`.

## Resources

The notation with lambdas is very compact but cannot support path parameters of type other than `String`. So we've added
the notion of resource, in a way similar to jaxb.

```java
routes.add(new CalculationResource());

public class CalculationResource {
  @Get("/add/:first/to/:second")
  public int add(int first, int second) {
    return first + second;
  }
}
```

Each method annotated with `@Get` is a route. The method can have any name. The number of parameters must match the uri
pattern. Parameters names are not important but it's a good practice to match the uri placeholders. The conversion between
path parameters and method parameters is done with [Jackson](http://jackson.codehaus.org/).

We can also let the web server take care of the resource instantiation. It will create a singleton for each resource,
and recursively inject dependencies as singletons. It's a kind of poor's man DI framework.

```java
routes.add(CalculationResource.class);
```

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

## Site variables

In addition to the variables defined in the Yaml Front Matter section, some site-wide variables are available.

 - All variables defined in the `app/_config.yml` file
 - `site.data` is a map of every file in `app/_data/` parsed and indexed by its file name (without extension)
 - `site.pages` is a list of all static pages in `app/`. Each entry is a map containing all variables in the file's YFM section, plus `content`, `path` and `name` variables.
 - `site.tags` is a map of every tag defined in static pages YMF sections. For each tag, there's the list of the pages with this tag. Pages can have multiple tags.
 - `site.categories`is a map of every category defined in static pages YMF sections. For each category, there's the list of the pages with this category. Pages can have one or zero category.

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

Ok, so its easy to mimic the behavior of a static website generated with Jekyll. But what about dynamic pages. Turns
out it's heady too.

Let's create a `hello.md` page with an unbound variable.

```markdown
Hello [[name]]
```

If we query `/hello`, the name will be replaced with an empty string since nowhere does it say what its value is. The solution
is to override the default route to `/hello` as is:

```java
routes.get("/hello", Model.of("name", "Bob");
```

Now, when the pages is rendered, `[[name]]` will be replaced server-side with `Bob`.

If not specified, the name of the page (ie. the view) to render for a given uri is guessed after the uri. Files are
looked up in this order: `uri`, `uri.html`, `uri.md`, `uri.markdown`, `uri.txt` then `uri.asciidoc`. Most of the time
it will *just work*, but the view can of course be overridden:

```java
routes.get("/hello/:whom", (context, whom) -> ModelAndView.of("greeting", "name", whom);
```

## Return types

A route can return any Object, the server will try to guess what to do with it:

 - `java.lang.String` is interpreted as inline html with content type `text/html;charset=UTF-8`.
 - `byte[]` is interpreted as `application/octet-stream`.
 - `java.io.InputStream` is interpreted as `application/octet-stream`.
 - `java.io.File` is interpreted as a static file. The content type is guessed from file's extension.
 - `java.nio.file.Path` is interpreted as a static file. The content type is guessed from file's extension.
 - `Model` is interpreted as a template which name is guessed, rendered with given variables. The content type is
 guessed from file's extension.
 - `ModelAndView` is interpreted as a template with given name, rendered with given variables. The content type is
 guessed from file's extension.
 - `void` is empty content.
 - any other type is serialized to json with content type `application/json;charset=UTF-8`.

## POST

Now that the website is dynamic, we might also want to post data. We support `GET`, `POST`, `PUT` and `DELETE` methods.
Here's how one would post data.

```java
routes.post("/person", (context) -> {
  String name = context.get("name");
  int age = context.getInteger("age");

  Person person = new Person(name, age);
  // do something

  return Payload.created();
});
```

It's even easier to let [Jackson](http://jackson.codehaus.org/) do the mapping between form parameters and Java Beans.

```java
routes.post("/person", (context) -> {
  Person person = context.payload(Person.class);
  // do something

  return Payload.created();
});
```

Using the annotated resource syntax, it's even simpler:

```java
public class PersonResource {
  @Post("/person")
  public void create(Person person) {
    repository.add(person);
  }
}
```

Multiple methods can be used for the same uri:

```java
public class PersonResource {
  @Get("/person/:id")
  public Model show(String id) {
    return Model.of("person", repository.find(id));
  }

  @Put("/person/:id")
  public void update(String id, Person person) {
    repository.update(person);
  }
}
```

Same goes for the lambda syntax:

```java
routes.
  get("/person/:id", (context, id) -> Model.of("person", repository.find(id))).
  put("/person/:id", (context, id) -> {
    Person person = context.payload(Person.class);
    repository.update(person);

    return Payload.created();
  });
}
```

Or to avoid duplication:

```java
routes
  .with("/person/:id").
    get((context, id) -> Model.of("person", repository.find(id))).
    put((context, id) -> {
      Person person = context.payload(Person.class);
      repository.update(person);

      return Payload.created();
    });
}
```

## SSL

Starting the web server in SSL mode is very easy. You need a certificate file (`.crt`) and a private key file (`.der`),
That's it. No need to import anything in a stupid keystore. It cannot be easier!

```java
new Webserver().startSSL(9443, Paths.get("server.crt"), Paths.get("server.der"));
```

## Errors

TODO

## Json support

TODO

## Cookies

TODO

## Payload

TODO

## Filters

Cross-cutting behaviors can be implemented with filters. For example, one can log every request to the server
with this filter:

```java
routes.filter((uri, context, next) -> {
  System.out.println(uri);
  return next.get();
})
```

A filter can be defined in its own class:

```java
routes.filter(LogRequestFilter.class);

public class LogRequestFilter implements Filter {
  @Override
  public Payload apply(String uri, Context context, PayloadSupplier next) throws IOException {
    System.out.println(uri);
    return next.get();
  }
}
```

A filter can either pass to the next filter/route by returning `next.get()` or it can totally bypass the chain of
filters/routes by returning its own Payload. For example, a Basic Authentication filter would look like:

```java
public class BasicAuthFilter implements Filter {
  private final String uriPrefix;
  private final String realm;
  private final List<String> hashes;

  public BasicAuthFilter(String uriPrefix, String realm, Map<String, String> users) {
    this.uriPrefix = uriPrefix;
    this.realm = realm;
    this.hashes = new ArrayList<>();

    users.entrySet().forEach((entry) -> {
      String user = entry.getKey();
      String password = entry.getValue();
      String hash = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());

      hashes.add("Basic " + hash);
    });
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    if (!uri.startsWith(uriPrefix)) {
      return nextFilter.get(); // Ignore
    }

    String authorizationHeader = context.getHeader("Authorization");
    if ((authorizationHeader == null) || !hashes.contains(authorizationHeader.trim())) {
      return Payload.unauthorized(realm);
    }

    return nextFilter.get();
  }
}
```

Both `BasicAuthFilter` and `LogRequestFilter` are pre-packaged filters that you can use in your applications.

## Twitter Auth

TODO

## Dependency Injection

TODO

## Markdown extensions

TODO (Formulas, Tables, ...)

## HandleBars extensions

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

 + Javadoc
 + PROD_MODE is not really convenient. It forces to have more complex prod script
 + Add some sort of http standard log
 + Cleanup Payload class. Make Payload immutable?
 + Cors support
 + monitoring
 + Optional Payload?
 + If-Modified-since activé par défaut
 + Laisser le Compiler Cache donner le last modified
 + Gere les last modified dans Payload
 + Auto reload meme sans les lambda avec capture de variables locales
 + Streaming
 + File upload
 + OPTIONS method

# TODO Maybe

 + Singletons qui utilise les annotations standards
 + Remplacer Simple par un Servlet Filter qui fonctionne par defaut sur un Jetty Http
 + Help use local storage
 + Add your own/reuse Servlet filters
 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Fonctionnement en mode war
 + TRACE method
 + CONNECT method
 + nio
