'use strict';

(function(app) {

  app.service('components', function($mdToast, $mdDialog, $log) {

    /**
     * 提示框组件
     * @param msg 提示消息内容
     */
    this.showToast = function(msg) {
      $mdToast.show($mdToast.simple().textContent(msg).position('top right').hideDelay(3000));
    };

    /**
     * 确认框组件
     * 确认和取消操作的回调函数可以通过返回的 promise 设置
     *
     * 调用示例:
     *
     * confirm(ev, '是否确认删除?').then(function() {
     *   console.log('success');
     * }, function(err) {
     *   console.log('error');
     * });
     *
     * @param event       点击事件
     * @param title       确认框标题,默认为"确认操作?"
     * @param content     确认框内容,默认为"确认操作请点击确定按钮"
     * @param ok          确认框确定按钮文字,默认为"确定"
     * @param cancel      确认框取消按钮文字,默认为"取消"
     * @returns {promise}
     */
    this.confirm = function(event, title, content, ok, cancel) {
      var c = $mdDialog.confirm()
        .title(title || '确认操作?')
        .textContent(content || '确认操作请点击' + (ok || '确定') + '按钮')
        .ariaLabel('确认提示框')
        .targetEvent(event)
        .ok(ok || '确定')
        .cancel(cancel || '取消');
      return $mdDialog.show(c);
    };

    this.errCallback = function(err) {
      $log.debug(err);
    };

  });
})(angular.module('pea'));
