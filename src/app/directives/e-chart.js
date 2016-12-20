/**
 * Created by Administrator on 2016/12/8.
 */
'use strict';
(function(app) {

    app.directive('eChart',function() {


        return {
            restrict: 'A',
            link: function($scope, element, attrs) {
                var myChart = echarts.init(element[0]);
                $scope.$watch(attrs.eData, function () {
                    var option = $scope.$eval(attrs.eData);
                    if (angular.isObject(option)) {
                        setInterval(function () {
                            myChart.setOption(option);
                        }, 1500);
                    }
                }, true);
                $scope.getDom = function () {
                    return {
                        'height': element[0].offsetHeight,
                        'width': element[0].offsetWidth
                    };
                };
                $scope.$watch($scope.getDom, function () {
                    // resize echarts图表
                    myChart.resize();
                }, true);
            }
        };
    });

})(angular.module('pea'));