(function(app) {
  'use strict';

  var registrationDoctorCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $timeout) {
    var id = $stateParams.id;
    $http.get('/main/registration/doctor', {params: {id: id}}).success(function(data) {
      $scope.doctor = data;
    });

    $scope.showAgreement = function() {
      $http.get('/main/registration/doctor/agreement').success(function(data) {
        var myPopup = $ionicPopup.show({
          template: '<div style="padding: 2px;font-size:15px">'+data+'</div>',
          title: '挂号须知',
          buttons: [
            {
              text: '确定',
              type: 'positive',
              onTap: function(e) {
                e.preventDefault();
                myPopup.close();
              }
            }
          ]
        });
        $timeout(function() {
          angular.element(document.querySelector('.popup')).css('width','80%');
          angular.element(document.querySelector('.popup-title')).css('font-size','18px');
        });
      });
    };
    $scope.showAgreement();

    $scope.registrationPay = function() {
      $state.go('mainPaySelect');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainRegistrationDoctor', {
      url: '/main/registration/registrationDoctor/:id',
      templateUrl: 'modules/main/registration/registrationDoctor.html',
      controller: registrationDoctorCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
