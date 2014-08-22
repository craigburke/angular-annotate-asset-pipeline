//= require angular.min

var jsApp = angular.module('jsApp', []);

jsApp.controller('TestController', function($scope) {
    $scope.message = "Hello world";
});