import {defineStore} from "pinia"

export interface State {
    locale: String,
}

const getFirstBrowserLanguage = function () {
    let nav = window.navigator,
        browserLanguagePropertyKeys = ["language", "browserLanguage", "systemLanguage", "userLanguage"],
        i,
        language

    // support for HTML 5.1 "navigator.languages"
    if (Array.isArray(nav.languages)) {
        for (i = 0; i < nav.languages.length; i++) {
            language = nav.languages[i]
            if (language && language.length) {
                return language
            }
        }
    }

    // support for other well known properties in browsers
    for (i = 0; i < browserLanguagePropertyKeys.length; i++) {
        language = (nav as any)[browserLanguagePropertyKeys[i]]
        if (language && language.length) {
            return language
        }
    }

    return null
}

const defaultLocale = function() {
    let locale = getFirstBrowserLanguage()
    if (locale) {
        locale = (locale as String).toLowerCase()
        if (locale === "en-gb" || locale === "en-au" || locale === "en-ca" || locale === "en-ie" || locale === "en-nz" || locale === "en-za") {
            return "en_GB"
        } else if (locale.startsWith("en")) {
            return "en_US"
        } else if (locale === "zh-hk" || locale === "zh-tw") {
            return "zh_TW"
        } else if (locale.startsWith("zh")) {
            return "zh_CN"
        }
    }
    
    return "en_US"
}

function newState(): State {
    return {
        locale: defaultLocale(),
    }
}

export const useI18nStore = defineStore({
    id: "i18n",
    state: newState,
    persist: true,
})
