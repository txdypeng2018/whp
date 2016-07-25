'use strict';

module.exports = function(grunt) {

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Automatically load required Grunt tasks
  require('jit-grunt')(grunt, {
    configureProxies: 'grunt-connect-proxy',
    ngtemplates: 'grunt-angular-templates',
    useminPrepare: 'grunt-usemin'
  });

  // Configurable paths for the application
  var appConfig = {
    src: 'src',
    test: 'test',
    dist: 'dist',
    proxy: 'proxy'
  };

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    app: appConfig,

    // Watches files for changes and runs tasks based on the changed files
    watch: {
      bower: {
        files: ['bower.json'],
        tasks: ['wiredep']
      },
      js: {
        files: [
          '<%= app.src %>/**/*.js'
        ],
        tasks: ['newer:jshint:all', 'injector:server'],
        options: {
          livereload: '<%= connect.options.livereload %>'
        }
      },
      compass: {
        files: ['<%= app.src %>/{app,modules}/**/*.{scss,sass}'],
        tasks: ['compass:server', 'autoprefixer:server', 'injector:server']
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        options: {
          livereload: '<%= connect.options.livereload %>'
        },
        files: [
          '.tmp/styles/**/*.css',
          '<%= app.src %>/**/*.html',
          '<%= app.src %>/**/*.css',
          '<%= app.src %>/images/**/*.{pgn,jpg,jpeg,gif,webp,svg}'
        ]
      },
      proxyserver: {
        files: ['<%= app.proxy %>/**/*.js'],
        tasks: ['shell:stop', 'shell:start', 'newer:jshint:all']
      }
    },

    // The actual grunt server settings
    connect: {
      options: {
        port: 9000,
        // Change this to '0.0.0.0' to access the server from outside.
        hostname: 'localhost',
        livereload: 35729
      },
      proxies: [
        {
          context: '/api',
          host: 'localhost',
          port: 9090,
          rewrite: {
            '^/api': '/pep'
          }
        },
        {
          context: '/workflow',
          host: 'localhost',
          port: 9090,
          rewrite: {
            '^/workflow': '/pep/workflow'
          }
        },
        {
          context: '/pep',
          host: 'localhost',
          port: 9090,
          rewrite: {
            '^/pep/workflow/service': '/pep/workflow/service'
          }
        }
      ],
      livereload: {
        options: {
          open: true,
          base: [
            '.tmp',
            '<%= app.src %>'
          ],
          middleware: function (connect, options) {
            // Setup the proxy
            var middlewares = [require('grunt-connect-proxy/lib/utils').proxyRequest];

            // Serve static files
            options.base.forEach(function(base) {
              middlewares.push(connect.static(base));
            });
            middlewares.push(connect().use('/bower_components', connect.static('./bower_components')));
            middlewares.push(connect().use('/styles/fonts', connect.static('./src/assets/fonts')));
            middlewares.push(connect.static(appConfig.src));

            return middlewares;
          }
        }
      },
      dist: {
        options: {
          open: true,
          base: '<%= app.dist %>',
          hostname: '0.0.0.0',
          livereload: false,
          middleware: function (connect, options) {
            // Setup the proxy
            var middlewares = [require('grunt-connect-proxy/lib/utils').proxyRequest];

            // Serve static files
            options.base.forEach(function(base) {
              middlewares.push(connect.static(base));
            });
            middlewares.push(connect.static(appConfig.dist));

            return middlewares;
          }
        }
      }
    },

    // Make sure code styles are up to par and there are no obvious mistakes
    jshint: {
      options: {
        jshintrc: '.jshintrc',
        reporter: require('jshint-stylish')
      },
      all: {
        src: [
          'Gruntfile.js',
          '<%= app.src %>/**/*.js',
          '<%= app.proxy %>/**/*.js'
        ]
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= app.dist %>/**/*'
          ]
        }]
      },
      server: '.tmp'
    },

    // Add vendor prefixed styles
    autoprefixer: {
      options: {
        browsers: ['last 1 version']
      },
      server: {
        options: {
          map: true
        },
        files: [{
          expand: true,
          cwd: '.tmp/styles/',
          src: '{,**/}*.css',
          dest: '.tmp/styles/'
        }]
      },
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/styles/',
          src: '{,**/}*.css',
          dest: '.tmp/styles/'
        }]
      }
    },

    // Automatically inject Bower components into the app
    wiredep: {
      app: {
        src: ['<%= app.src %>/index.html'],
        ignorePath:  /\.\.\//
      },
      sass: {
        src: ['<%= app.src %>/**/*.{scss,sass}'],
        ignorePath: /(\.\.\/){1,2}bower_components\//
      }
    },

    // Compiles Sass to CSS and generates necessary files if requested
    compass: {
      options: {
        sassDir: '<%= app.src %>',
        cssDir: '.tmp/styles',
        generatedImagesDir: '.tmp/images/generated',
        imagesDir: '<%= app.src %>/assets/images',
        fontsDir: '<%= app.src %>/assets/fonts',
        importPath: './bower_components',
        httpImagesPath: '/images',
        httpGeneratedImagesPath: '/images/generated',
        httpFontsPath: '/styles/fonts',
        relativeAssets: false,
        assetCacheBuster: false,
        raw: 'Sass::Script::Number.precision = 10\n'
      },
      dist: {
        options: {
          generatedImagesDir: '<%= app.dist %>/images/generated'
        }
      },
      server: {
        options: {
          sourcemap: true
        }
      }
    },

    // Renames files for browser caching purposes
    filerev: {
      dist: {
        src: [
          '<%= app.dist %>/scripts/**/*.js',
          '<%= app.dist %>/styles/**/*.css',
          '<%= app.dist %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
          '<%= app.dist %>/styles/fonts/*'
        ]
      }
    },

    injector: {
      options: {
        template: '<%= app.src %>/index.html'
      },
      server: {
        options: {
          ignorePath: ['src', '.tmp']
        },
        files: {
          '<%= app.src %>/index.html': ['<%= app.src %>/**/*.js', '.tmp/styles/**/*.css']
        }
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      html: '<%= app.src %>/index.html',
      options: {
        dest: '<%= app.dist %>',
        flow: {
          html: {
            steps: {
              js: ['concat', 'uglify'],
              css: ['cssmin']
            },
            post: {}
          }
        }
      }
    },

    // Performs rewrites based on filerev and the useminPrepare configuration
    usemin: {
      html: ['<%= app.dist %>/{,*/}*.html'],
      css: ['<%= app.dist %>/styles/{,*/}*.css'],
      js: ['<%= app.dist %>/scripts/{,*/}*.js'],
      options: {
        assetsDirs: [
          '<%= app.dist %>',
          '<%= app.dist %>/images',
          '<%= app.dist %>/styles'
        ],
        patterns: {
          js: [[/(images\/[^''""]*\.(png|jpg|jpeg|gif|webp|svg))/g, 'Replacing references to images']]
        }
      }
    },

    // imagemin: {
    //   dist: {
    //     files: [{
    //       expand: true,
    //       cwd: '<%= app.src %>/assets/images',
    //       src: '{,*/}*.{png,jpg,jpeg,gif}',
    //       dest: '<%= app.dist %>/images'
    //     }]
    //   }
    // },

    svgmin: {
      dist: {
        files: [{
          expand: true,
          cwd: '<%= app.src %>/assets/images',
          src: '{,*/}*.svg',
          dest: '<%= app.dist %>/images'
        }]
      }
    },

    htmlmin: {
      dist: {
        options: {
          collapseWhitespace: true,
          conservativeCollapse: true,
          collapseBooleanAttributes: true,
          removeCommentsFromCDATA: true
        },
        files: [{
          expand: true,
          cwd: '<%= app.dist %>',
          src: ['*.html'],
          dest: '<%= app.dist %>'
        }]
      }
    },

    ngtemplates: {
      dist: {
        options: {
          module: 'isj',
          htmlmin: '<%= htmlmin.dist.options %>',
          usemin: 'scripts/scripts.js'
        },
        cwd: '<%= app.src %>',
        src: '{app,modules}/{,**/}*.html',
        dest: '.tmp/templateCache.js'
      }
    },

    // ng-annotate tries to make the code safe for minification automatically
    // by using the Angular long form for dependency injection.
    ngAnnotate: {
      dist: {
        src: ['.tmp/concat/scripts/scripts.js'],
        dest: '.tmp/concat/scripts/scripts.js'
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= app.src %>',
          dest: '<%= app.dist %>',
          src: [
            '*.{ico,png,txt}',
            '*.html',
            'images/{,*/}*.{webp}',
            'styles/fonts/{,*/}*.*'
          ]
        }, {
          expand: true,
          cwd: '.tmp/images',
          dest: '<%= app.dist %>/images',
          src: ['generated/*']
        }, {
          expand: true,
          cwd: '<%= app.src %>/assets/',
          dest: '<%= app.dist %>/assets/',
          src: ['images/{,**/}*.{png,jpg,jpeg,gif}']
        }, {
          expand: true,
          cwd: '<%= app.src %>/assets/',
          dest: '<%= app.dist %>/styles/',
          src: ['fonts/{,**/}*.{eot,ttf,woff,woff2}']
        }]
      },
      styles: {
        expand: true,
        cwd: '<%= app.src %>',
        dest: '.tmp/styles/',
        src: '**/*.css'
      }
    },

    // Run some tasks in parallel to speed up the build process
    concurrent: {
      dist: [
        'compass:dist',
        // 'imagemin',
        'svgmin'
      ]
    },

    shell: {
      stop: {
        command: 'pm2 stop all -s'
      },
      start: {
        command: 'pm2 start proxy/proxy-server.js -s'
      }
    },

    concat: {
      dist: {
        src: ['<%= app.src %>/{app,modules}/**/*.js', '.tmp/*.js'],
        dest: '.tmp/concat/scripts/scripts.js'
      }
    }
  });

  grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
    if (target === 'dist') {
      return grunt.task.run(['build', 'configureProxies:server', 'connect:dist:keepalive']);
    }

    var tasks = [
      'shell:stop',
      'shell:start',
      'clean:server',
      'wiredep',
      'compass:server',
      'injector:server',
      'autoprefixer:server',
      'configureProxies:server',
      'connect:livereload',
      'watch'
    ];

    if (target === 'real') {
      tasks.splice(1, 1);
    } else if (target === 'win') {
      tasks.splice(0, 2);
    }
    grunt.task.run(tasks);
  });

  grunt.registerTask('build', [
    'clean:dist',
    'wiredep',
    'useminPrepare',
    'concurrent:dist',
    'autoprefixer:dist',
    'ngtemplates',
    'concat',
    'concat:dist',
    'ngAnnotate',
    'copy:dist',
    'cssmin',
    'uglify',
    'filerev',
    'usemin',
    'htmlmin'
  ]);

  grunt.registerTask('default', [
    'newer:jshint',
    'build'
  ]);
};
