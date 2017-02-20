'use strict';

var dateSelectCtrl = function($scope, $ionicScrollDelegate, $element) {
  //初期化
  $scope.leftIconIsShow = false;
  $scope.rightIconIsShow = false;
  $scope.$watch('dateSelectParam.selectDays', function (newData) {
    if (newData.length > 7) {
      $scope.rightIconIsShow = true;
    }
    $scope.month = $scope.dateSelectParam.selectDays[0].month;
    $scope.fixMonth = true;
  });

  //设置控件宽度
  $scope.width = {};
  var surplusWidth = (document.body.clientWidth-34)%7;
  var scrollWidth = document.body.clientWidth-34-surplusWidth;
  $scope.width.scroll = (scrollWidth+1)+'px';
  var tdWidth = scrollWidth/7;
  $scope.width.td = tdWidth+'px';
  surplusWidth = surplusWidth - 1;
  if (surplusWidth < 0) {
    $scope.width.left = '16px';
    $scope.width.right = '17px';
  }
  else if (surplusWidth > 0 && surplusWidth%2 === 0) {
    $scope.width.left = (17+surplusWidth/2)+'px';
    $scope.width.right = (17+surplusWidth/2)+'px';
  }
  else if (surplusWidth > 0) {
    $scope.width.left = (17+(surplusWidth-1)/2)+'px';
    $scope.width.right = (17+(surplusWidth-1)/2+1)+'px';
  }
  else {
    $scope.width.left = '17px';
    $scope.width.right = '17px';
  }

  var trPosition = [];
  var changeId = 0;
  for(var i = 1, j = 0; i < $scope.dateSelectParam.selectDays.length; i++){
    if($scope.dateSelectParam.selectDays[i].month !== $scope.dateSelectParam.selectDays[i-1].month){
      $scope.dateSelectParam.selectDays[i].monthFlag = true;
      changeId = i;
      trPosition[j] = tdWidth * (i-1);
      j++;
    }
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
    var monthDiv1 = $element[0].querySelector('.isj-date-select-month');
    $scope.$watch('month',function(newData, oldData){
      if(newData !== oldData){
        $scope.fixMonth = true;
      }
      if(newData < oldData || (newData === 12 && oldData === 1)){
        monthDiv1.style.opacity = 1;
      }
    });

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
    var monthDiv = $element[0].querySelectorAll('.isj-date-select-month');
    for(var i = 0; i < monthDiv.length; i++){
      if(!angular.isUndefined(trPosition[i])){
        if(delegate.getScrollPosition().left < trPosition[i]){
          $scope.dateSelectParam.selectDays[changeId-1].monthFlag = false;
          $scope.fixMonth = true;
        }
        else if(delegate.getScrollPosition().left < (trPosition[i] + tdWidth) && delegate.getScrollPosition().left > trPosition[i]){
          $scope.dateSelectParam.selectDays[changeId-1].monthFlag = true;
          $scope.fixMonth = false;
        }
        else if(delegate.getScrollPosition().left >= (trPosition[i] +tdWidth)){
          $scope.dateSelectParam.selectDays[changeId-1].monthFlag = false;
          $scope.fixMonth = true;
        }
      }
    }
  };
  var scrollMonth = function() {
    $scope.month = $scope.dateSelectParam.selectDays[parseInt((delegate.getScrollPosition().left+7)/tdWidth)].month;
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
