import {createApp} from "vue"
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
import {registerSW} from "virtual:pwa-register"

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
    console.log(error)
    return Promise.reject(error)
})

let pinia = createPinia()

pinia.use(persistedState)

app.use(pinia)
app.use(VueAxios, axios)
app.provide("axios", app.config.globalProperties.axios)

app.mount("#app")

const updateSW = registerSW({
    onNeedRefresh() {
    },
    onOfflineReady() {
    },
})
