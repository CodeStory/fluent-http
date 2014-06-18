try {
  var filename = __filename.toString() + '.source'
  var v3sourceMap = JSON.parse(CoffeeScript.compile(__source, {bare: true, sourceMap: true, filename: filename}).v3SourceMap);
  v3sourceMap.file = filename;
  v3sourceMap.sources = [filename];
  delete v3sourceMap['sourceRoot'];
  JSON.stringify(v3sourceMap, null, " ");
} catch (exception) {
  throw 'Unable to compile CoffeeScript ' + exception;
}