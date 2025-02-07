const base = require("./webpack.client")

module.exports = {
    ...base,
    entry: [ './client.preact.js' ],
    output: {
        ...base.output,
        filename: 'client.preact.js'
    }
}
