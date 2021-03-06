Fixes and improvements in node-sybase-plus
------------

* removed a <b>[df.setTimeZone(TimeZone.getTimeZone("UTC"));]</b> from the <b>[SybaseDB.java]</b> class ( it brings wrong results when you don't need a Universal Time Zone )
* added <b>columns</b> parameter in a query callback function. The reason is JSON result doesn't keep right columns order. Example:<br/>
db.query("select * ...", function (err, data, <b>columns</b>) {...
* added a <b>query2Csv(query, callback, csvOpts)</b> function which produces a CSV output.<br/>
The <b>callback</b> function also brings a regular <b>jsonData</b>: callback(err, <b>csvData</b>, columns, <b>jsonData</b>) {...<br/>
Optional <b>csvOpts</b> are described in [json2csv](https://www.npmjs.com/package/json2csv) package ( the <b>fields</b> parameter in the <b>csvOps</b> is provided automatically by the framework if not specified )<br/>


node-sybase
---------

A simple node.js wrapper around a Java application that provides easy access to Sybase databases via jconn3. The main goal is to allow easy installation without the requirements of installing and configuring odbc or freetds. You do however have to have java 1.5 or newer installed.


requirements
------------

* java 1.5+

install
-------

### git

```bash
git clone git://github.com/rodhoward/node-sybase.git
cd node-sybase
node-gyp configure build
```
### npm

```bash
npm install sybase
```

quick example
-------------

```javascript
var Sybase = require('sybase'),
	db = new Sybase('host', port, 'dbName', 'username', 'pw');

db.connect(function (err) {
  if (err) return console.log(err);
  
  db.query('select * from user where user_id = 42', function (err, data) {
    if (err) console.log(err);
    
    console.log(data);

    db.disconnect();

  });
});
```

api
-------------

The api is super simple. It makes use of standard node callbacks so that it can be easily used with promises. The only thing not covered in the example above is the option to print some timing stats to the console as well as to specify the location of the Java application bridge, which shouldn't need to change.

```javascirpt 
var logTiming = true,
	javaJarPath = './JavaSybaseLink/dist/JavaSybaseLink.jar',
	db = new Sybase('host', port, 'dbName', 'username', 'pw', logTiming, javaJarPath);
```

The java Bridge now optionally looks for a "sybaseConfig.properties" file in which you can configure jconnect properties to be included in the connection. This should allow setting properties like:
```properties
ENCRYPT_PASSWORD=true
```