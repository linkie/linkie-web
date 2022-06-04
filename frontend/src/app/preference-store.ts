import {defineStore} from "pinia"

export const languages = ["groovy", "kotlin"] as const
export type CodeLanguage = typeof languages[number]

export interface State {
    codeLanguage: CodeLanguage,
}

function newState(): State {
    return {
        codeLanguage: "groovy",
    }
}

export const usePreferenceStore = defineStore({
    id: "preference",
    state: newState,
    persist: true,
})
