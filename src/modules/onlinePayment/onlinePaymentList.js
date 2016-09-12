(function(app) {
  'use strict';

  var onlinePaymentListCtrl = function($scope, $http, $state, $stateParams, $cordovaToast) {
    //取得缴费列表
    var getPayments = function(param) {
      param.memberId = $stateParams.memberId;
      $http.get('/recipes', {params: param}).success(function(data) {
        $scope.recipes = data;
        $scope.amount = '0.00';
        $scope.recipeNums = [];
        for (var i = 0 ; i < $scope.recipes.length ; i++) {
          var outpatient = $scope.recipes[i];
          outpatient.canCheck = false;
          for (var j = 0 ; j < outpatient.recipes.length ; j++) {
            if (outpatient.recipes[j].statusCode === '0') {
              outpatient.canCheck = true;
              outpatient.isCheck = false;
            }
          }
          if (outpatient.canCheck) {
            outpatient.isCheck = false;
          }
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //初始化支付状态查询下拉菜单
    $scope.searchStr = {
      searchStatus: ''
    };
    $scope.searchStatusTypes = [
      {code:'', name :'全部'},
      {code:'0', name :'未支付'},
      {code:'1', name :'已支付'}
    ];

    //查询缴费列表事件
    $scope.searchPay = function() {
      getPayments($scope.searchStr);
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      getPayments($scope.searchStr);
    });

    //返回上页
    $scope.goBack = function() {
      $state.go('tab.main');
    };

    //取得就诊人
    $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
      $scope.patient = data;
    });
    //选择家庭成员
    $scope.selectMember = function() {
      $state.go('familyMemberSelect', {skipId: 'onlinePaymentList', memberId: $scope.patient.id});
    };

    //选择处方事件
    var setRecipeNums = function(recipeNum, isPush) {
      var idx = $scope.recipeNums.indexOf(recipeNum);
      if (isPush) {
        if (idx < 0) {
          $scope.recipeNums.push(recipeNum);
        }
      }
      else {
        if (idx >= 0) {
          $scope.recipeNums.splice(idx, 1);
        }
      }
    };

    var setAmount = function(amount, isAdd) {
      if (isAdd) {
        $scope.amount = (Number($scope.amount)+Number(amount)+1).toFixed(2);
        $scope.amount = Number($scope.amount-1).toFixed(2);
      }
      else {
        $scope.amount = (Number($scope.amount)-Number(amount)+1).toFixed(2);
        $scope.amount = Number($scope.amount-1).toFixed(2);
      }
    };

    $scope.checkClk = function(type, outpatientNum, recipeNum) {
      var outpatient = {};
      if (type === '0') {
        for (var i = 0 ; i < $scope.recipes.length ; i++) {
          outpatient = $scope.recipes[i];
          if (outpatient.outpatientNum === outpatientNum) {
            for (var j = 0 ; j < outpatient.recipes.length ; j++) {
              if (outpatient.recipes[j].statusCode === '0') {
                if (outpatient.isCheck !== outpatient.recipes[j].isCheck) {
                  setAmount(outpatient.recipes[j].total, outpatient.isCheck);
                }
                outpatient.recipes[j].isCheck = outpatient.isCheck;
                setRecipeNums(outpatient.recipes[j].recipeNum, outpatient.isCheck);
              }
            }
          }
        }
      }
      else {
        for (var m = 0 ; m < $scope.recipes.length ; m++) {
          outpatient = $scope.recipes[m];
          if (outpatient.outpatientNum === outpatientNum) {
            var isCheck = true;
            for (var n = 0 ; n < outpatient.recipes.length ; n++) {
              if (outpatient.recipes[n].statusCode === '0') {
                if (outpatient.recipes[n].recipeNum === recipeNum) {
                  setRecipeNums(recipeNum, outpatient.recipes[n].isCheck);
                  setAmount(outpatient.recipes[n].total, outpatient.recipes[n].isCheck);
                }
                if (!outpatient.recipes[n].isCheck) {
                  isCheck = false;
                }

              }
            }
            outpatient.isCheck = isCheck;
          }
        }
      }
    };

    //支付
    $scope.payClk = function() {
      if ($scope.recipeNums.length > 0) {
        $scope.isSubmit = true;
        $http.post('/orders', {recipeNums: $scope.recipeNums}).success(function(data) {
          $state.go('paymentSelect', {orderNum: data.orderNum});
        }).error(function(data){
          $scope.isSubmit = false;
          $cordovaToast.showShortBottom(data);
        });
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('onlinePaymentList', {
      url: '/onlinePayment/onlinePaymentList/:memberId',
      templateUrl: 'modules/onlinePayment/onlinePaymentList.html',
      controller: onlinePaymentListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
