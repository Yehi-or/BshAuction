const path = require('path');
const dotenv = require('dotenv');
dotenv.config({ path: path.join(__dirname, '.env') });

module.exports = {
  devServer: {
    port: 8200,
    proxy: {
      '/api': {
        target: 'http://localhost:8100/api',
        changeOrigin: true,
        pathRewrite: { 
          '^/api': ''
        } 
      }
    }
  },
  lintOnSave: false
};