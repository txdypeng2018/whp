(function(app) {
  'use strict';

  var waterfallViewCtrl = function($scope, $http) {
    var getDate = function(param, isInit) {
      $http.get('/playground/waterfall', {params: param}).success(function(data) {
        $scope.waterfall.hasMore = data.hasMore;
        if (isInit) {
          $scope.datas = data.data;
        }
        else {
          for (var i = 0 ; i < data.data.length ; i++) {
            $scope.datas.push(data.data[i]);
          }
        }
      }).finally(function() {
        $scope.$broadcast('peaWaterfall.loadFinished');
      });
    };

    $scope.waterfall = {
      pageNo: 1,
      hasMore: true,
      init: function () {
        $scope.waterfall.pageNo = 1;
        $scope.waterfall.hasMore = true;
        getDate({pageNo: $scope.waterfall.pageNo}, true);
      },
      loadMore: function () {
        $scope.waterfall.pageNo++;
        getDate({pageNo: $scope.waterfall.pageNo}, false);
      }
    };

    $scope.waterfall.init();
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundWaterfallView', {
      url: '^/playground/waterfall/waterfallView',
      templateUrl: 'modules/playground/waterfall/waterfallView.html',
      controller: waterfallViewCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
