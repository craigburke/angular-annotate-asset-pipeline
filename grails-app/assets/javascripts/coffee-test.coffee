#= require angular.min

coffeeApp = angular.module 'coffeeApp', []

coffeeApp.controller 'TestController', ($scope) ->
	$scope.message = 'Hello World'
