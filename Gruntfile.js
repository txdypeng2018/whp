'use strict';

module.exports = function(grunt) {

  // Automatically load required Grunt tasks
  require('jit-grunt')(grunt);

  // Configurable paths for the application
  var appConfig = {
    proxy: 'proxy'
  };

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    app: appConfig,

    // Watches files for changes and runs tasks based on the changed files
    watch: {
      proxyserver: {
        files: ['<%= app.proxy %>/**/*.js'],
        tasks: ['shell:stop', 'shell:start']
      }
    },

    shell: {
      stop: {
        command: 'pm2 stop all -s'
      },
      start: {
        command: 'pm2 start proxy/proxy-webservices.js -s'
      }
    }

  });

  grunt.registerTask('default', [
    'shell:stop',
    'shell:start',
    'watch'
  ]);
};
