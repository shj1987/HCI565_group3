/* use strict */
var app = angular.module("RavensApp", []);

app.service("floorPlanService", function ($http, $q)
{
	var deferred = $q.defer();
	
	// var _dataObj = {};
 //  	this.dataObj = _dataObj;
 	// var inputValue;

	$http.get('data/sedac-csv.json').then(function (data)
	{
		deferred.resolve(data);
	});

	this.getFloorPlans = function ()
	{
		return deferred.promise;
	}
})

.controller("ravensCtrl", function ($scope, floorPlanService)
{
	var promise = floorPlanService.getFloorPlans();
	promise.then(function (data)
	{
		$scope.floorPlans = data.data;
		// console.log($scope.floorPlans);
	});
})
// ;

// app.config(['$routeProvider', function($routeProvider) {
//     $routeProvider.when('/floorplans/:orderId', {
// 		templateUrl: 'detail.html',
// 		controller: 'ShowOrderController'
//       });
// }])
// .controller('ShowOrderController', function($scope, $routeParams) {
// 	$scope.order_id = $routeParams.orderId;
// })
// .controller('One', function($scope, floorPlanService) {
//   $scope.data = floorPlanService.dataObj;
// })

// .controller('Two', function($scope, floorPlanService) {
//   $scope.data = floorPlanService.dataObj;
// })
;



