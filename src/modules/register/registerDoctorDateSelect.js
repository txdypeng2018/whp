(function (app) {
    'use strict';

    var registerDoctorDateSelectCtrl = function ($scope, $http, $state, $stateParams, $filter, $timeout, $cordovaToast, $ionicScrollDelegate) {
        $scope.hideSearch = true;
        $scope.daySelected = '';
        //默认顶部栏选中为按时间选择
        $scope.districtId = '1';
        //是否隐藏时间栏
        $scope.dataPicker = {
            isShow: true
        };
        $scope.leftIconIsShow = false;
        $scope.rightIconIsShow = true;

        $scope.rightSlide = function () {
            $ionicScrollDelegate.scrollBy(357, 0, true);
        };
        $scope.leftSlide = function () {
            $ionicScrollDelegate.scrollBy(-357, 0, true);
        };


        $scope.scroll = function () {
            if ($ionicScrollDelegate.getScrollPosition().left > 1) {
                $scope.leftIconIsShow = true;
            } else {
                $scope.leftIconIsShow = false;
            }
            if($ionicScrollDelegate.getScrollPosition().left >= 1120){
                $scope.rightIconIsShow = false;
            }else{
                $scope.rightIconIsShow = true;
            }
            $scope.$apply($scope.leftIconIsShow);
            $scope.$apply($scope.rightIconIsShow);
            scrollMonth();
        };


        //不同的院区的颜色
        $scope.districtColor = new Map();
        //颜色数组
        var color = ['district-icon-positive', 'district-icon-balanced',
            'district-icon-royal', 'district-icon-calm', 'district-icon-assertive'];
        //院区数量
        var districtCount = 0;
        var displayDays = 30;
        var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

        //取得指定天数以后的日期
        var getNextDay = function (date, days) {
            date = +date + 1000 * 60 * 60 * 24 * days;
            date = new Date(date);
            return date;
        };

        //取得医生照片
        var getDoctorPhoto = function (doctorId, index) {
            $http.get('/doctors/photo', {
                params: {
                    doctorId: doctorId,
                    index: index
                }
            }).success(function (data, status, headers, config) {
              $scope.doctors[config.params.index].photo = data;
            }).error(function (data, status, fun, config) {
              $scope.doctors[config.params.index].photo = '';
            });
        };
        //取得科室下的医生
        $scope.major = $stateParams.major;
        $scope.hasSearchStr = ($scope.major !== '');
        var getDoctors = function () {
            var startDate = '';
            var endDate = '';
            if ($scope.daySelected !== '' && !$scope.hasSearchStr) {
                startDate = $scope.daySelected;
                endDate = $scope.daySelected;
            }
            else {
                startDate = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
                endDate = $filter('date')(getNextDay(new Date(), displayDays), 'yyyy-MM-dd');
            }
            var params = {
                districtId: $stateParams.districtId,
                subjectId: $stateParams.subjectId,
                major: $scope.major,
                startDate: startDate,
                endDate: endDate
            };
            $http.get('/schedule/doctors', {params: params}).success(function (data) {
                $scope.doctors = data;
                var id;
                for (var i = 0; i < data.length; i++) {
                    $scope.doctors[i].district = $scope.doctors[i].district.substring(0,2);
                    id = data[i].districtId;
                    if (i > 0) {
                        if (data[i].districtId !== data[i - 1].districtId) {
                            districtCount++;
                            $scope.districtColor.set(id, color[districtCount - 1]);
                        }
                    } else {
                        districtCount = 1;
                        $scope.districtColor.set(id, color[districtCount - 1]);
                    }
                    getDoctorPhoto(data[i].id, i);
                }
            }).error(function (data) {
                $cordovaToast.showShortBottom(data);
            });
        };

        //医生方式选择事件
        $scope.districtClk = function (id) {
            $scope.districtId = id;
            if (id === '1') {
                $scope.dataPicker.isShow = true;
                if (!angular.isUndefined($scope.daySelectedTmp) && $scope.daySelectedTmp !== '') {
                    $scope.daySelected = $scope.daySelectedTmp;
                }
                else {
                    $scope.daySelected = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
                }
                getDoctors();
            }
            if (id === '2') {
                $scope.dataPicker.isShow = false;
                $scope.daySelectedTmp = $scope.daySelected;
                $scope.daySelected = '';
                getDoctors();
            }
        };

        //设置可选择的日期
        var setSelectDay = function (date) {
            return {
                date: $filter('date')(date, 'yyyy-MM-dd'),
                month: date.getMonth() + 1,
                day: date.getDate(),
                week: weekStr[date.getDay()]
            };
        };

        //初始化可选择日期
        var selectDayInit = function (date) {
            var selectDays = [];
            for (var i = 0; i < displayDays; i++) {
                if (i === 0) {
                    selectDays[i] = setSelectDay(date);
                }
                else {
                    if (date.getTime() > getNextDay(new Date(), displayDays-1).getTime()) {
                        break;
                    }
                    date = getNextDay(new Date(selectDays[i - 1].date), 1);
                    selectDays[i] = setSelectDay(date);
                }
            }
            if (selectDays.length < displayDays) {
                var selectDayTmps = [];
                for (var j = 0; j < displayDays - selectDays.length; j++) {
                    selectDayTmps[j] = setSelectDay(getNextDay(new Date(selectDays[0].date), selectDays.length - displayDays + j));
                }
                var m = 0;
                for (var n = displayDays - selectDays.length; n < displayDays; n++) {
                    selectDayTmps[n] = selectDays[m];
                    m++;
                }
                selectDays = selectDayTmps;
            }
            return selectDays;
        };
        $scope.selectDays = selectDayInit(getNextDay(new Date(), 1));
        $scope.$on('$ionicView.beforeEnter', function () {
            //默认选中明天
            $scope.daySelected = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
            getDoctors();
            $scope.leftIconIsShow = false;
            $scope.rightIconIsShow = true;
        });

        //当前滑动应该显示的月份
        var currentMonth = $scope.selectDays[0].month;
        var currentDay = $scope.selectDays[0].day;
        var allDays;
        var remainDays;
        var remainDaysWidth;
        var scrollMonth = function () {
            if(currentMonth === 1 || currentMonth === 3 || currentMonth === 5 || currentMonth === 7 || currentMonth === 8 || currentMonth === 10 || currentMonth === 12){
                allDays = 31;
                remainDays = allDays - currentDay;
            }else if(currentDay === 2){
                allDays = 28;
                remainDays = allDays - currentDay;
            }else{
                allDays = 30;
                remainDays = allDays - currentDay;
            }
            remainDaysWidth = remainDays*51;
            if ($ionicScrollDelegate.getScrollPosition().left > remainDaysWidth) {
                if($scope.selectDays[0].month === currentMonth){
                    $scope.selectDays[0].month = currentMonth+1;
                }
            } else {
                $scope.selectDays[0].month = currentMonth;
            }
        };


        //日期选择事件
        $scope.dayClk = function (index, date) {
            $scope.daySelected = date;
            $scope.selectDays[0].month = parseInt(date.substring(5,7));
            getDoctors();
        };

        //查询框显示隐藏事件
        $scope.searchClk = function () {
            $scope.hideSearch = !$scope.hideSearch;
            if (!$scope.hideSearch) {
                $timeout(function () {
                    document.getElementById('registerDoctorDateSelect_search').focus();
                }, 50);
            }
        };
        //查询事件
        $scope.doSearch = function () {
            $scope.hasSearchStr = ($scope.major !== '');
            getDoctors();
        };

        //选择照片事件
        $scope.photoClk = function (id, event) {
            event.stopPropagation();
            $state.go('doctorIntroductionView', {doctorId: id, type: '0'});
        };

        //医生选中事件
        $scope.doctorClk = function (doctorId, overCount) {
            if (overCount > 0) {
                //判断如果是选择医生状态，则置选择日期为空
                if (!$scope.dataPicker.isShow || $scope.hasSearchStr) {
                    $scope.daySelected = '';
                }
                $state.go('registerDoctorTimeSelect', {doctorId: doctorId, date: $scope.daySelected, type: '2'});
            }
        };

        $scope.contentMarginTop = function() {
          if (!$scope.hasSearchStr) {
            if ($scope.dataPicker.isShow) {
              return 'registerDoctorDateSelect-top1';
            }
            else {
              return 'registerDoctorDateSelect-top2';
            }
          }
          else {
            return '';
          }
        };
    };

    var mainRouter = function ($stateProvider) {
        $stateProvider.state('registerDoctorDateSelect', {
            url: '/register/registerDoctorDateSelect/:districtId/:subjectId/:major',
            templateUrl: 'modules/register/registerDoctorDateSelect.html',
            controller: registerDoctorDateSelectCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));
