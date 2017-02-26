'use strict';

(function(){
  angular.module('pea.waterfall', [])
    .directive('peaWaterfall', function($window, $rootScope, $timeout){
      return {
        restrict: 'E',
        scope: {
          miniWidth: '@',
          cols : '@'
        },
        controller: function() {},
        link: function(scope, element){
          //计算列个数
          var getCols = function (wrapWidth, miniWidth, cols) {
            var listCols = cols || 6;
            if (!angular.isUndefined(miniWidth)) {
              listCols = parseInt(wrapWidth / miniWidth);
              if (listCols === 0) {
                listCols = 1;
              }
            }
            return listCols;
          };

          //取得最小高度列位置
          var getMiniHeightIndex = function(arr){
            var height = arr[0];
            var index = 0;
            for (var i = 0 ; i < arr.length ; i++) {
              if (arr[i] < height) {
                height = arr[i];
                index = i;
              }
            }
            return index;
          };

          //取得滚动元素
          var getScrollElement = function (element) {
            var scrollElement;
            if (!angular.isUndefined(element) && !angular.isUndefined(element[0])) {
              if (element[0].scrollHeight > element[0].clientHeight) {
                scrollElement = element;
              }
              else {
                scrollElement = getScrollElement(element.parent());
              }
            }
            return scrollElement;
          };

          //滚动监控事件
          var scrollMonitor = function() {
            var index = getMiniHeightIndex(scope.everyColHeight);
            var shortHeight = scope.everyColHeight[index];
            var clientHeight = scope.scrollElement[0].clientHeight;
            var scrollTop = scope.scrollElement[0].scrollTop;
            if (scrollTop + clientHeight >= shortHeight) {
              scope.$emit('peaWaterfall.loadMore');
            }
          };

          //设置瀑布流位置
          var waterfall = function(miniWidth, cols) {
            scope.everyColHeight = [];

            //取得列数和列宽度
            var wrapWidth = element[0].offsetWidth;
            scope.listCols = getCols(wrapWidth, miniWidth, cols);
            var colWidth = 100/scope.listCols;
            var everyChild = element.find('pea-waterfall-repeat');
            for (var m = 0; m < everyChild.length; m++){
              everyChild[m].style.width = colWidth + '%';
            }

            $timeout(function () {
              //设置第一行元素位置
              for (var i = 0; i < scope.listCols; i++){
                everyChild[i].style.top = 0;
                everyChild[i].style.left = i * colWidth + '%';
                var h = parseInt(everyChild[i].offsetHeight);
                scope.everyColHeight.push(h);
              }

              //根据各列告诉将各元素放置在高度最小元素下面
              for (var k = scope.listCols; k < everyChild.length; k++){
                var index = getMiniHeightIndex(scope.everyColHeight);
                everyChild[k].style.top = scope.everyColHeight[index] +'px';
                everyChild[k].style.left = colWidth * index +'%';
                scope.everyColHeight[index] = scope.everyColHeight[index] + parseInt(everyChild[k].offsetHeight);
              }

              //设置瀑布流整体高度
              var sorted = scope.everyColHeight.sort(function(a, b){
                return a - b;
              });
              var max = sorted[sorted.length - 1];
              element[0].style.height = max + 50 + 'px';

              //数据不满一屏时继续加载
              if (sorted[0] <= document.body.clientHeight) {
                scope.$emit('peaWaterfall.loadMore');
              }

              //设置滚动加载事件
              if (angular.isUndefined(scope.scrollElement)) {
                scope.scrollElement = getScrollElement(element.parent());
                if (!angular.isUndefined(scope.scrollElement)) {
                  scope.scrollElement[0].onscroll = scrollMonitor;
                }
              }
              var searchCard = element.find('pea-waterfall-infinite')[0].previousSibling.previousSibling;
              var peaWidth = everyChild[0].parentNode;
              searchCard.style.width = peaWidth.offsetWidth - 16 + 'px';
            });
          };

          scope.$emit('peaWaterfall.dataLoading');
          element[0].style.height = 50+'px';

          //子元素准备完成后设置位置
          var promise = null;
          $rootScope.$on('peaWaterfall.repeatFinished',function(){
            $timeout.cancel(promise);
            scope.elementReady = true;
            waterfall(scope.miniWidth, scope.cols);
          });

          //浏览器窗口大小改变时重新设置位置
          $window.onresize = function(){
            if (scope.elementReady) {
              $timeout.cancel(promise);
              promise = $timeout(function(){
                waterfall(scope.miniWidth, scope.cols);
              }, 500);
            }
          };
        }
      };
    })
    .directive('peaWaterfallRepeat', function($interval){
      return {
        restrict: 'E',
        require: '^peaWaterfall',
        link: function(scope, element) {
          if (scope.$last === true) {
            angular.element(element).ready(function() {
              var imgs = element.parent().find('img');
              if (angular.isUndefined(imgs) || imgs.length === 0) {
                scope.$emit('peaWaterfall.repeatFinished');
              }
              else {
                //有图片时需要等待图片加载完成知道图片的高度
                var imgLoadIndex = 0;
                var imgLoadAdd = function () {
                  imgLoadIndex++;
                };
                for (var i = 0 ; i < imgs.length ; i++) {
                  var oImage = new Image();
                  oImage.src = imgs[i].src;
                  oImage.onload = imgLoadAdd;
                  oImage.onerror = imgLoadAdd;
                }
                var promise = $interval(function () {
                  if (imgLoadIndex === imgs.length) {
                    scope.$emit('peaWaterfall.repeatFinished');
                    $interval.cancel(promise);
                  }
                }, 500);
              }
            });
          }
        }
      };
    })
    .directive('peaWaterfallInfinite', function($rootScope){
      return {
        restrict: 'E',
        replace: false,
        scope: {
          loadMore: '&',
          hasMore : '='
        },
        require: '^peaWaterfall',
        template: '<div class="prompt"><div ng-if="!hasMore" class="no-more">没有更多了</div><div ng-if="dataLoading"><md-progress-circular md-mode="indeterminate"></md-progress-circular></div></div>',
        link: function(scope) {
          scope.dataLoading = false;
          $rootScope.$on('peaWaterfall.loadMore', function(){
            if (scope.hasMore && !scope.dataLoading) {
              scope.dataLoading = true;
              scope.loadMore();
            }
          });
          $rootScope.$on('peaWaterfall.dataLoading', function(){
            scope.dataLoading = true;
          });
          scope.$on('peaWaterfall.loadFinished', function(){
            scope.dataLoading = false;
          });
        }
      };
    });
})();
