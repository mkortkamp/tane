See [The Strategy Pattern](http://en.wikipedia.org/wiki/Strategy_pattern) for details about the strategy pattern.

This refactoring will result in a new class, the strategy, to be created which is potentially injected into the original class, thus enabling different strategies to be used by the original class.

I guess it could be argued that this is just [Replace Method With Method Object](http://www.refactoring.com/catalog/replaceMethodWithMethodObject.html) + [Extract Interface](http://www.refactoring.com/catalog/extractInterface.html).