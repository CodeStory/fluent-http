try {
  CoffeeScript.compile(__source, {bare: true, sourceMap: true}).v3SourceMap;
} catch (e) {
  throw 'Unable to compile CoffeeScript ' + e;
}