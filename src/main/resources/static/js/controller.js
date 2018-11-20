var app = angular.module("mainApp", ["ngRoute"]);

app.config(function($routeProvider) {
	$routeProvider
	.when('/', {
		templateUrl: 'login.html'
	})
	.when('/menu', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'menu.html'
	})
	.when('/managePlayers', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'managePlayers.html'			
	});	
	
	function _goToLoginIfNotLoggedId($location, $rootScope) {
		if (!$rootScope.loggedInPlayer) {
			$location.path("/");
		}
	}
});

app.controller('loginCtrl', function($scope, $http, $location, $rootScope) {
	//Initialize page with default data which is blank in this example
    $scope.players = [];
	
    //Now load the data from server
    _refreshPageData();
    
	$scope.login = function() {
		var loginData = {
			playerId: $scope.playerId,
			password: "" + $scope.password
		};
		
		_loginPlayer(loginData);
	};
	
	function _refreshPageData() {        
		$http({
            method: 'GET',
            url: 'api/players'
        }).then(function successCallback(response) {
        	$scope.players = response.data;
        }, function errorCallback(response) {
            console.log("failure: " + response.statusText);
        });
    }
	
	function _loginPlayer(loginData) {
		$http({
            method: 'POST',
            url: 'api/login',
            data: angular.toJson(loginData),
            headers: {
                'Content-Type': 'application/json'
            }		
        }).then(function successCallback(response) {
        	$rootScope.loggedInPlayer = response.data;        	
        	console.log("Login success");
        	$location.path("/menu");        	
        	//window.location = "/index.html";
        }, function errorCallback(error, statusText) {
        	$rootScope.loggedInPlayer = null;
        	console.log("Login failed with error code: " + error.status); 
        	alert("You failed to login!");
        });
    }
});

app.controller("playerManagementCtrl", function ($scope, $http, $rootScope) {

    //Initialize page with default data which is blank in this example
    $scope.players = [];
    
    $scope.form = {
        id: -1,
        name: "",
        password: "",
        gloryPoints: 0,
        lootEnabled: true,
        enabled: true,
        admin: false
    };

    //Now load the data from server
    _refreshPageData();

    //HTTP POST/PUT methods for add/edit players
    $scope.update = function () {
        var method = "";
        var url = "";
        var data = {};
        if ($scope.form.id == -1) {
            //Id is absent so add players - POST operation
            method = "POST";
            url = 'api/players';
            data.name = $scope.form.name;
            data.password = $scope.form.password;
            data.gloryPoints = $scope.form.gloryPoints;
            data.lootEnabled = $scope.form.lootEnabled;
            data.enabled = $scope.form.enabled;
            data.admin = $scope.form.admin;
        } else {
            //If Id is present, it's edit operation - PUT operation
            method = "PUT";
            url = 'api/players/' + $scope.form.id;
            data.name = $scope.form.name;
            data.password = $scope.form.password;
            data.gloryPoints = $scope.form.gloryPoints;
            data.lootEnabled = $scope.form.lootEnabled;
            data.enabled = $scope.form.enabled;
            data.admin = $scope.form.admin;
        }

        $http({
            method: method,
            url: url,
            data: angular.toJson(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
    
    //HTTP DELETE- delete player by id
    $scope.remove = function (player) {
        $http({
            method: 'DELETE',
            url: 'api/players/' + player.id
        }).then(_success, _error);
    };
    
    //In case of edit players, populate form with player data
    $scope.edit = function (player) {
        $scope.form.id = player.id;
        $scope.form.name = player.name;
        $scope.form.password = player.password;
        $scope.form.gloryPoints = player.gloryPoints;
        $scope.form.lootEnabled = player.lootEnabled;
        $scope.form.enabled = player.enabled; 
        $scope.form.admin = player.admin;
    };

    /* Private Methods */
    //HTTP GET- get all players collection
    function _refreshPageData() {
    	if ($rootScope.loggedInPlayer.admin) {
    		$scope.admin = true;
    	} else {
    		$scope.admin = false;
    	}
        $http({
            method: 'GET',
            url: 'api/players'
        }).then(function successCallback(response) {
            $scope.players = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }

    function _success(response) {
        _refreshPageData();
        _clearForm()
    }

    function _error(response) {
        console.error(response.statusText);
    }

    //Clear the form
    function _clearForm() {
        $scope.form.id = -1;
        $scope.form.name = "";
        $scope.form.password = "";
        $scope.form.gloryPoints = "";                
        $scope.form.lootEnabled = true;
        $scope.form.enabled = true;
        $scope.form.admin = false;
    }
});
