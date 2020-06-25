const webpack = require('webpack');

module.exports = {
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
          JAVA_HOME: JSON.stringify(process.env.JAVA_HOME)
      }
    })
  ]
}
