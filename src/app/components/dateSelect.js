'use strict';

var dateSelectCtrl = function($scope, $ionicScrollDelegate) {
  //初期化
  $scope.leftIconIsShow = false;
  $scope.rightIconIsShow = false;
  $scope.$watch('dateSelectParam.selectDays', function (newData) {
    if (newData.length > 7) {
      $scope.rightIconIsShow = true;
    }
    $scope.month = $scope.dateSelectParam.selectDays[0].month;
  });

  //设置控件宽度
  $scope.width = {};
  var surplusWidth = (document.body.clientWidth-40)%7;
  var scrollWidth = document.body.clientWidth-40-surplusWidth;
  $scope.width.scroll = (scrollWidth+1)+'px';
  var tdWidth = scrollWidth/7;
  $scope.width.td = tdWidth+'px';
  surplusWidth = surplusWidth - 1;
  if (surplusWidth < 0) {
    $scope.width.left = '19px';
    $scope.width.right = '20px';
  }
  else if (surplusWidth > 0 && surplusWidth%2 === 0) {
    $scope.width.left = (20+surplusWidth/2)+'px';
    $scope.width.right = (20+surplusWidth/2)+'px';
  }
  else if (surplusWidth > 0) {
    $scope.width.left = (20+(surplusWidth-1)/2)+'px';
    $scope.width.right = (20+(surplusWidth-1)/2+1)+'px';
  }
  else {
    $scope.width.left = '20px';
    $scope.width.right = '20px';
  }

  //滚动事件
  var delegate = $ionicScrollDelegate.$getByHandle('dateScroll');
  $scope.leftSlide = function() {
    if ($scope.leftIconIsShow) {
      delegate.scrollBy(-(tdWidth*7), 0, true);
    }
  };
  $scope.rightSlide = function() {
    if ($scope.rightIconIsShow) {
      delegate.scrollBy(tdWidth*7, 0, true);
    }
  };
  $scope.scroll = function () {
    if (delegate.getScrollPosition().left > 1) {
      $scope.leftIconIsShow = true;
    }
    else {
      $scope.leftIconIsShow = false;
    }
    var maxWidth = tdWidth*($scope.dateSelectParam.selectDays.length-7)-2;
    if(delegate.getScrollPosition().left >= maxWidth){
      $scope.rightIconIsShow = false;
    }else{
      $scope.rightIconIsShow = true;
    }
    $scope.$apply($scope.leftIconIsShow);
    $scope.$apply($scope.rightIconIsShow);
    scrollMonth();
  };
  var scrollMonth = function() {
    $scope.month = $scope.dateSelectParam.selectDays[parseInt((delegate.getScrollPosition().left)/tdWidth)].month;
  };

  //日期选择事件
  $scope.dateClk = function(date) {
    if ($scope.dateSelectParam.daySelected !== date) {
      $scope.dateSelectParam.daySelected = date;
      $scope.callback();
    }
  };
};

angular.module('isj').directive('isjDateSelect', function() {
  return {
    restrict: 'E',
    replace: false,
    scope:{
      dateSelectParam: '=dateSelectParam',
      callback: '&'
    },
    templateUrl: 'app/components/date-select.html',
    controller: dateSelectCtrl
  };
});
