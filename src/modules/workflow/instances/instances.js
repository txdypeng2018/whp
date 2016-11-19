'use strict';

(function(app) {

  var ctrl = function(wfrFactory, $scope, $sce, components) {
    var ProcInst = wfrFactory.procInst.self();
    var ProcDef = wfrFactory.procDef.self();
    var DiagramSize = wfrFactory.procDef.diagram();

    $scope.qsQuery = function(params) {
      params = params || {};
      var procInsts = ProcInst.query(params, function() {
        $scope.total = procInsts.total;
        $scope.items = procInsts.data;
        angular.forEach($scope.items, function(item) {
          ProcDef.get({procDefId: item.processDefinitionId}, function(procDef) {
            item.procDef = procDef;
          }, components.errCallback);
        });
        $scope.showMoreListItems = (procInsts.total !== procInsts.size);
      });
    };

    $scope.qsQuery();

    $scope.clickListItem = function(procInst) {
      $scope.procInst = procInst;
      var url = '/workflow/diagram-viewer/index.html' +
                  '?processDefinitionId=' + procInst.procDef.id +
                  '&processInstanceId=' + procInst.id;
      $scope.diagramURL = $sce.trustAsResourceUrl(url);

      DiagramSize.get({procDefId: procInst.processDefinitionId}, function(size) {
        $scope.diagramStyle = {
          width: size.width + 'px',
          height: size.height + 'px'
        };
      }, components.errCallback);
    };

    $scope.listItemTemplate = function(item) {
      return item.procDef ? item.procDef.name : '';
    };

    $scope.getMoreListItems = function() {
      $scope.qsQuery({size: $scope.total});
    };

    $scope.contentPageUrl = 'modules/workflow/instances/instances.html';

  };

  var router = function($stateProvider, PEA) {
    $stateProvider.state('main.workflowInstances', {
      url: '^/workflow/instances',
      controller: ctrl,
      templateUrl: PEA.tpls.list2Cols
    });
  };

  app.config(router);

})(angular.module('pea'));
