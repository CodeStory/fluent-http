try {
  CoffeeScript.compile(__source, {bare: true, sourceMap: true}).v3SourceMap;
} catch (exception) {
  throw 'Unable to compile CoffeeScript ' + exception;
}