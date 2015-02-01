AngularJs Annotate Asset-Pipeline
================================

The Grails `angular-annotate-asset-pipeline` is a plugin that provides annotations for dependecy injection to allow angular JavaScript or CofeeScript files to be minified within the asset pipeline. A more recent version of the Asset Pipeline (2.0+) is required.

For more information on how to use asset-pipeline, visit [here](http://www.github.com/bertramdev/asset-pipeline).

## Getting started
Add the plugin to your **BuildConfig.groovy**:
```groovy
plugins {
		runtime ":angular-annotate-asset-pipeline:2.0.3"
}
```

## How it works

Since the parameter names are significant for AngularJS to do dependency injection and most minifiers rename parameters,
you have to use inline annotations. See [A Note on Minification](https://docs.angularjs.org/tutorial/step_05)

This plugin uses [ng-annotate v0.15.4](https://github.com/olov/ng-annotate) to add those inline annotations and make your angular files safe to minify.

For example this 
```javascript
myApp.controller('IndexController', function($scope) {
	$scope.message = "Hello world";
});
```

Will be automatically annotated like so:
```javascript
myApp.controller('IndexController', ['$scope', function($scope) {
	$scope.message = "Hello world";
}]);
```
