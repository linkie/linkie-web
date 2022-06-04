import {defineStore} from "pinia"

interface State {
    loader?: string,
    version?: string,
    allowSnapshots: boolean,
    forgeGradle: boolean,
}

function newState(): State {
    return {
        allowSnapshots: false,
        forgeGradle: false,
    }
}

export const useDependencySearchStore = defineStore({
    id: "dependency_search",
    state: newState,
    persist: true,
})
