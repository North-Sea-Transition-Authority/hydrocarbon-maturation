const sass = require('gulp-sass')(require('sass'));
const postcss = require('gulp-postcss');
const gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const autoprefixer = require('autoprefixer');
const rename = require("gulp-rename");

const rollup = require('rollup');
const babel = require('rollup-plugin-babel');
const resolve = require('@rollup/plugin-node-resolve');
const commonjs = require('rollup-plugin-commonjs');
const nodeBuiltins = require('rollup-plugin-node-builtins');

const sassOptions = {
  outputStyle: 'compressed',
  includePath: 'src/main/resources/scss'
};

function compileSass(exitOnError) {
  const plugins = [
    autoprefixer({
      browsers: ['last 3 versions', '> 0.1%', 'Firefox ESR'],
      grid: true
    })
  ];

  let sassTask = sass(sassOptions);
  if (!exitOnError) {
    //Without an error handler specified, the task will exit on error, which we want for the "buildAll" task
    sassTask = sassTask.on('error', sass.logError);
  }

  return gulp.src('src/main/resources/scss/*.scss', {base: "."})
    .pipe(sourcemaps.init())
    .pipe(sassTask)
    .pipe(postcss(plugins))
    .pipe(sourcemaps.write())
    .pipe(rename(path => {
      //E.g. src\main\resources\sass\core -> src\main\resources\public\assets\static\css
      path.dirname = path.dirname.replace(/([\/\\])scss[\/\\]?/, '$1public$1assets$1static$1css');
    }))
    .pipe(gulp.dest('./'));
}

const babelOptions =
  {
    "exclude": "node_modules/**",
    "presets": [
      ["@babel/preset-env", {
        "useBuiltIns": "usage",
        "corejs": "3",
        "debug": false,
        "targets": {
          "chrome": 65,
          "ie": 11,
          "firefox": 52,
          "safari": 11,
          "ios": 12,
          "edge": 16
        }
      }]
    ],
    "plugins": ["@babel/plugin-transform-classes"],
  }

// We create a separate bundle for OpenLayers and other GIS content as they do not work in IE.
// Inclusion in the primary bundle would break the entire bundle for IE.
gulp.task('rollup-babel-gis', () =>
  rollup.rollup({
    input: './src/main/resources/js/gis/allGis.js',
    plugins: [
      commonjs(),
      babel(babelOptions),

      // OpenLayers (specifically geotiff) wants to reference some node builtins such as fs and http. We shouldn't need this though as geotiff has both browser and node version.
      // Ideally, the browser version should be picked up with resolve({browser: true}), but this leads to various import errors within OL/Geotiff.
      // Although not optimal, including node builtins in this bundle resolves this problem
      nodeBuiltins(),
      resolve({preferBuiltins: true})
    ]
  }).then(bundle =>
    bundle.write({
      file: './src/main/resources/public/assets/static/js/remi-gis-bundle.js',
      format: "iife",
      sourcemap: true
    })
  )
);

// copy FDS into public/assets
gulp.task('copyFdsResources', () => {
  return gulp.src(['fivium-design-system-core/fds/**/*'])
    .pipe(gulp.dest('src/main/resources/templates/fds'));
});

// copy FDS images into public/assets
gulp.task('copyFdsImages', () => {
    return gulp.src(['fivium-design-system-core/fds/static/images/**/*'])
        .pipe(gulp.dest('src/main/resources/public/assets/static/fds/images'));
});

// copy govuk-frontend into public/assets
gulp.task('copyGovukResources', () => {
  return gulp.src(['fivium-design-system-core/node_modules/govuk-frontend/**/*'])
    .pipe(gulp.dest('src/main/resources/public/assets/govuk-frontend'));
});

// copy FDS bundle into public/assets
gulp.task('copyJs', () => {
  return gulp.src(['src/main/resources/templates/fds/static/js/**/*'])
    .pipe(gulp.dest('src/main/resources/public/assets/static/fds/js'));
});

// copy vendor JS into public/assets
gulp.task('copyVendorJs', () => {
  return gulp.src(['src/main/resources/templates/fds/vendor/**/*'])
    .pipe(gulp.dest('src/main/resources/public/assets/static/js/vendor'))
});

// copy vendor CSS
gulp.task('copyVendorCss', () => {
  return gulp.src(['node_modules/ol/ol.css'])
    .pipe(gulp.dest('src/main/resources/public/assets/static/css'));
});

// copy govuk-frontend dependency into public/assets
gulp.task('copyHtml5Shiv', () => {
  return gulp.src(['fivium-design-system-core/node_modules/html5shiv/dist/html5shiv.min.js'])
    .pipe(gulp.dest('src/main/resources/public/assets/html5shiv'))
});

// Init all appropriate resources into project's public/assets
gulp.task('initFds', gulp.series(['copyFdsResources', 'copyFdsImages', 'copyGovukResources', 'copyHtml5Shiv', 'copyJs', 'copyVendorJs', 'copyVendorCss']))

gulp.task('sassCi', gulp.series(['initFds'], () => {
  return compileSass(true);
}));

gulp.task('buildAll', gulp.series(['sassCi', 'rollup-babel-gis']));