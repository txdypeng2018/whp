'use strict';

(function(app) {

    app.directive('textareaAutoHeight', function() {
        function autoHeight(elem) {
            if(elem.scrollHeight < 46){
                elem.style.height = 34 + 'px';
                elem.parentNode.style.height = 58 + 'px';
                elem.parentNode.style.minHeight = 58 + 'px';
            }
            if(elem.scrollHeight < 108 && elem.scrollHeight > 46){
                elem.style.height = 'auto';
                elem.style.height = elem.scrollHeight + 'px';
                elem.parentNode.style.height = elem.scrollHeight + 12 + 'px';
                elem.parentNode.style.minHeight = elem.scrollHeight + 12 + 'px';
            }
        }
        return {
            restrict: 'A',
            scope: {},
            link: function (scope, ele) {
                var oriEle = ele[0];
                ele.on('keyup click keydown', function() {
                    autoHeight(oriEle);
                });
            }
        };
    });

})(angular.module('pea'));
