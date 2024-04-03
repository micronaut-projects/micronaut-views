const path = require('path');
const webpack = require('webpack');

// This targets the browser even though we run it server-side, because that's closer to GraalJS.
module.exports = {
    entry: ['web-streams-polyfill/dist/polyfill', './server.js'],
    watch: true,
    output: {
        path: path.resolve(__dirname, '../resources/views/server.js'),
        filename: 'ssr-components.mjs',
        module: true,
        library: {
            type: 'module',
        },
        // GraalJS uses `globalThis` instead of `window` for the global object.
        globalObject: 'globalThis'
    },
    devtool: false,
    experiments: {
        outputModule: true
    },
    plugins: [
        new webpack.ProvidePlugin({
            // GraalJS doesn't support TextEncoder yet. It's easy to add and here's a polyfill in the meantime.
            TextEncoder: ['text-encoding', 'TextEncoder'],
            TextDecoder: ['text-encoding', 'TextDecoder'],
        }),
        new webpack.DefinePlugin({
            SERVER: true,
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
