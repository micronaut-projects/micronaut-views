import {h, render} from 'preact'

const pageComponentName = Micronaut.rootComponent;

import(`./components/${pageComponentName}.js`).then(module => {
    const PageComponent = module[pageComponentName]
    render(h(PageComponent, Micronaut.rootProps, null), document)
})
