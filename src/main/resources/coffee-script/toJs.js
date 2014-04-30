try {
  CoffeeScript.compile(__source, {bare: true});
} catch (e) {
  throw 'Unable to compile CoffeeScript ' + e;
}