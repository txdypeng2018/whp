(function(app) {
  'use strict';

  var tomcatLogsCtrl = function($scope, $http, $sce, $mdToast) {
    var lvIn = {
      'ERROR': '["ERROR"]',
      'WARN': '["WARN", "ERROR"]',
      'INFO': '["INFO", "WARN", "ERROR"]',
      'DEBUG': '["DEBUG", "INFO", "WARN", "ERROR"]'
    };
    var intervalTime = 2000;
    var search = '';
    var startDate = '';
    var endDate = '';
    $scope.search = '';
    $scope.date = {
      start: '',
      end: ''
    };
    $scope.logs = [];

    var now = new Date();
    var tmStart = new Date(now.getTime() - intervalTime);
    var tmEnd = now;

    $scope.logLevelItems = [
      {value: 'DEBUG', label: 'DEBUG'},
      {value: 'INFO', label: 'INFO'},
      {value: 'WARN', label: 'WARN'},
      {value: 'ERROR', label: 'ERROR'}
    ];
    $scope.logLevel = 'ERROR';
    $scope.autoUpdate = true;
    $scope.overflowLock = false;
    $scope.search = '';

    //将时间转换成mongo的objectId
    var tranObjectId = function(date) {
      var time = parseInt(date.getTime()/1000);
      time = time.toString(16);
      time = time + new Array(17).join('0');
      return time;
    };

    //取得tomcat日志
    var getLogs = function () {
      var query = '{';
      query += 'lv: {$in: '+lvIn[$scope.logLevel]+'}, ';
      if (startDate !== '' && endDate !== '') {
        query += 'tm: {$gte: "'+startDate+'.000", $lte: "'+endDate+'.000"}';
      }
      else {
        query += '_id: {$gte: ObjectId("'+tranObjectId(tmStart)+'"), $lte: ObjectId("'+tranObjectId(tmEnd)+'")}';
      }
      if (search.length > 0) {
        query += ', $or: [{clz: /'+search+'/}, {msg: /'+search+'/}]';
      }
      query += '}';

      $http.get('/msc/LOGS', {params: {query: query, sort: '{tm: 1}'}}).success(function(data) {
        for(var n = 0; n < data.data.length; n++){
          var log = JSON.parse(data.data[n]);
          if ($scope.logs.length > 1 && $scope.logs[$scope.logs.length-1].clz === log.clz && $scope.logs[$scope.logs.length-1].msg === log.msg) {
            if ($scope.logs[$scope.logs.length-1].times < 9) {
              $scope.logs[$scope.logs.length-1].times++;
            }
          }
          else {
            log.tmAfter = log.tm.substring(0, 19);
            if (log.clz.length > 45) {
              log.clzAfter = log.clz.substring(log.clz.length - 45, log.clz.length);
            }
            log.msgAfter = log.msg.replace(new RegExp(/\r\n/g), '<br>');
            log.msgAfter = log.msgAfter.replace(new RegExp(/\t/g), '&nbsp;&nbsp;&nbsp;&nbsp;');
            log.msgAfter = '[' + log.tm + ']&nbsp;' + log.clzAfter + '&nbsp;-&nbsp;' + log.msgAfter;
            log.msgAfter = $sce.trustAsHtml(log.msgAfter);
            log.times = 1;
            $scope.logs.push(log);
            if ($scope.logs.length > 3000 && !$scope.overflowLock) {
              $scope.logs.shift();
            }
          }
        }
      });
    };

    //日志查询
    var searchLog = function () {
      search = $scope.search;
      if (($scope.date.start === '' && $scope.date.end === '') || ($scope.date.start !== '' && $scope.date.end !== '')) {
        startDate = $scope.date.start;
        endDate = $scope.date.end;
      }
      else {
        startDate = '';
        endDate = '';
      }
      $scope.logs = [];
      if (!$scope.autoUpdate || (startDate !== '' && endDate !== '')) {
        getLogs();
      }
    };

    //按日期查询
    $scope.dateSearch = function() {
      if (($scope.date.start === '' && $scope.date.end !== '') || ($scope.date.start !== '' && $scope.date.end === '')) {
        $mdToast.show($mdToast.simple().textContent('必须输入时间段'));
      }
      else {
        searchLog();
      }
    };

    //清空日志查询条件
    $scope.clearSearchClk = function() {
      $scope.search = '';
      if (search !== '') {
        searchLog();
      }
    };

    //重置日期查询
    $scope.dateClear = function() {
      $scope.date.start = '';
      $scope.date.end = '';
      if (startDate !== '' && endDate !== '') {
        searchLog();
      }
    };

    //自动更新切换
    $scope.autoUpdateClk = function() {
      $scope.autoUpdate = !$scope.autoUpdate;
    };

    //锁定滚动条
    $scope.overflowLockClk = function() {
      $scope.overflowLock = !$scope.overflowLock;
    };

    //清空log
    $scope.clearLogClk = function() {
      $scope.logs = [];
    };

    //日志过滤事件
    $scope.searchKeyup = function(e) {
      var keyCode = window.event?e.keyCode:e.which;
      if(keyCode === 13){
        searchLog();
      }
    };

    //ng-repeat完成将滚动条设置到底部
    $scope.repeatFinish = function () {
      if (!$scope.overflowLock) {
        var logContent = document.getElementById('log_content');
        logContent.scrollTop = logContent.scrollHeight;
      }
    };

    //定时取日志
    getLogs();
    setInterval(function(){
      tmStart = tmEnd;
      tmEnd = new Date(tmStart.getTime() + intervalTime);
      if ($scope.autoUpdate && startDate === '' && endDate === '') {
        getLogs();
      }
    }, intervalTime);
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.logViewTomcatLogs', {
      url: '^/logview/tomcatLogs',
      templateUrl: 'modules/logview/tomcatLogs.html',
      controller: tomcatLogsCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
