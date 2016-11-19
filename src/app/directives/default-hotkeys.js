'use strict';

(function(app) {

  app.directive('defaultHotkeys', function(hotkeys) {
    return {
      link: function($scope) {
        var hks = hotkeys.bindTo($scope);

        if ($scope.clickEnter) {
          hks.add({
            combo: 'enter',
            allowIn: ['INPUT'],
            callback: $scope.clickEnter});
        }
        if ($scope.clickESC) {
          hks.add({
            combo: 'esc',
            allowIn: ['INPUT'],
            callback: $scope.clickESC});
        }

        if ($scope.focusQS) {
          hks.add('s', '模糊搜索', $scope.focusQS);
        }
        if ($scope.createItem) {
          hks.add('f a', '创建', $scope.createItem);
        }
        if ($scope.editItem) {
          hks.add('f e', '编辑', $scope.editItem);
        }
        if ($scope.delItem) {
          hks.add('f d', '删除', $scope.delItem);
        }

        if ($scope.hotkeys) {
          angular.forEach($scope.hotkeys, function(obj) {
            hks.add({
              combo: obj.combo,
              description: obj.description ? obj.description : '',
              callback: obj.callback
            });
          });
        }
      }
    };
  });

})(angular.module('pea'));
