(function(app) {
  'use strict';

  var onlinePaymentListCtrl = function($scope, $http, $state, $stateParams, toastService) {
    //取得缴费列表
    var getPayments = function(param) {
      $scope.recipes = null;
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
        $scope.recipes = [];
        toastService.show(data);
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
      $scope.patient = {};
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        toastService.show(data);
      });

      $scope.isSubmit = false;
      getPayments($scope.searchStr);
    });

    //返回上页
    $scope.goBack = function() {
      $state.go('tab.main');
    };

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

    $scope.checkClk = function(outpatientNum) {
      var outpatient = {};
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
        else {
          if (outpatient.isCheck) {
            outpatient.isCheck = false;
            for (var k = 0 ; k < outpatient.recipes.length ; k++) {
              if (outpatient.recipes[k].statusCode === '0') {
                setAmount(outpatient.recipes[k].total, outpatient.isCheck);
                outpatient.recipes[k].isCheck = false;
                setRecipeNums(outpatient.recipes[k].recipeNum, outpatient.isCheck);
              }
            }
          }
        }
      }
    };

    //支付
    $scope.payClk = function() {
      if ($scope.recipeNums.length > 0) {
        var param = {
          memberId: $scope.patient.id,
          outpatients: []
        };
        for (var i = 0 ; i < $scope.recipes.length ; i++) {
          var recipe = $scope.recipes[i];
          var recipeNums = [];
          for (var j = 0 ; j < recipe.recipes.length ; j++) {
            if (recipe.recipes[j].isCheck) {
              recipeNums.push(recipe.recipes[j].recipeNum);
            }
          }
          if (recipeNums.length > 0) {
            param.outpatients.push({outpatientNum: recipe.outpatientNum, recipeNums: recipeNums});
          }
        }
        $scope.isSubmit = true;
        $http.post('/orders', param).success(function(data) {
          $state.go('paymentSelect', {orderNum: data.orderNum, memberId: $stateParams.memberId});
        }).error(function(data){
          $scope.isSubmit = false;
          toastService.show(data);
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
