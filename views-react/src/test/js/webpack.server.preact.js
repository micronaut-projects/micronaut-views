const base = require("./webpack.server")

module.exports = {
    ...base,
    entry: [ './server.preact.js' ],
    output: {
        ...base.output,
        filename: 'ssr-components.preact.mjs'
    },
    resolve: {
        alias: {
            "react": "preact/compat",
            "react-dom/test-utils": "preact/test-utils",
            "react-dom": "preact/compat",     // Must be below test-utils
            "react/jsx-runtime": "preact/jsx-runtime"
        },
    }
}
