import {createApp} from "vue"
import axios from "axios"
import VueAxios from "vue-axios"
import App from "./App.vue"
import "./index.css"
import "nprogress/nprogress.css"
import hljs from 'highlight.js/lib/core';
import groovy from 'highlight.js/lib/languages/groovy';
import gradle from 'highlight.js/lib/languages/gradle';
import java from 'highlight.js/lib/languages/java';
import kotlin from 'highlight.js/lib/languages/kotlin';
import hljsVuePlugin from "@highlightjs/vue-plugin";
// @ts-ignore
import NProgress from "nprogress"
import {HTTP} from "./app/backend"
import {createPinia} from "pinia"
import persistedState from "pinia-plugin-persistedstate"

NProgress.configure({
    showSpinner: false,
})

const app = createApp(App)

HTTP.interceptors.request.use(config => {
    NProgress.start()
    return config
}, error => {
    console.log(error)
    return Promise.reject(error)
})

HTTP.interceptors.response.use(response => {
    NProgress.done()
    return response
}, error => {
    if (!axios.isCancel(error)) {
        console.log(error)
    }
    return Promise.reject(error)
})

hljs.registerLanguage('java', java);
hljs.registerLanguage('groovy', groovy);
hljs.registerLanguage('kotlin', kotlin);
hljs.registerLanguage('gradle', gradle);

let pinia = createPinia()

pinia.use(persistedState)

app.use(pinia)
app.use(VueAxios, axios)
app.provide("axios", app.config.globalProperties.axios)

app.use(hljsVuePlugin)

app.mount("#app")
