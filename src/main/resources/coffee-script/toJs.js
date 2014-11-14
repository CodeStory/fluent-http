try {
  CoffeeScript.compile(__source, {bare: true, literate: __literate});
} catch (exception) {
  throw 'Unable to compile CoffeeScript ' + exception;
}