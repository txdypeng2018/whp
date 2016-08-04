(function(app) {
  'use strict';

  var deptListCtrl = function($scope, $http, $window, $state, $stateParams) {
    var type = $stateParams.type;

    var getDept = function(param) {
      $http.get('/main/registration/district/dept', {params: param}).success(function(data) {
        $scope.depts = data;
        $scope.deptRights = data[0].depts;
      });
    };

    $scope.districtClk = function(event, id) {
      var districtDocuments = document.querySelectorAll('.district-button');
      for (var i = 0 ; i < districtDocuments.length ; i++) {
        if (!angular.element(districtDocuments[i]).hasClass('button-outline')) {
          angular.element(districtDocuments[i]).addClass('button-outline');
        }
      }
      angular.element(event.currentTarget).removeClass('button-outline');
      getDept({'id': id});
    };

    $scope.deptLeftClk = function(event, id) {
      angular.element(document.querySelectorAll('.dept-div')).removeClass('positive');
      angular.element(document.querySelectorAll('.dept-div')).removeClass('dept-div-left');
      angular.element(event.currentTarget).addClass('positive');
      angular.element(event.currentTarget).addClass('dept-div-left');
      for (var i = 0 ; i < $scope.depts.length ; i++) {
        if ($scope.depts[i].id === id) {
          $scope.deptRights = $scope.depts[i].depts;
          break;
        }
      }
    };

    $scope.deptRightClk = function(id) {
      if (type === '1') {
        $state.go('mainRegistrationDoctorList', {id: id});
      }
      else if (type === '2') {
        $state.go('mainRegistrationDateList', {id: id});
      }
    };

    $http.get('/main/registration/district').success(function(data) {
      $scope.districts = data;
      if ($scope.districts.length > 0) {
        getDept({'id': $scope.districts[0].id});
      }
    });

    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('dept_select').style.height =
        (document.getElementById('dept_list_content').offsetHeight - 50) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('dept_select').style.height =
        (document.getElementById('dept_list_content').offsetHeight - 50) + 'px';
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainDeptList', {
      url: '/main/deptList/:type',
      templateUrl: 'modules/main/registration/deptList.html',
      controller: deptListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
