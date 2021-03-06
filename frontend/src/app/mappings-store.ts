import {defineStore} from "pinia"

interface State {
    namespace?: string,
    version?: string,
    allowSnapshots: boolean,
    allowClasses: boolean,
    allowFields: boolean,
    allowMethods: boolean,
    searchText: string,
    translateAs?: string,
}

function newState(): State {
    return {
        namespace: undefined,
        version: undefined,
        allowSnapshots: false,
        allowClasses: true,
        allowFields: true,
        allowMethods: true,
        searchText: "",
        translateAs: undefined,
    }
}

export const useMappingsStore = defineStore({
    id: "mappings",
    state: newState,
    actions: {
        setNamespace(namespace: string | undefined) {
            this.namespace = namespace
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
