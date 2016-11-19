'use strict';

(function(app) {

  var createModelCtrl = function($scope, $mdDialog) {
    $scope.cancel = function() {
      $mdDialog.cancel();
    };
    $scope.save = function() {
      $scope.model.metaInfo = JSON.stringify($scope.model);
      $mdDialog.hide($scope.model);
    };
  };

  var gotoModeler = function(modelId) {
    window.location = '/workflow/modeler.html?modelId=' + modelId;
  };

  var initEditorSource = function(ModelSource, Model, model) {
    var modelSrc = new ModelSource();
    modelSrc.id = model.id;
    modelSrc.$init({
      name: model.name,
      description: JSON.parse(model.metaInfo).description
    }, function() {
      gotoModeler(model.id);
    }, function(err) {
      console.debug('Error occurs while initial model source. %o', err);
      console.debug('Delete model that just created.');
      Model.delete({modelId: model.id});
    });
  };

  var createModel = function($mdDialog, Model, ModelSource) {
    $mdDialog.show({
      controller: createModelCtrl,
      templateUrl: 'modules/workflow/designer/create-model.html',
      clickOutsideToClose: false
    }).then(function(data) {
      var model = new Model(data);
      model.$save(function() {
        initEditorSource(ModelSource, Model, model);
      });
    });
  };

  var importModel = function($mdDialog) {
    $mdDialog.show({
      controller: importModelCtrl,
      templateUrl: 'modules/workflow/designer/import-model.html',
      clickOutsideToClose: false
    });
  };

  var importModelCtrl = function($scope, $mdDialog, Upload, $log, $state, components) {
    $scope.cancel = function() {
      $mdDialog.cancel();
    };
    $scope.import = function(file) {
      Upload.upload({
        url: '/workflow/service/repository/models/import',
        data: {file: file}
      }).then(function(resp) {
        $log.debug('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
        $mdDialog.hide();
        $state.reload();
        components.showToast('导入成功');
      }, function(resp) {
        $log.debug('Upload file error! Error status: ' + resp.status);
        components.showToast('导入失败');
      });
    };
  };

  var designerCtrl = function(wfrFactory, $scope, $mdDialog, components, $state, $log) {
    var Model = wfrFactory.model.model();
    var ModelSource = wfrFactory.model.source();
    var ModelDeployment = wfrFactory.model.deployment();
    var ProcDef = wfrFactory.procDef.self();
    var Deployment = wfrFactory.deployment.deployment();
    var ProcInst = wfrFactory.procInst.self();

    $scope.qsQuery = function(params) {
      params = params || {};
      if ($scope.qsText) {
        params.nameLike = '%' + $scope.qsText + '%';
      }
      var models = Model.query(params, function() {
        $scope.total = models.total;
        $scope.showMoreListItems = (models.total !== models.size);
        $scope.items = [];
        angular.forEach(models.data, function(m) {
          $scope.items.push(m);
          if (m.deploymentId) {
            ProcDef.query({deploymentId: m.deploymentId}, function(res) {
              m.procDef = res.data[0];
            });
            Deployment.get({deploymentId: m.deploymentId}, function(res) {
              m.deploymentTime = res.deploymentTime;
            });
          }
        });
      });
    };

    $scope.qsQuery();

    $scope.clickListItem = function(item) {
      //$log.debug(item);
      $scope.m = item;
      $scope.m.description = JSON.parse(item.metaInfo).description;
      var couldStart = couldStartProcess(item);
      $scope.dosTitle = couldStart ? '启动流程' : '部署流程';
      $scope.dosIcon = couldStart ? 'directions_run' : 'cloud_upload';
    };

    var couldStartProcess = function(model) {
      // 由于流程部署后会将部署 id 和 url 更新至模型中,故在一次部署后,模型的最后更新时间一定会晚于部署时间一些
      // 为了标记出流程部署后再次修改且尚未部署的情况,此处需要判断流程的最后更新时间是否远远晚于部署时间
      return model.deploymentId && !longLongAgo(model.deploymentTime, model.lastUpdateTime);
    };

    /**
     * 终止日期是否远远晚于起始日期
     * 远远晚于,暂时设定为 1 秒
     *
     * @param d1Str 起始日期的 ISO String
     * @param d2Str 终止日期的 ISO String
     * @returns {boolean}
     */
    var longLongAgo = function(d1Str, d2Str) {
      var d1 = new Date(d1Str);
      var d2 = new Date(d2Str);
      var oneSecond = 1000;
      return d2.getTime() - d1.getTime() > oneSecond;
    };

    $scope.getMoreListItems = function() {
      $scope.qsQuery({size: $scope.total});
    };

    $scope.listItemTemplate = function(item) {
      return item.name + (couldStartProcess(item) ? '' : ' *');
    };

    $scope.contentPageUrl = 'modules/workflow/designer/designer.html';

    $scope.createItem = function() {
      createModel($mdDialog, Model, ModelSource);
    };

    $scope.importModel = function() {
      importModel($mdDialog);
    };

    $scope.fabBtns = [{
      ariaLabel: 'New Model',
      tooltip:   '创建',
      icon:      'add',
      click:     $scope.createItem
    }, {
      ariaLabel: 'Import Models',
      tooltip:   '导入',
      icon:      'import_export',
      click:     $scope.importModel
    }];

    $scope.editItem = function() {
      if ($scope.m) {
        gotoModeler($scope.m.id);
      }
    };

    $scope.delItem = function(ev) {
      // 未选中流程或流程已部署则不允许删除(删除已部署的流程逻辑比较麻烦,暂不实现)
      if (!$scope.m || $scope.m.deploymentId) {
        return;
      }

      var success = function() {
        var modelId = $scope.m.id;
        Model.delete({modelId: modelId}, function() {
          components.showToast('删除成功');
          $state.reload();
        }, function(err) {
          $log.debug('Error occurs when deleting model %s! %o', modelId, err);
          components.showToast('删除失败');
        });
      };

      components.confirm(ev, '确认删除?', null, '删除').then(success);
    };

    $scope.deployOrStart = function () {
      if (couldStartProcess($scope.m)) {
        $scope.startProcess();
      } else {
        $scope.deploy();
      }
    };

    $scope.deploy = function() {
      var errCallback = function(err) {
        $log.debug('Error occurs when deploying! %o', err);
        components.showToast('部署失败');
      };
      if ($scope.m && !couldStartProcess($scope.m)) {
        ModelDeployment.save($scope.m, function (deployment) {
          $scope.m.deploymentId = deployment.id;
          $scope.m.deploymentUrl = deployment.url;
          Model.update($scope.m, function() {
            components.showToast('部署成功');
            $state.reload();
          }, errCallback);
        }, errCallback);
      }
    };

    $scope.startProcess = function() {
      if ($scope.m && couldStartProcess($scope.m)) {
        ProcInst.start({
          processDefinitionId: $scope.m.procDef.id
        }, function() {
          $log.debug('Should implement after start process logic here...');
        }, components.errCallback);
      }
    };

    $scope.export = function() {
      if ($scope.m) {
        window.location = wfrFactory.model.exportUrl($scope.m.id);
      }
    };

    $scope.hotkeys = $scope.hotkeys || [];
    $scope.hotkeys.push({
      combo:       'f w',
      description: '部署流程',
      callback:     $scope.deploy
    }, {
      combo:       'f x',
      description: '启动流程',
      callback:     $scope.startProcess
    }, {
      combo:       'f r',
      description: '导入流程',
      callback:     $scope.importModel
    }, {
      combo:       'f t',
      description: '导出流程',
      callback:     $scope.export
    });
  };

  var designerRouter = function($stateProvider, PEA) {
    $stateProvider.state('main.workflowDesigner', {
      url: '^/workflow/designer',
      controller: designerCtrl,
      templateUrl: PEA.tpls.list2Cols
    });
  };

  app.config(designerRouter);

})(angular.module('pea'));
