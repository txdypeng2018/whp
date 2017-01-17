'use strict';

var perGridCtrl = function($scope, $element, $window, $timeout, $http, screenSizeSm) {
  //表格满屏显示函数
  var setGridFullHeight = function($element, $window, screenSizeSm) {
    if ($window.innerWidth < screenSizeSm) {
      $element.css('height', document.body.clientHeight - 118 + 'px');
    }
    else {
      $element.css('height', document.body.clientHeight - 68 + 'px');
    }
  };

  //页脚响应式显示函数
  var setPaginationDisplay = function($scope, $element, $window, $timeout) {
    $timeout(function(){
      if ($element[0].clientWidth > 450) {
        $scope.gridParam.pageSelect = true;
        $scope.gridParam.boundaryLinks = true;
      }
      else if ($element[0].clientWidth > 380) {
        $scope.gridParam.pageSelect = true;
        $scope.gridParam.boundaryLinks = false;
      }
      else {
        $scope.gridParam.pageSelect = false;
        $scope.gridParam.boundaryLinks = false;
      }
    });
  };

  //变量初始化
  //内部方法和变量放置位置
  $scope.innerParam = {};
  //每页默认显示20条
  $scope.gridParam.limit = 20;
  //可选择每页显示条数
  $scope.gridParam.limitOptions = [10, 20, 30];
  //选中的记录
  $scope.gridParam.selected = [];
  //默认关闭查询栏
  $scope.innerParam.searchBar = false;
  //默认关闭行编辑
  $scope.innerParam.showRowEdit = false;
  $scope.innerParam.isRowEditSubmit = false;
  //过滤条件参数
  $scope.gridParam.filter = '';
  $scope.innerParam.filter = {
    options: {
      debounce: 300
    }
  };
  $scope.innerParam.gridFiltered = [];

  //设置默认值
  $scope.gridParam.idField = $scope.gridParam.idField || 'id';
  if (angular.isUndefined($scope.gridParam.isAllDataLoad)) {
    $scope.gridParam.isAllDataLoad = true;
  }
  $scope.gridParam.page = $scope.gridParam.page || 1;
  if (angular.isUndefined($scope.gridParam.toolbar)) {
    $scope.gridParam.toolbar = true;
  }
  if (!angular.isUndefined($scope.gridParam.height)) {
    $element.css('height', $scope.gridParam.height);
  }
  else {
    setGridFullHeight($element, $window, screenSizeSm);
    angular.element($window).bind('resize', function() {
      setGridFullHeight($element, $window, screenSizeSm);
    });
  }
  if (angular.isUndefined($scope.gridParam.rowSelection)) {
    $scope.gridParam.rowSelection = true;
  }
  if (angular.isUndefined($scope.gridParam.multiSelect)) {
    $scope.gridParam.multiSelect = false;
  }
  if (angular.isUndefined($scope.gridParam.autoSelect)) {
    $scope.gridParam.autoSelect = true;
  }
  if (angular.isUndefined($scope.gridParam.pagination)) {
    $scope.gridParam.pagination = true;
  }
  if (!$scope.gridParam.pagination) {
    $scope.gridParam.isAllDataLoad = true;
  }
  $scope.gridParam.order = $scope.gridParam.order || '';
  if (angular.isUndefined($scope.gridParam.hasFilter)) {
    $scope.gridParam.hasFilter = false;
  }
  if (angular.isUndefined($scope.gridParam.fixedCheckbox)) {
    $scope.gridParam.fixedCheckbox = false;
  }
  if (!$scope.gridParam.fixedCheckbox && !angular.isUndefined($scope.gridParam.columns)) {
    for (var k = 0 ; k < $scope.gridParam.columns.length ; k++) {
      if ($scope.gridParam.columns[k].isFixed) {
        $scope.gridParam.fixedCheckbox = true;
      }
    }
  }
  $scope.gridParam.searchData = $scope.gridParam.searchData || {};
  $scope.innerParam.searchData = angular.copy($scope.gridParam.searchData);
  $scope.gridParam.rowEditData = $scope.gridParam.rowEditData || {};

  //列显示
  var columnCustomLocal = $window.localStorage[$scope.gridParam.columnCustom];
  var columnCustomArray = [];
  if (!angular.isUndefined(columnCustomLocal) && columnCustomLocal !== null && columnCustomLocal !== '') {
    columnCustomArray = columnCustomLocal.split(',');
  }
  if (!angular.isUndefined($scope.gridParam.columnCustom)) {
    for (var m = 0 ; m < $scope.gridParam.columns.length ; m++) {
      if (columnCustomArray.indexOf($scope.gridParam.columns[m].field) >= 0) {
        $scope.gridParam.columns[m].isHide = true;
      }
    }
  }

  //方法初期化
  //从后台取得数据方法
  $scope.gridParam.loadData = function() {
    var order = '';
    var orderBy = '';
    if ($scope.gridParam.order !== '') {
      if ($scope.gridParam.order.substr(0, 1) === '-') {
        order = 'DESC';
        orderBy = $scope.gridParam.order.substr(1, $scope.gridParam.order.length-1);
      }
      else {
        order = 'ASC';
        orderBy = $scope.gridParam.order;
      }
    }
    var param = {
      isAllDataLoad: $scope.gridParam.isAllDataLoad,
      pageNo: $scope.gridParam.page,
      pageSize: $scope.gridParam.limit,
      orderBy: orderBy,
      order: order,
      filter: $scope.gridParam.filter
    };
    if (!angular.isUndefined($scope.gridParam.searchData)) {
      for (var key in $scope.gridParam.searchData) {
        param[key] = $scope.gridParam.searchData[key];
      }
    }
    if (!angular.isUndefined($scope.gridParam.onBeforeLoad)) {
      $scope.gridParam.onBeforeLoad(param);
    }
    $http.get($scope.gridParam.url, {params: param}).success(function(data) {
      $scope.gridParam.desserts = data;
      $scope.gridParam.selected = [];
    });
  };

  //显示查询栏
  $scope.gridParam.showSearchBar = function() {
    if ($scope.gridParam.hasFilter) {
      $scope.innerParam.searchBar = true;
    }
    else {
      $scope.innerParam.showSearchAdvanced();
    }
  };
  //关闭查询栏
  $scope.gridParam.closeSearchBar = function() {
    $scope.innerParam.searchBar = false;
    setTimeout(function(){
      $scope.gridParam.filter = '';
    }, 300);
  };
  //显示高级查询栏
  $scope.innerParam.showSearchAdvanced = function() {
    $element[0].querySelector('.search-sheet').style.display = 'block';
    $element[0].querySelector('.search-backdrop').style.display = 'block';
    setTimeout(function(){
      $element[0].querySelector('.search-sheet').style.marginTop = '0px';
    }, 10);
    setTimeout(function(){
      $element[0].querySelector('.btn-advanced-search').focus();
    }, 300);
  };
  //关闭高级查询栏
  var closeSearchAdvancedOp = function() {
    $element[0].querySelector('.search-sheet').style.marginTop = '';
    setTimeout(function(){
      $element[0].querySelector('.search-sheet').style.display = 'none';
      $element[0].querySelector('.search-backdrop').style.display = 'none';
    }, 300);
  };
  $scope.innerParam.closeSearchAdvanced = function() {
    $scope.gridParam.searchData = angular.copy($scope.innerParam.searchData);
    closeSearchAdvancedOp();
  };
  //高级查询
  $scope.innerParam.querySearchAdvanced = function() {
    $scope.gridParam.page = 1;
    $scope.innerParam.searchData = angular.copy($scope.gridParam.searchData);
    $scope.gridParam.loadData();
    closeSearchAdvancedOp();
  };
  //重置高级查询
  $scope.innerParam.resetSearchAdvanced = function() {
    $scope.gridParam.searchData = {};
    $scope.innerParam.searchData = {};
    $scope.gridParam.page = 1;
    $scope.gridParam.loadData();
    closeSearchAdvancedOp();
  };
  //关闭高级查询栏快捷键(ESC)
  angular.element(document).bind('keydown', function(event) {
    if (event.keyCode === 27) {
      var searchDisplay = $element[0].querySelector('.search-sheet').style.display;
      if (angular.isUndefined(searchDisplay) || searchDisplay === '' || searchDisplay === 'none') {
        $scope.gridParam.selected = [];
        $scope.$apply($scope.gridParam.selected);
      }
      else {
        $scope.innerParam.closeSearchAdvanced();
      }
    }
  });

  //显示行编辑
  $scope.gridParam.disableRowEditSubmit = function(flg) {
    $scope.innerParam.isRowEditSubmit = flg;
  };
  $scope.gridParam.showRowEdit = function() {
    $scope.gridParam.rowEditData = {};
    $scope.gridParam.disableRowEditSubmit(false);
    $scope.innerParam.showRowEdit = true;
  };
  $scope.gridParam.closeRowEdit = function() {
    $scope.innerParam.showRowEdit = false;
  };
  $scope.gridParam.rowEditFrom = function() {
    return $scope.gridForm;
  };

  //两种翻页加载方式控制
  $scope.innerParam.orderBy = function(gridParam) {
    return $scope.gridParam.isAllDataLoad? gridParam.order : '';
  };
  $scope.innerParam.limitTo = function(gridParam) {
    return ($scope.gridParam.isAllDataLoad && $scope.gridParam.pagination)? gridParam.limit : '';
  };
  $scope.innerParam.limitToBegin = function(gridParam) {
    return ($scope.gridParam.isAllDataLoad && $scope.gridParam.pagination)? (gridParam.page -1) * gridParam.limit : '';
  };

  //判断是否在前台过滤记录
  $scope.innerParam.getFilter = function(gridParam) {
    return $scope.gridParam.isAllDataLoad? gridParam.filter : '';
  };

  //取得总记录数
  $scope.innerParam.getTotal = function() {
    if ($scope.gridParam.isAllDataLoad) {
      return $scope.innerParam.gridFiltered? $scope.innerParam.gridFiltered.length : 0;
    }
    else {
      return $scope.gridParam.desserts? $scope.gridParam.desserts.count : 0;
    }
  };

  //自定义列
  $scope.innerParam.columnCustomChecked = function(field) {
    return columnCustomArray.indexOf(field) < 0;
  };
  $scope.innerParam.columnCustomClick = function(columnCustom, field) {
    if (columnCustomArray.indexOf(field) >= 0) {
      columnCustomArray.splice(columnCustomArray.indexOf(field), 1);
    }
    else {
      columnCustomArray.push(field);
    }
    for (var m = 0 ; m < $scope.gridParam.columns.length ; m++) {
      $scope.gridParam.columns[m].isHide = columnCustomArray.indexOf($scope.gridParam.columns[m].field) >= 0;
    }
    $window.localStorage[columnCustom] = columnCustomArray.join(',');
  };

  //从后台取得数据
  if (!angular.isUndefined($scope.gridParam.url)) {
    $scope.gridParam.loadData();
  }

  //页脚响应式显示
  setPaginationDisplay($scope, $element, $window, $timeout);
  angular.element($window).bind('resize', function() {
    setTimeout(function(){
      setPaginationDisplay($scope, $element, $window, $timeout);
    }, 300);
  });

  //编辑操作按钮参数
  if (!angular.isUndefined($scope.gridParam.operations)) {
    var btns = {
      unSelected: [],
      singleSelected: [],
      multipleSelected: []
    };
    for (var i = 0 ; i < $scope.gridParam.operations.length ; i++) {
      var operation = $scope.gridParam.operations[i];
      if (angular.isUndefined(operation.display)) {
        operation.display = ['unSelected'];
      }
      for (var j = 0 ; j < operation.display.length ; j++) {
        if (operation.display[j] === 'unSelected') {
          btns.unSelected.push(operation);
        }
        else if (operation.display[j] === 'singleSelected') {
          btns.singleSelected.push(operation);
        }
        else if (operation.display[j] === 'multipleSelected') {
          btns.multipleSelected.push(operation);
        }
      }
    }
    $scope.gridParam.btns = btns;
  }

  //换页或排序时从新加载数据
  if (!$scope.gridParam.isAllDataLoad) {
    $scope.gridParam.onPaginate = function() {
      $scope.gridParam.loadData();
    };
    $scope.gridParam.onReorder = function() {
      $scope.gridParam.loadData();
    };
  }

  //过滤记录时从新设置列表翻页
  $scope.$watch('gridParam.filter', function () {
    $scope.gridParam.page = 1;
    if (!$scope.gridParam.isAllDataLoad) {
      $scope.gridParam.loadData();
    }
  });

  var scrollContainer = angular.element($element[0].querySelector('md-table-container'));
  scrollContainer.on('scroll', function () {
    //合计跟随列表内容横滚动条滚动
    angular.element($element[0].querySelector('.pea-tbody-total')).css('transform', 'translate3d(' + -(scrollContainer.prop('scrollLeft')) + 'px, 0, 0)');
    //固定列不随横滚动条滚动
    angular.element($element[0].querySelectorAll('.fixed-column')).css('transform', 'translate3d(' + (scrollContainer.prop('scrollLeft')) + 'px, 0, 0)');
    //checkbox固定不随横滚动条滚动
    if ($scope.gridParam.fixedCheckbox) {
      angular.element($element[0].querySelectorAll('.md-checkbox-cell')).css('transform', 'translate3d(' + (scrollContainer.prop('scrollLeft')) + 'px, 0, 0)');
      angular.element($element[0].querySelectorAll('.md-checkbox-column')).css('transform', 'translate3d(' + (scrollContainer.prop('scrollLeft')) + 'px, 0, 0)');
    }
  });
};

/**
 * @ngdoc overview
 * @name PeaGrid
 *
 * @description
 * 介绍PeaGrid使用方法，及各参数说明
 *
 * @param {Object} gridParam 生成PeaGrid的参数
 *
 * @example
 * //html示例
 * <pea-grid grid-param="gridParam"></pea-grid>
 *
 * //PeaGrid参数示例
 * $scope.gridParam = {
 *   columns: [{field: 'dept', title: '科室', width: 260},
 *     {field: 'title', title: '职称', width: 220}],    //类型：Array  说明：表头属性，更多详细请参见column的说明  默认值：null
 *   typeColumns: [[{title: '', colspan: 2, isFixed: false},
        {title: '类别1', colspan: 2}]],                 //类型：Array  说明：双层表头的上层，colspan表示跨越列数，isFixed表示列固定（必须与下层表头isFixed属性一致），typeColumns内的总列数必须与columns一致  默认值：null
 *   idField: 'id',                                    //类型：String  说明：记录唯一标识字段  默认值：id
 *   url: '/playground/grid/basic',                    //类型：String  说明：从远程请求数据的url，后台返回数据格式参加desserts的说明  默认值：null
 *   isAllDataLoad: true,                              //类型：Boolean  说明：是否一次加载所有数据  默认值：true
 *   page: 1,                                          //类型：Int  说明：指定显示页码，随翻页变化  默认值：1
 *   toolbar: true,                                    //类型：Boolean  说明：是否显示工具栏，工具栏用来显示标题、操作、查询  默认值：true
 *   operations: [                                     //类型：Object  说明：指定工具栏的操作，更多详细请参见operation的说明  默认值：null
 *     {text: '删除', click: clkF, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
 *   ],
 *   title: 'User Mange',                              //类型：String  说明：表格标题  默认值：null
 *   height: '500px',                                  //类型；String  说明：指定表格高度  默认值：满屏
 *   rowSelection: true,                               //类型：Boolean  说明：是否可选择行  默认值：true
 *   multiSelect: false,                               //类型：Boolean  说明：是否可以多选  默认值：false
 *   autoSelect: true,                                 //类型：Boolean  说明：在行内任意位置可选中行  默认值：true
 *   onDeselect: function(rowData) {...},              //类型：Function  说明：去除行选择时触发的事件  默认值：null
 *   onSelect: function(rowData) {...},                //类型：Function  说明：行选择时触发的事件  默认值：null
 *   selected: [],                                     //类型：Array  说明：列表选中记录的集合  默认值：空
 *   dblClick: function(rowData) {...},                //类型：Function  说明：双击行时触发的事件  默认值：null
 *   ngDisabled: function(rowData){                    //类型：Function  说明：指定不能选择的行  默认值：null
 *      return rowData.name === 'aa';
 *   },
 *   pagination: true,                                 //类型：Boolean  说明：是否显示翻页栏  默认值：true
 *   order: '-name',                                   //类型：String  说明：初始化数据排序字段，倒序字段前面加'-'  默认值：空
 *   rowStyler: function(rowData) {                    //类型：Function  说明：设置指定行背景色  默认值：null
 *     if (rowData.name === 'aa') {
 *        return '#ffffff';
 *      }
 *   },
 *   onBeforeLoad: function(param){                    //类型：Function  说明：从后台取得数据前执行，设置需要向后台传递的数据  默认值：null
 *     param['other'] = 'Y';
 *   },
 *   fixedCheckbox: false,                             //类型：Boolean  说明：是否固定checkbox列，不随滚动条滚动  默认值：false，有列固定时为true
 *   hasFilter: false,                                 //类型：Boolean  说明：是否使用拦截器查询  默认值：false
 *   searchTemplate: 'search.html',                    //类型：String  说明：高级查询模板  默认值：null
 *   searchInitData: {},                               //类型：Object  说明：高级查询模板初期化值（如数据字典）放置的位置  默认值：null
 *   searchData：{},                                   //类型：Object  说明：高级查询条件放置值的位置，模板内参数位置必须与此一致  默认值：空
 *   rowEditTemplate: 'rowEdit.html',                  //类型：String  说明：行内编辑模板  默认值：null
 *   rowEditInitData: {},                              //类型：Object  说明：行内编辑模板初期化值（如数据字典）放置的位置  默认：null
 *   rowEditData：{},                                  //类型：Object  说明：行内编辑模板放置值的位置，模板内参数位置必须与此一致  默认值：空
 *   rowEditSubmit: function() {...},                  //类型：Object  说明：行内编辑提交方法  默认值：null
 *   columnCustom: '唯一ID'                            //类型：String  说明：用户是否可以自定义显示哪些列（不能和多层表头混合使用），用户设置保存在本地缓存传入唯一ID名下  默认值：null
 * }
 *
 * //column参数示例
 * column = {
 *   field: 'name',                                    //类型：String  说明：表列数据标识  默认值：null
 *   title: '姓名',                                     //类型：String  说明：表头名称  默认值：null
 *   width: 200,                                       //类型：Int  说明：表列宽度  默认值：null
 *   enableSort: 'name',                               //类型：String  说明：表列是否可进行排序，并指定排序依赖的字段标识  默认值：null
 *   descFirst: true,                                  //类型：Boolean  说明：表列排序时，是否先倒序排序  默认值：null
 *   isNumeric: false,                                 //类型：Boolean  说明：指定表列为数字，数据靠右显示  默认值：false
 *   isLineWrap: false,                                //类型：Boolean  说明：文字超长，是否折行显示，默认不折行  默认值：false
 *   isFixed: false,                                   //类型：Boolean  说明：表列固定，不随滚动条滚动，固定列必须从左侧开始  默认值：false
 *   formatter: function(value, rowData){...}          //类型：Function  说明：表列格式化函数，返回格式后的数据  默认值：null
 *   styler: function(value, rowData){...}             //类型：Function  说明：设置单元格文字颜色函数，返回颜色号  默认值：null
 *   edit: function(rowData, field, event){...}        //类型：Function  说明：单元格编辑方法  默认值：null
 *   editComment: '点击新增备注'                         //类型：String  说明：单元格编辑提示  默认值：null
 * }
 *
 * //operation参数示例
 * operation = {
 *   text: '删除',                                      //类型：String  说明：按钮文字  默认值：null
 *   click: function(selected, event) {...},           //类型：Function  说明：按钮点击触发事件  默认值：null
 *   iconClass: 'delete_black',                        //类型：String  说明：按钮图标  默认值：null
 *   display: ['singleSelected', 'multipleSelected']   //类型：Array  说明：按钮在什么情况下显示（unSelected：无选择时；singleSelected：单选时；multipleSelected：多选时；）  默认值：['unSelected']
 * }
 *
 * //方法
 * $scope.gridParam.loadData()                          //刷新列表数据
 * $scope.gridParam.showSearchBar()                     //显示查询栏
 * $scope.gridParam.closeSearchBar()                    //关闭查询栏
 * $scope.gridParam.showRowEdit()                       //显示行内编辑
 * $scope.gridParam.closeRowEdit()                      //关闭行内编辑
 * $scope.gridParam.disableRowEditSubmit()              //行内编辑提交按钮无效，放置重复提交
 * $scope.gridParam.rowEditFrom()                       //取得行内编辑提交时的From
 *
 * //后台返回数据desserts
 * desserts = {
 *   "count": 28,                                       //类型：Number  说明：全部数据条数
 *   "data": [                                          //类型：Array  说明：取得的数据
 *     {
 *       "id": "1",
 *       "name": "啊啊1"
 *     }
 *   ],
 *   "total": {                                         //类型：Object  说明：合计数据，数据结构必须与data相同
 *     "id": "",
 *     "name": "合计："
 *   }
 * }
 */
angular.module('pea.grid', []).directive('peaGrid', function() {
  return {
    restrict: 'E',
    replace: true,
    scope:{
      gridParam: '=gridParam'
    },
    templateUrl: 'app/components/grid/pea-grid.html',
    controller: perGridCtrl
  };
});
