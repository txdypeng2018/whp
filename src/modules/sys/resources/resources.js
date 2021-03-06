'use strict';

(function(app) {

  var ctrl = function($scope, $resource) {

    var simpleNodes = [
      { id:1, pId:0, name:'父节点1 - 展开', open:true},
      { id:11, pId:1, name:'父节点11 - 折叠'},
      { id:111, pId:11, name:'叶子节点111'},
      { id:112, pId:11, name:'叶子节点112'},
      { id:113, pId:11, name:'叶子节点113'},
      { id:114, pId:11, name:'叶子节点114'},
      { id:12, pId:1, name:'父节点12 - 折叠'},
      { id:121, pId:12, name:'叶子节点121'},
      { id:122, pId:12, name:'叶子节点122'},
      { id:123, pId:12, name:'叶子节点123'},
      { id:124, pId:12, name:'叶子节点124'},
      { id:13, pId:1, name:'父节点13 - 没有子节点', hasChildren:true},
      { id:2, pId:0, name:'父节点2 - 折叠'},
      { id:21, pId:2, name:'父节点21 - 展开', open:true},
      { id:211, pId:21, name:'叶子节点211'},
      { id:212, pId:21, name:'叶子节点212'},
      { id:213, pId:21, name:'叶子节点213'},
      { id:214, pId:21, name:'叶子节点214'},
      { id:22, pId:2, name:'父节点22 - 折叠'},
      { id:221, pId:22, name:'叶子节点221'},
      { id:222, pId:22, name:'叶子节点222'},
      { id:223, pId:22, name:'叶子节点223'},
      { id:224, pId:22, name:'叶子节点224'},
      { id:23, pId:2, name:'父节点23 - 折叠'},
      { id:231, pId:23, name:'叶子节点231'},
      { id:232, pId:23, name:'叶子节点232'},
      { id:233, pId:23, name:'叶子节点233'},
      { id:234, pId:23, name:'叶子节点234'},
      { id:3, pId:0, name:'父节点3 - 没有子节点', hasChildren:true},
      { id:4, pId:0, name:'infinite node', hasChildren:true}
    ];

    $scope.treeOptions = {
      nodes: simpleNodes,
      onExpand: function (node) {
        console.debug('on expand');
        if (node.name.indexOf('infinite node') > -1 && !node.children) {
          var children = [
            {id: node.id * 1 + 100, pId: node.id, name: 'infinite node', hasChildren: true},
            {id: node.id * 2 + 100, pId: node.id, name: 'infinite node', hasChildren: true},
            {id: node.id * 3 + 100, pId: node.id, name: 'infinite node', hasChildren: true}
          ];
          node.children = [];
          angular.forEach(children, function (child) {
            child.parent = node;
            node.children.push(child);
          });
        }
      },
      onCollapse: function (node) {
        console.debug('on collapse %o', node);
      },
      onClick: function (node) {
        console.debug('on click node %o', node);
      }
    };

    $scope.gridOptions = {
      title: '资源列表',
      query: {
        order: 'name'
      },
      head: [
        { label: '名称', orderBy: 'name' },
        { label: '序号' },
        { label: 'URL', orderBy: 'url' },
        { label: '分类', orderBy: 'method' },
        { label: '备注' }
      ],
      body: [
        'item.name',
        'gridOptions.items.data.indexOf(item) + 1',
        'item.url',
        'item.method',
        '"abcdefghijklmnopqrstuvwxyz"'
      ]
    };

    var Resource = $resource('/sys/resources/:resourceId', {resourceId: '@id'}, {
      query: { method: 'GET' }
    });

    Resource.query(function(res) {
      $scope.gridOptions.items = res;
    });
  };

  var router = function($stateProvider, PEA) {
    $stateProvider.state('main.sysResources', {
      url: '^/sys/resources',
      controller: ctrl,
      templateUrl: PEA.tpls.treeGrid
    });
  };

  app.config(router);

})(angular.module('pea'));
