export function ssr(component, props, callback, clientBundleURL) {
    globalThis.Micronaut = {
        prefetch: callback.recordPrefetch
    };
    const html = renderToString(preact.h(component, props, null))
    callback.write(html)
    const boot = {
        rootProps: props,
        rootComponent: component.name,
        prefetch: callback.getPrefetchedData()
    };

    // The Micronaut object defined here is not the same as the Micronaut object defined server side.
    callback.write(`<script type="text/javascript">var Micronaut = ${JSON.stringify(boot)};</script>`)
    callback.write(`<script type="text/javascript" src="${clientBundleURL}" async="true">`)
}
