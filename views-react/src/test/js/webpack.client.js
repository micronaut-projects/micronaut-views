const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: ['./client.js'],
    devtool: false,
    output: {
        path: path.resolve(__dirname, '../resources/views/static'),
        filename: 'client.js',
    },
    plugins: [
        new webpack.DefinePlugin({
            SERVER: false,
        })
    ],
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                }
            }
        ]
    }
};
