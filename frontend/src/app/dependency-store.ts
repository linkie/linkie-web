import {defineStore} from "pinia"

interface State {
    loader?: string,
    version?: string,
    allowSnapshots: boolean,
}

function newState(): State {
    return {
        allowSnapshots: false,
    }
}

export const useDependencySearchStore = defineStore({
    id: "dependency_search",
    state: newState,
    actions: {
        setLoader(modLoader: string | undefined) {
            this.loader = modLoader
        },
        setVersion(version: string | undefined) {
            this.version = version
        },
        setAllowSnapshots(allowSnapshots: boolean) {
            this.allowSnapshots = allowSnapshots
        },
    },
    persist: true,
})
