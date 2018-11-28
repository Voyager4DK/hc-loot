var app = angular.module("mainApp", ["ngRoute"]);

app.config(function($routeProvider) {
	$routeProvider
	.when('/', {
		templateUrl: 'login.html'
	})
	.when('/main', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'main.html'
	})
	.when('/managePlayers', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'managePlayers.html'
	})
	.when('/manageLootItems', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'manageLootItems.html'
	})
	.when('/wishList', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'wishList.html'
	})
	.when('/results', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'results.html'
	})
	.when('/comments', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'comments.html'		
	});	
	
	function _goToLoginIfNotLoggedId($location, $rootScope) {
		if (!$rootScope.loggedInPlayer) {
			$location.path("/");
		}
	}
});

app.controller('loginCtrl', function($scope, $http, $location, $rootScope) {
	// Initialize page with default data which is blank in this example
    $scope.players = [];
	
    // Now load the data from server
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
            url: 'api/players/enabled'
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
        	console.log("Login success, window.location=" + window.location);
        	$location.path("/main");
        }, function errorCallback(error, statusText) {
        	$rootScope.loggedInPlayer = null;
        	console.log("Login failed with error code: " + error.status); 
        	alert("You failed to login!");
        });
    }
});

app.controller("playerManagementCtrl", function ($scope, $http, $rootScope) {

    // Initialize page with default data which is blank in this example
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

    // Now load the data from server
    _refreshPageData();

    // HTTP POST/PUT methods for add/edit players
    $scope.update = function () {
        var method = "";
        var url = "";
        var data = {};
        if ($scope.form.id == -1) {
            // Id is absent so add players - POST operation
            method = "POST";
            url = 'api/players';            
        } else {
            // If Id is present, it's edit operation - PUT operation
            method = "PUT";
            url = 'api/players/' + $scope.form.id;
        }
        
        data.name = $scope.form.name;
        data.password = $scope.form.password;
        data.gloryPoints = $scope.form.gloryPoints;
        data.lootEnabled = $scope.form.lootEnabled;
        data.enabled = $scope.form.enabled;
        data.admin = $scope.form.admin;

        $http({
            method: method,
            url: url,
            data: angular.toJson(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
    
    // HTTP DELETE- delete player by id
    $scope.remove = function (player) {
        if (!confirm("Are you sure..?")) {
            return;
        }
        $http({
            method: 'DELETE',
            url: 'api/players/' + player.id
        }).then(_success, _error);
    };
    
    // In case of edit players, populate form with player data
    $scope.edit = function (player) {
        $scope.form.id = player.id;
        $scope.form.name = player.name;
        $scope.form.gloryPoints = player.gloryPoints;
        $scope.form.lootEnabled = player.lootEnabled;
        $scope.form.enabled = player.enabled; 
        $scope.form.admin = player.admin;
        _getPassword(player.id);
    };

    /* Private Methods */
    // HTTP GET- get all players collection
    function _refreshPageData() {
    	if ($rootScope.loggedInPlayer.admin) {
    		$scope.admin = true;
    	} else {
    		$scope.admin = false;
    	}
        $scope.playerId = $rootScope.loggedInPlayer.id;
        $http({
            method: 'GET',
            url: 'api/players'
        }).then(function successCallback(response) {
            $scope.players = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }

    function _getPassword(playerId) {
        $http({
            method: 'GET',
            url: 'api/players/' + playerId
        }).then(function successCallback(response) {
            var player = response.data;
            $scope.form.password = player.password;
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

    // Clear the form
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

app.controller("lootItemManagementCtrl", function ($scope, $http) {

    // Initialize page with default data which is blank in this example
    $scope.lootItems = [];

    $scope.form = {
        id: -1,
        rowAndNum: "",
        name: "",
        common: false,
        prioritySequence: ($scope.lootItems.length+1)
    };

    // Now load the data from server
    _refreshPageData();

    // HTTP POST/PUT methods for add/edit lootItems
    $scope.update = function () {
        var method = "";
        var url = "";
        var data = {};
        if ($scope.form.id == -1) {
            // Id is absent so add lootItems - POST operation
            method = "POST";
            url = 'api/loot_items';
            data.prioritySequence = $scope.lootItems.length+1;
        } else {
            // If Id is present, it's edit operation - PUT operation
            method = "PUT";
            url = 'api/loot_items/' + $scope.form.id;
            data.prioritySequence = $scope.form.prioritySequence;
        }
        
        data.rowAndNum = $scope.form.rowAndNum;
        data.name = $scope.form.name;
        data.common = $scope.form.common;
        data.original = true;

        _update(method, url, data);
    };

    // HTTP DELETE- delete lootItem by id
    $scope.remove = function () {
    	if (!confirm("Are you sure you want to delete that?")) {
            return;
        }
    	if ($scope.selectedLootItemId.constructor === Array) {
            var ids = $scope.selectedLootItemId;
            for (i = 0; i < ids.length; i++) {
                _delete(ids[i]);
            }
        } else {
            _delete($scope.selectedLootItemId);
        }
    };

    // In case of edit lootItems, populate form with lootItem data
    $scope.edit = function () {
        if ($scope.selectedLootItemId.constructor === Array && $scope.selectedLootItemId.length > 1) {
            alert("You can only edit 1 item at a time");
        	return;
        }

        $http({
            method: 'GET',
            url: 'api/loot_items/' + $scope.selectedLootItemId
        }).then(function successCallback(response) {
            var lootItem = response.data;
            $scope.form.id = lootItem.id;
            $scope.form.rowAndNum = lootItem.rowAndNum;
            $scope.form.name = lootItem.name;
            $scope.form.common = lootItem.common;
            $scope.form.prioritySequence = lootItem.prioritySequence;            
        }, function errorCallback(response) {
            console.log(response.statusText);
        });

    };
    
    $scope.moveUp = function() {
    	var ids = $scope.selectedLootItemId;
        console.log("ids=" + ids);
    	for (i = 0; i < ids.length; i++) {
    		console.log("ids[" + i + "]=" + ids[i]);
        	var method = "PUT";
            var url = 'api/loot_items/' + ids[i] + "/change_sequence";
            _update(method, url, -1)
        }
        // _refreshPageData();
    };
    
    $scope.moveDown = function() {
    	var ids = $scope.selectedLootItemId;
        for (i = 0; i < ids.length; i++) {        	
        	var method = "PUT";
            var url = 'api/loot_items/' + ids[i] + "/change_sequence";
            _update(method, url, 1)
        }
        // _refreshPageData();
    };

    /* Private Methods */
    // HTTP GET- get all lootItems collection
    function _refreshPageData() {
        $http({
            method: 'GET',
            url: 'api/loot_items'
        }).then(function successCallback(response) {
            $scope.lootItems = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });

        $http({
            method: 'GET',
            url: 'api/loot_items/loot_item_properties'
        }).then(function successCallback(response) {
            $scope.lootItemProperties = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }

    function _delete(id) {
        $http({
            method: 'DELETE',
            url: 'api/loot_items/' + id
        }).then(_success, _error);
    }
    
    function _update(method, url, data) {
    	$http({
            method: method,
            url: url,
            data: angular.toJson(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    }

    function _success(response) {
        _refreshPageData();
        _clearForm()
    }

    function _error(response) {
        console.error(response.statusText);
    }

    // Clear the form
    function _clearForm() {
    	$scope.form.id = -1;
    	$scope.form.rowAndNum = "";
        $scope.form.name = "";
        $scope.form.common = false;
        $scope.form.prioritySequence = null;        
    }
});

app.controller("wishListCtrl", function ($scope, $http, $rootScope) {

    // Initialize page with default data which is blank in this example
    $scope.lootItems = [];

    // Now load the data from server
    if ($rootScope.loggedInPlayer.lootEnabled) {
    	_refreshPageData();
    } else {
    	$scope.lootDisabled = true;
    }

    
    $scope.moveUp = function() {
    	var ids = $scope.selectedEnabledLootItemId;
        for (i = 0; i < ids.length; i++) {
    		console.log("ids[" + i + "]=" + ids[i]);
        	if (ids[i].length > 0) {
        		var method = "PUT";
        		var url = 'api/loot_items/' + ids[i] + "/change_sequence";
        		_update(method, url, -1);
        	}
        }
    };
    
    $scope.moveDown = function() {
    	var ids = $scope.selectedEnabledLootItemId;
        for (i = 0; i < ids.length; i++) {        	
        	var method = "PUT";
            var url = 'api/loot_items/' + ids[i] + "/change_sequence";
            _update(method, url, 1)
        }
    };
    
    $scope.toggle = function(disabled) {
    	var ids = $scope.selectedDisabledLootItemId;
    	if (disabled) {
    		ids = $scope.selectedEnabledLootItemId;
    	}
        for (i = 0; i < ids.length; i++) {        	
        	var method = "PUT";
            var url = 'api/loot_items/' + ids[i] + "/toggle";
            _update(method, url, disabled)
        }
    };
    
    $scope.reset = function() {
    	if (!confirm("Are you sure you want to reset your wish list and reload loot items?")) {
            return;
        }
    	$http({
            method: 'GET',
            url: 'api/loot_items/reset_player/' + $rootScope.loggedInPlayer.id
        }).then(function successCallback(response) {
        	_splitLootItemData(response.data);
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }    

    /* Private Methods */
    // HTTP GET- get all lootItems collection
    function _refreshPageData() {  	
    	$http({
            method: 'GET',
            url: 'api/loot_items/for_player/' + $rootScope.loggedInPlayer.id
        }).then(function successCallback(response) {
        	_splitLootItemData(response.data);
        }, function errorCallback(response) {
            console.log(response.statusText);
        });

        $http({
            method: 'GET',
            url: 'api/loot_items/loot_item_properties'
        }).then(function successCallback(response) {
            $scope.lootItemProperties = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }
    
    function _splitLootItemData(lootItems) {
    	$scope.lootItems = [];
    	$scope.disabledLootItems = [];
    	var disabledLootItems
    	for (var i = 0; i < lootItems.length; i++) {
			if (lootItems[i].disabled) {
				$scope.disabledLootItems[$scope.disabledLootItems.length] = lootItems[i];
			} else {
				$scope.lootItems[$scope.lootItems.length] = lootItems[i];
			}
		}
    }
    
    function _update(method, url, data) {
    	$http({
            method: method,
            url: url,
            data: angular.toJson(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    }

    function _success(response) {
        _refreshPageData();
    }

    function _error(response) {
        console.error(response.statusText);
    }
});

app.controller("resultsCtrl", function ($scope, $http, $rootScope) {

	_refreshPageData();
	
	$scope.distributeLoot = function() {
    	if (!confirm("Are you sure you want to distribute loot?")) {
            return;
        }
    	$http({
            method: 'GET',
            url: 'api/result/distribute'
        }).then(function successCallback(response) {
        	$scope.results = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }
	
	
	function _refreshPageData() {
		if ($rootScope.loggedInPlayer.admin) {
    		$scope.admin = true;
    	} else {
    		$scope.admin = false;
    	}
		$http({
            method: 'GET',
            url: 'api/result'
        }).then(function successCallback(response) {
            $scope.results = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
		
    	$http({
            method: 'GET',
            url: 'api/loot_items/loot_item_properties'
        }).then(function successCallback(response) {
            $scope.lootItemProperties = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }
});	

app.controller("commentsCtrl", function ($scope, $http, $rootScope) {

	_refreshPageData();
	
	function _refreshPageData() {  	
    	$http({
            method: 'GET',
            url: 'api/loot_items/loot_item_properties'
        }).then(function successCallback(response) {
            $scope.lootItemProperties = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }
});