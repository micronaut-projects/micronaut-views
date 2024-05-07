export async function ssr(component, props, callback, clientBundleURL) {
    globalThis.Micronaut = {};
    const element = React.createElement(component, props, null);

    // Data to be passed to the browser after the main HTML has finished loading.
    const boot = {
        rootProps: props,
        rootComponent: component.name,
    };

    // The Micronaut object defined here is not the same as the Micronaut object defined server side.
    const bootstrapScriptContent = `var Micronaut = ${JSON.stringify(boot)};`;
    const stream = await ReactDOMServer.renderToReadableStream(element, {
        bootstrapScriptContent: bootstrapScriptContent,
        bootstrapScripts: [clientBundleURL]
    });

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
