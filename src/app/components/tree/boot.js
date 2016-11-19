'use strict';

angular.module('pea.tree.tpls', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('tree/template/main.html',
    '<li>\n' +
    '  <span ng-click="treeOptions.onSwitchClick(node);">' +
    '    <md-icon md-font-icon="material-icons">{{ node.loading ? ' +
    '                                               "autorenew" : ' +
    '                                               !node.children&&!node.hasChildren ? ' +
    '                                                 "keyboard_arrow_right" : ' +
    '                                                 (node.children||node.hasChildren)&&node.open ? ' +
    '                                                   "remove" : ' +
    '                                                   "add"}}' +
    '    </md-icon>\n' +
    '  </span>\n' +
    '  <a ng-click="treeOptions.onNodeClick(node)">\n' +
    '    <md-icon md-font-icon="material-icons">{{ (node.children||node.hasChildren)&&node.open ? ' +
    '                                                 "folder_open" : ' +
    '                                                 (node.children||node.hasChildren)&&!node.open ? ' +
    '                                                   "folder" : ' +
    '                                                   "insert_drive_file"}}' +
    '    </md-icon>\n' +
    '    <span ng-bind="node.name"></span>\n' +
    '  </a>\n' +
    '  <ul ng-include="\'tree/template/main.html\'" ng-repeat="node in node.children | filter:{visible:true}"></ul>\n' +
    '</li>\n' +
    '');

  $templateCache.put('tree/template/tpl.html',
    '<div class="peaTree">\n' +
    '  <ul ng-include="\'tree/template/main.html\'" ng-repeat="node in treeOptions.treeModel | filter:{visible:true}"></ul>\n' +
    '</div>\n' +
    '');
}]);

angular.module('pea.tree', ['pea.tree.tpls']);
