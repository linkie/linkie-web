import {createApp} from "vue"
import {createI18n} from "vue-i18n"
import axios from "axios"
import VueAxios from "vue-axios"
import App from "./App.vue"
import "./index.css"
import "nprogress/nprogress.css"
// @ts-ignore
import NProgress from "nprogress"
import {HTTP} from "./app/backend"
import {createPinia} from "pinia"
import persistedState from "pinia-plugin-persistedstate"
// @ts-ignore
import enUS from "./locales/en_US.json"
// @ts-ignore
import enGB from "./locales/en_GB.json"
// @ts-ignore
import zhCN from "./locales/zh_CN.json"
// @ts-ignore
import zhTW from "./locales/zh_TW.json"
import {isTauri} from "./app/tauri/tauri";
// @ts-ignore

Prism.manual = true;

NProgress.configure({
    showSpinner: false,
})

const app = createApp(App)

HTTP.interceptors.request.use(config => {
    if (!isTauri()) {
        NProgress.start()
    }
    return config
}, error => {
    console.log(error)
    return Promise.reject(error)
})

HTTP.interceptors.response.use(response => {
    if (!isTauri()) {
        NProgress.done()
    }
    return response
}, error => {
    if (!axios.isCancel(error)) {
        console.log(error)
    }
    return Promise.reject(error)
})

const i18n = createI18n({
    locale: "en_US",
    fallbackLocale: "en_US",
    messages: {
        "en_US": enUS,
        "en_GB": enGB,
        "zh_CN": zhCN,
        "zh_TW": zhTW,
    },
})

let pinia = createPinia()

pinia.use(persistedState)

app.use(pinia)
app.use(VueAxios, axios)
app.provide("axios", app.config.globalProperties.axios)

app.use(i18n)

app.mount("#app")
