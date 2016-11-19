'use strict';

(function(app) {

  /**
   * Workflow Resource Factory
   * Decorate Activiti RESTFul API
   */
  app.service('wfrFactory', function($resource) {
    var composeResource = function (url, paramDefaults, actions) {
      return $resource('/workflow/service' + url, paramDefaults, actions);
    };

    var composeModelResource = function (suffix, actions) {
      return composeResource('/repository/models/:modelId' + suffix, {modelId: '@id'}, actions);
    };

    this.model = {
      model: function () {
        return composeModelResource('', {
          query: {method: 'GET', params: {sort: 'lastUpdateTime', order: 'desc'}},
          update: {method: 'PUT'}
        });
      },
      source: function () {
        return composeModelResource('/source', {
          init: {method: 'POST'}
        });
      },
      deployment: function () {
        return composeModelResource('/deployment', {});
      },
      exportUrl: function (modelId) {
        return '/workflow/service/repository/models/' + modelId + '/export';
      }
    };

    var composeDeploymentResource = function (suffix, actions) {
      return composeResource('/repository/deployments/:deploymentId' + suffix, {deploymentId: '@id'}, actions);
    };

    this.deployment = {
      deployment: function () {
        return composeDeploymentResource('', {
          query: {method: 'GET', params: {sort: 'deployTime', order: 'desc'}}
        });
      }
    };

    var composeProcDefResource = function (suffix, actions) {
      return composeResource('/repository/process-definitions/:procDefId' + suffix, {procDefId: '@id'}, actions);
    };

    this.procDef = {
      self: function () {
        return composeProcDefResource('', {
          query: {method: 'GET', params: {sort: 'version', order: 'desc'}}
        });
      },
      diagram: function() {
        return composeProcDefResource('/diagram-size', {});
      }
    };

    this.procInst = {
      self: function() {
        return composeResource('/runtime/process-instances/:procInstId', {procInstId: '@id'}, {
          query: {method: 'GET'},
          start: {method: 'POST'}
        });
      }
    };

  });

})(angular.module('pea'));
