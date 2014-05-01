try {
  CoffeeScript.compile(__source, {bare: true});
} catch (exception) {
  throw 'Unable to compile CoffeeScript ' + exception;
}