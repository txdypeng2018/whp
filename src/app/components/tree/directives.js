'use strict';

/**
 * Example code
 *
 * html:
 *
 * <pea-tree></pea-tree>
 *
 * controller:
 *
 * var simpleNodes = [
     { id:1, pId:0, name:"父节点1 - 展开", open:true},
     { id:11, pId:1, name:"父节点11 - 折叠"},
     { id:111, pId:11, name:"叶子节点111"},
     { id:112, pId:11, name:"叶子节点112"},
     { id:113, pId:11, name:"叶子节点113"},
     { id:114, pId:11, name:"叶子节点114"},
     { id:12, pId:1, name:"父节点12 - 折叠"},
     { id:121, pId:12, name:"叶子节点121"},
     { id:122, pId:12, name:"叶子节点122"},
     { id:123, pId:12, name:"叶子节点123"},
     { id:124, pId:12, name:"叶子节点124"},
     { id:13, pId:1, name:"父节点13 - 没有子节点", hasChildren:true},
     { id:2, pId:0, name:"父节点2 - 折叠"},
     { id:21, pId:2, name:"父节点21 - 展开", open:true},
     { id:211, pId:21, name:"叶子节点211"},
     { id:212, pId:21, name:"叶子节点212"},
     { id:213, pId:21, name:"叶子节点213"},
     { id:214, pId:21, name:"叶子节点214"},
     { id:22, pId:2, name:"父节点22 - 折叠"},
     { id:221, pId:22, name:"叶子节点221"},
     { id:222, pId:22, name:"叶子节点222"},
     { id:223, pId:22, name:"叶子节点223"},
     { id:224, pId:22, name:"叶子节点224"},
     { id:23, pId:2, name:"父节点23 - 折叠"},
     { id:231, pId:23, name:"叶子节点231"},
     { id:232, pId:23, name:"叶子节点232"},
     { id:233, pId:23, name:"叶子节点233"},
     { id:234, pId:23, name:"叶子节点234"},
     { id:3, pId:0, name:"父节点3 - 没有子节点", hasChildren:true},
     { id:4, pId:0, name:"infinite node", hasChildren:true}
   ];

   $scope.treeOptions = {
     nodes: simpleNodes,
     onExpand: function (node) {
       console.debug('on expand');
       if (node.name.indexOf('infinite node') > -1 && !node.children) {
         var children = [
           {id: node.id * 1 + 100, pId: node.id, name: "infinite node", hasChildren: true},
           {id: node.id * 2 + 100, pId: node.id, name: "infinite node", hasChildren: true},
           {id: node.id * 3 + 100, pId: node.id, name: "infinite node", hasChildren: true}
         ];
         node.children = [];
         angular.forEach(children, function (child) {
           child.parent = node;
           node.children.push(child);
         });
       }
     },
     onCollapse: function (node) {
       console.debug('on collapse');
     },
     onClick: function (node) {
       console.debug('on click node');
     }
   };
 */
angular.module('pea.tree')

  .directive('peaTree', function(){
    return {
      restrict: 'E',
      templateUrl: 'tree/template/tpl.html',
      replace: true,
      link: function($scope) {

        var treeOptions = $scope.treeOptions || {};

        var treeModel = treeOptions.treeModel = [];

        // Map<nodeId, nodeObject>
        var treeMap = treeOptions.treeMap = [];

        var modifyChildrenVisible = function(node) {
          angular.forEach(node.children, function(child) {
            child.visible = node.visible && node.open;
            modifyChildrenVisible(child);
          });
        };

        /**
         * @ngdoc function
         * @description Pre-handle simple nodes to tree model.
         *
         * Simple nodes supplied by $scope.treeOptions.nodes,
         * or use $scope.treeOptions.async function to supply initial simple nodes
         * A simple node is like this:
         * {
         *   id: 1,                   // node id
         *   pId: 0,                  // parent node id
         *   name: 'demo',            // node name show on page
         *   open: true|false,        // node open state
         *   hasChildren: true|false  // has children or not
         * }
         *
         * Tree model is a node object list, node object extends simple node with some properties.
         * A node object is like this:
         * {
         *   id: 1, pId: 0, name: 'demo', open: true, hasChildren: true,
         *   parent: parentNode,      // parent node
         *   children: childrenNodes, // children nodes array
         *   visible: true|false,     // show or hide on page
         *   loading: true|false      // show loading style or not
         * }
         *
         * Pre-handle function do things as below:
         * 1. adds node object to the tree map, which is a node id and node object map
         * 2. extends simple node to node object
         * 3. adds root level node object to tree model
         * 4. refreshes the visible of root level node objects' children
         *
         * @param {Array} nodes simple nodes array
         */
        var preHandle = function(nodes) {
          angular.forEach(nodes, function(node) {
            treeMap[node.id] = node;
          });

          angular.forEach(nodes, function(node) {
            var parentNode = treeMap[node.pId];
            if(parentNode) {
              parentNode.children = parentNode.children || [];
              parentNode.children.push(node);
              node.parent = parentNode;
            } else {
              node.visible = true;
              treeModel.push(node);
            }
          });

          angular.forEach(treeModel, function(node) {
            if(node.open) {
              modifyChildrenVisible(node);
            }
          });
        };

        // initial tree model
        if(!treeOptions.nodes && treeOptions.async) {
          treeOptions.async()
            .then(function(children) {
              preHandle(children);
            });
        } else {
          preHandle(treeOptions.nodes);
        }

        var addChildrenToNode = function(parent, children) {
          parent.children = parent.children || [];
          angular.forEach(children, function(child) {
            child.parent = parent;
            parent.children.push(child);
            child.visible = parent.visible && parent.open;
            treeMap[child.id] = child;
          });
        };

        var refreshNode = function(node) {
          node.loading = true;
          node.children = [];
          return treeOptions.async(node)
            .then(function(children) {
              addChildrenToNode(node, children);
            })
            .then(function() {
              node.loading = false;
            });
        };

        var doSwitchClickLogic = function(node) {
          if(node.open) {
            if(treeOptions.onCollapse) {
              treeOptions.onCollapse(node);
            }
          } else {
            if(treeOptions.onExpand) {
              treeOptions.onExpand(node);
            }
          }
          node.open = !node.open;
          modifyChildrenVisible(node);
        };

        treeOptions.onSwitchClick = function(node) {
          if(!node.children && !node.hasChildren) {
            return;
          }

          if(treeOptions.async && !node.open && !node.children) {
            refreshNode(node).then(function() {
              doSwitchClickLogic(node);
            });
          } else {
            doSwitchClickLogic(node);
          }
        };

        treeOptions.onNodeClick = function(node) {
          if(treeOptions.onClick) {
            treeOptions.onClick(node);
          }
        };

        treeOptions.refreshById = function(nodeId) {
          var node = treeMap[nodeId];
          refreshNode(node).then(function() {
            modifyChildrenVisible(node);
          });
        };
      }
    };
  });
