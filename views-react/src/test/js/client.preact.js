import {h, render} from 'preact'

const pageComponentName = __micronaut_boot.rootComponent;

import(`./components/${pageComponentName}.js`).then(module => {
    const PageComponent = module[pageComponentName]
    render(h(PageComponent, __micronaut_boot.rootProps, null), document)
})
