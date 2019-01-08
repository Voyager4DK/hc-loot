var app = angular.module("mainApp", ["ngRoute"]);

app.factory('httpResponseErrorInterceptor', ['$injector', '$q', '$timeout', function($injector, $q, $timeout) {
    var retryCount = 0;
    return {
        'responseError': function(response) {
            if (retryCount == 5) {
                retryCount = 0;
            }
            var now = new Date();
            retryCount = retryCount+1;
            var waitTime = 200*retryCount;

            if (response.status === 0 || response.status === 503 || response.status === 504) {
                return $timeout(function() {
                    console.error(now.toLocaleTimeString() + ": http request failed with status " + response.status + " - retryCount=" + retryCount + ", waitTime=" + waitTime + "ms");
                    var $http = $injector.get('$http');
                    return $http(response.config);
                }, waitTime);
            }
            return $q.reject(response);
        }
    };
}]);

app.config(function($routeProvider, $httpProvider) {
    $httpProvider.interceptors.push('httpResponseErrorInterceptor');
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
	.when('/gloryRankings', {
		resolve: {
			"check": function($location, $rootScope) {
				_goToLoginIfNotLoggedId($location, $rootScope);
			}
		},
		templateUrl: 'gloryRankings.html'
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


app.service('refreshPageData', ['$http', '$rootScope', function($http, $rootScope) {
	this.quickDeleteEnitity = function (id, array) {		
    	for (var i=0; i<array.length; i++) {
			if (id == array[i].id) {
				console.log("found entity to delete: " + array[i].name);
				array.splice(i, 1);
			}
		}		
	}
	
	this.quickMoveLootItem = function (id, diff, lootItems) {
		var itemMoved = false;
    	for (j = 0; j < lootItems.length; j++) {
    		var lootItem = lootItems[j];
    		if (lootItem.id == id) {
    			if (lootItem.prioritySequence+diff > 0 && lootItem.prioritySequence+diff <= lootItems.length) {
    				var otherItem = lootItems[j+diff];
    				lootItem.prioritySequence = lootItem.prioritySequence + diff;
    				if (otherItem.prioritySequence == lootItem.prioritySequence) {
    					//console.log("otherItem.prioritySequence=" +otherItem.prioritySequence + ", lootItem.prioritySequence=" +lootItem.prioritySequence)
    					otherItem.prioritySequence = otherItem.prioritySequence - diff;
        				lootItems[j+diff] = lootItem;
        				lootItems[j] = otherItem;
    				}    				
    				itemMoved = true;
    			}
    			break;   			
    		}
    	}
    	return itemMoved;
    }
	
	this.refreshAdmin = function(scope) {
		if ($rootScope.loggedInPlayer.admin) {
			scope.admin = true;
		} else {
			scope.admin = false;
		}
	}
	
	this.refreshGloryRankings = function(scope) {
		scope.gloryRankings = $rootScope.gloryRankings;
		
		if (!scope.gloryRankings) {
			scope.gloryRankings = [];
		}
		
		$http({
            method: 'GET',
            url: 'api/glory_rankings'
        }).then(function successCallback(response) {
        	console.log("Succesfully loaded glory points");
        	$rootScope.gloryRankings = response.data;
        	scope.gloryRankings = $rootScope.gloryRankings;
        }, function errorCallback(response) {
            console.error("Failed to load glory points: " + response.statusText);
        });
	}
	
    this.refreshPlayers = function (scope, player) {
    	//_addPlayer(player);
    	scope.players = $rootScope.players;
    	scope.allPlayers = $rootScope.allPlayers;
		$http({
            method: 'GET',
            url: 'api/players'
        }).then(function successCallback(response) {
        	$rootScope.allPlayers = response.data;
        	scope.allPlayers = $rootScope.allPlayers;
        	_setEnabledPlayers(scope.allPlayers, scope);
            scope.loading = false;
        }, function errorCallback(response) {
            console.error("failure: " + response.statusText);
        });
		
		function _setEnabledPlayers(allPlayers, scope) {
			var players = [];
			for (var i=0; i<allPlayers.length; i++) {
				if (allPlayers[i].enabled) {
					players[players.length] = allPlayers[i];
				}
			}
			scope.players = players;
			$rootScope.players = players;
		}
    }
    
    this.refreshFeatureToggles = function (scope) {   	
    	scope.wishListDisabled = $rootScope.wishListDisabled;
    	$http({
            method: 'GET',
            url: 'api/feature_toggle/WISH_LIST'
        }).then(function successCallback(response) {
            $rootScope.wishListDisabled = response.data;
            scope.wishListDisabled = $rootScope.wishListDisabled;
        }, function errorCallback(response) {
            console.error(response.statusText);
        });
    }

    this.refreshLootItems = function (scope, lootItem) {
        scope.lootItems = $rootScope.lootItems;
        scope.lootItemProperties = $rootScope.lootItemProperties;

        $http({
            method: 'GET',
            url: 'api/loot_items'
        }).then(function successCallback(response) {
            $rootScope.lootItems = response.data;
            scope.lootItems = $rootScope.lootItems;
            scope.loading = false;
        }, function errorCallback(response) {
            console.error(response.statusText);
        });


        $http({
            method: 'GET',
            url: 'api/loot_items/loot_item_properties'
        }).then(function successCallback(response) {
        	$rootScope.lootItemProperties = response.data;
            scope.lootItemProperties = $rootScope.lootItemProperties;
        }, function errorCallback(response) {
            console.error(response.statusText);
        });

    }

}]);

app.controller('loginCtrl', function($scope, $http, $location, $rootScope, refreshPageData) {
    $scope.loading = true;

    // Now load the data from server
    refreshPageData.refreshPlayers($scope);
    
	$scope.login = function() {
	    $scope.error = false;
	    $scope.waiting = true;
		var loginData = {
			playerId: $scope.playerId,
			password: "" + $scope.password
		};
		
		_loginPlayer(loginData);
	};	
	
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
        	$scope.waiting = false;
        }, function errorCallback(error, statusText) {
        	$rootScope.loggedInPlayer = null;
        	$scope.waiting = false;
        	$scope.error = "Login failed with errorCode: " + error.status;
        });
    }
});

app.controller("mainCtrl", function ($scope, $http, $rootScope, $location, refreshPageData) {
    if (!$rootScope.lootItems) {
    	$scope.loading = true;
    }
    refreshPageData.refreshFeatureToggles($scope);
    refreshPageData.refreshGloryRankings($scope);
	refreshPageData.refreshLootItems($scope);	
	
	$scope.logOut = function () {
		$rootScope.loggedInPlayer = "";
		$location.path("/");
	}
});

app.controller("playerManagementCtrl", function ($scope, $http, $rootScope, refreshPageData) {

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
    refreshPageData.refreshPlayers($scope);
    
    // HTTP POST/PUT methods for add/edit players
    $scope.update = function () {
    	$scope.saveDisabled = true;
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
        
        data.id = $scope.form.id;
        data.name = $scope.form.name;
        data.password = $scope.form.password;
        data.gloryPoints = $scope.form.gloryPoints;
        data.lootEnabled = $scope.form.lootEnabled;
        data.enabled = $scope.form.enabled;
        data.admin = $scope.form.admin;
        
        //_quickAddOrUpdatePlayer(data);

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

        refreshPageData.quickDeleteEnitity(player.id, $rootScope.allPlayers);
        
        $http({
            method: 'DELETE',
            url: 'api/players/' + player.id
        }).then(_successDelete, _error);
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
    
    function _quickAddOrUpdatePlayer(player) {
		if (player) {
			_insertUpdatePlayer(player, $rootScope.allPlayers);
			_insertUpdatePlayer(player, $rootScope.players);
		}
	}		
	
	function _insertUpdatePlayer(player, playerArray) {
		var foundMatch = false;
		for (var i=0; i<playerArray.length; i++) {
			if (player.id == playerArray[i].id) {
				foundMatch = true;
				playerArray[i] = player;
			}
		}
		if (!foundMatch) {
			playerArray[playerArray.length] = player;
		}
	}

    function _getPassword(playerId) {
        $http({
            method: 'GET',
            url: 'api/players/' + playerId
        }).then(function successCallback(response) {
            var player = response.data;
            $scope.form.password = player.password;
        }, function errorCallback(response) {
            console.error(response.statusText);
        });
    }

    function _success(response) {
    	_quickAddOrUpdatePlayer(response.data);
    	$scope.saveDisabled = false;
    	_clearForm();
    	refreshPageData.refreshPlayers($scope);
    }
    
    function _successDelete(response) {
    	refreshPageData.refreshPlayers($scope);    	
    }

    function _error(response) {
        alert("Your request failed! " + response.statusText);
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

app.controller("lootItemManagementCtrl", function ($scope, $http, $rootScope, refreshPageData) {

    // Initialize page with default data which is blank in this example
	//$rootScope.lootItems = [];

    $scope.form = {
        id: -1,
        rowAndNum: "",
        name: "",
        common: false,
        prioritySequence: 1
    };

    // Now load the data from server
    refreshPageData.refreshLootItems($scope);

    // HTTP POST/PUT methods for add/edit lootItems
    $scope.update = function () {
        var method = "";
        var url = "";
        var data = {};
        if ($scope.form.id == -1) {
            // Id is absent so add lootItems - POST operation
            method = "POST";
            url = 'api/loot_items';
            data.prioritySequence = $rootScope.lootItems.length+1;
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
    	
        var ids = $scope.selectedLootItemId;
        for (i = 0; i < ids.length; i++) {
        	refreshPageData.quickDeleteEnitity(ids[i], $rootScope.lootItems);
            _delete(ids[i]);
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
            console.error(response.statusText);
        });

    };
    
    $scope.save = function() {
    	var data = [];
    	$scope.saving = true;
    	$scope.error = "";
    	    	
    	$http({
            method: "PUT",
            url: "api/loot_items/update",
            data: angular.toJson($scope.lootItems),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function successCallback(response) {
        	console.log("Success in saving loot items");
        	$scope.saving=false;
        }, function errorCallback(response) {
        	console.error("Failure in saving loot items");
        	$scope.saving=false;
        	$scope.error="Failed to save loot items, try again";
        });
    }
    
    
    
    $scope.moveUp = function() {
    	var ids = $scope.selectedLootItemId;
        for (i = 0; i < ids.length; i++) {
    		//console.log("ids[" + i + "]=" + ids[i]);
        	if (ids[i].length > 0) {
        		refreshPageData.quickMoveLootItem(ids[i], -1, $scope.lootItems);
        	}
        }
    };
    
    $scope.moveDown = function() {
    	var ids = $scope.selectedLootItemId;
        for (i = 0; i < ids.length; i++) {        	
        	refreshPageData.quickMoveLootItem(ids[i], 1, $scope.lootItems);
        }
    };

    /* Private Methods */
    function _delete(id) {
        $http({
            method: 'DELETE',
            url: 'api/loot_items/' + id
        }).then(_success, _error);
    }
    
    function _update(method, url, data) {
    	console.log("url=" + url);
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
    	refreshPageData.refreshLootItems($scope);
        //_refreshPageData();
        _clearForm();
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

app.controller("gloryRankingsCtrl", function ($scope, $http, $rootScope, refreshPageData) {
	refreshPageData.refreshGloryRankings($scope);
	refreshPageData.refreshAdmin($scope);
	
	$scope.saveGloryRankings = function() {
		$http({    		
            method: "PUT",
            url: "api/glory_rankings",
            data: angular.toJson($scope.gloryRankings),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function successCallback(response) {
        	console.log("Success in updated glory rankings!");
        	$rootScope.gloryRankings = $scope.gloryRankings;
        }, function errorCallback(response) {
        	console.error("Failed updated glory rankings!");
        });
	}
});


app.controller("wishListCtrl", function ($scope, $http, $rootScope, refreshPageData) {

    // Initialize page with default data which is blank in this example
    $scope.wishItems = [];
    
    refreshPageData.refreshFeatureToggles($scope);
    refreshPageData.refreshAdmin($scope);
    
    
    // Now load the data from server
    if ($rootScope.loggedInPlayer.lootEnabled) {
    	if (!$scope.wishListDisabled) {
    		_refreshPageData();
    	} else {
    		console.log("WishList disabled!");
    	}
    } else {    	
    	$scope.lootDisabled = true;
    }
    
    $scope.save = function() {
    	var data = [];
    	$scope.saving=true;
    	$scope.error="";
    	for (var i=0; i<$scope.wishItems.length; i++) {
    		$scope.wishItems[i].disabled = false;
    		data[data.length] = $scope.wishItems[i];
    	}
    	for (var i=0; i<$scope.disabledLootItems.length; i++) {
    		$scope.disabledLootItems[i].disabled = true;
    		data[data.length] = $scope.disabledLootItems[i];
    	}
    	
    	$http({
            method: "PUT",
            url: "api/loot_items/update",
            data: angular.toJson(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function successCallback(response) {
        	console.log("Success in saving wish list");
        	$scope.saving=false;
        }, function errorCallback(response) {
        	console.error("Failure in saving wish list");
        	$scope.saving=false;
        	$scope.error="Failed to save wish list, try again";
        });
    }

    $scope.disableChanged = function() {
    	console.log("disableChanged called!");
    	$http({
            method: "PUT",
            url: "api/feature_toggle/WISH_LIST",
            data: angular.toJson($scope.wishListDisabled),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function successCallback(response) {
        	console.log("Success in updating WISH_LIST feature toggle");
        }, function errorCallback(response) {
        	console.error("Failure in updating WISH_LIST feature toggle");
        });
    }
    
    $scope.moveUp = function() {
    	var ids = $scope.selectedEnabledLootItemId;
        for (i = 0; i < ids.length; i++) {
    		//console.log("ids[" + i + "]=" + ids[i]);
        	if (ids[i].length > 0) {
        		refreshPageData.quickMoveLootItem(ids[i], -1, $scope.wishItems);
        	}
        }
    };
    
    $scope.moveDown = function() {
    	var ids = $scope.selectedEnabledLootItemId;
        for (i = 0; i < ids.length; i++) {        	
        	refreshPageData.quickMoveLootItem(ids[i], 1, $scope.wishItems);
        }
    };
    
    $scope.toggle = function(disabled) {
    	var ids = $scope.selectedDisabledLootItemId;
    	if (disabled) {
    		ids = $scope.selectedEnabledLootItemId;
    	}
        for (i = 0; i < ids.length; i++) {        	
        	_quickToggle(ids[i], disabled);
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
    	if ($rootScope.wishItems) {
    		$scope.disabledLootItems = $rootScope.disabledLootItems;
    		$scope.wishItems = $rootScope.wishItems;
    	}
    	
    	if ($scope.wishItems.length == 0) {
        	$scope.loading = true;
        }
    	
    	$http({
            method: 'GET',
            url: 'api/loot_items/for_player/' + $rootScope.loggedInPlayer.id
        }).then(function successCallback(response) {
        	_splitLootItemData(response.data);
        	$scope.loading = false;
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
        $scope.refresh = false;
    }
    
    function _quickToggle(id, disabled) {
    	if (disabled) {
    		_moveEntityFromArray1ToArray2(id, $scope.wishItems, $scope.disabledLootItems);    	
    	} else {
    		$scope.refresh = true;
    		_moveEntityFromArray1ToArray2(id, $scope.disabledLootItems, $scope.wishItems);
    	} 
    }
    
    function _moveEntityFromArray1ToArray2(id, array1, array2) {
    	for (var j=0; j<array1.length; j++) {
			if (array1[j].id == id) {
				array2[array2.length] = array1[j];
				array1.splice(j, 1);
				break;
			}
		}
    }
    
    function _splitLootItemData(lootItems) {
    	$scope.wishItems = [];
    	$scope.disabledLootItems = [];
    	var disabledLootItems
    	for (var i = 0; i < lootItems.length; i++) {
			if (lootItems[i].disabled) {
				$scope.disabledLootItems[$scope.disabledLootItems.length] = lootItems[i];
			} else {
				$scope.wishItems[$scope.wishItems.length] = lootItems[i];
			}
		}
    	$rootScope.disabledLootItems = $scope.disabledLootItems;
    	$rootScope.wishItems = $scope.wishItems;
    }
    
    function _update(method, url, data, refresh) {
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
        console.log("Update was a success");
        if ($scope.refresh) {
        	_refreshPageData();
        }
    }

    function _error(response) {
        console.error("Update failed: " + response.statusText);
    }
});

app.controller("resultsCtrl", function ($scope, $http, $rootScope, refreshPageData) {

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
		refreshPageData.refreshAdmin($scope);
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