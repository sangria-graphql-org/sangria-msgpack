[Sangria](http://sangria-graphql.org/) [MessagePack](http://msgpack.org/) marshalling.

[![Build Status](https://travis-ci.org/sangria-graphql-org/sangria-msgpack.svg?branch=master)](https://travis-ci.org/sangria-graphql-org/sangria-msgpack) [![Coverage Status](http://coveralls.io/repos/sangria-graphql-org/sangria-msgpack/badge.svg?branch=master&service=github)](http://coveralls.io/github/sangria-graphql-org/sangria-msgpack?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.sangria-graphql/sangria-msgpack_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.sangria-graphql/sangria-msgpack_2.11) [![License](http://img.shields.io/:license-Apache%202-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt) [![Join the chat at https://gitter.im/sangria-graphql/sangria](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sangria-graphql/sangria?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

SBT Configuration:

```scala
libraryDependencies += "org.sangria-graphql" %% "sangria-msgpack" % "1.0.0"
```

## BigDecimal handling

MessagePack does not support `BigDecimal` natively. However it supports [extension types](https://github.com/msgpack/msgpack/blob/master/spec.md#types-extension-type). 
 
In order to provide `BigDecimal` support, sangria-msgpack implements an extension type with type ID `47`. It packs the scale in the first 4 bytes (int) followed by unscaled big integers value. This is the default behaviour, so you don't need any addition import for it.

If you would like to allow sangria-msgpack to pack `BigDecimal` values as standard types (big integer and double), then you need to add following import:
 
```scala
import sangria.marshalling.msgpack.standardTypeBigDecimal._
```

Please use it with caution because it will throw `IllegalArgumentException` if number does not fit in big integer or double.

## License

**sangria-msgpack** is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
