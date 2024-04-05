async function renderWithReact(component, props, callback, config) {
    globalThis.__micronaut_prefetch = callback.recordPrefetch;
    const element = React.createElement(component, props, null);

    var stream;
    do {
        // Data to be passed to the browser after the main HTML has finished loading.
        const boot = {
            rootProps: props,
            rootComponent: component.name,
            prefetch: callback.getPrefetchedData()
        };

        var bootstrapScriptContent = `var __micronaut_boot = ${JSON.stringify(boot)};`;
        stream = await ReactDOMServer.renderToReadableStream(element, {
            bootstrapScriptContent: bootstrapScriptContent,
            bootstrapScripts: [config.getClientBundleURL()]
        });
    } while (callback.didPrefetch());

    // This ugliness is because renderToPipeableStream (what we should really use) is only in the node build
    // of react-dom/server, but we use the browser build. Trying to use the node build causes various errors
    // and problems that I don't yet understand, something to do with module formats.
    const reader = stream.getReader();
    while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        callback.write(value);
    }
}

async function renderWithPreact(component, props, callback, config) {
    globalThis.__micronaut_prefetch = callback.recordPrefetch;
    const html = renderToString(h(component, props, null))
    callback.write(html)
    const boot = {
        rootProps: props,
        rootComponent: component.name,
        prefetch: callback.getPrefetchedData()
    };
    const bootstrapScriptContent = `var __micronaut_boot = ${JSON.stringify(boot)};`;
    callback.write(`<script type="text/javascript">${bootstrapScriptContent}</script>`)
    callback.write(`<script type="text/javascript" src="${config.getClientBundleURL()}" async="true">`)
}

async function ssr(component, props, callback, config) {
    if (typeof ReactDOMServer !== 'undefined')
        return renderWithReact(component, props, callback, config);
    else
        return renderWithPreact(component, props, callback, config);
}

export { ssr };
