import {defineStore} from "pinia"
import {useDependenciesDataStore} from "./dependencies-data"

interface State {
    loader: string | undefined,
    version: string | undefined,
    allowSnapshots: boolean,
    forgeGradle: boolean,
    neoGradle: boolean,
}

function newState(): State {
    return {
        loader: undefined,
        version: undefined,
        allowSnapshots: false,
        forgeGradle: false,
        neoGradle: false
    }
}

export const useDependencySearchStore = defineStore({
    id: "dependency_search",
    state: newState,
    persist: true,
})

export function applicableDependencyVersions(): string[] {
    let {loader, allowSnapshots} = useDependencySearchStore()
    let {searchData} = useDependenciesDataStore()

    if (!loader) return []
    let versions = searchData.versions[loader] ?? []
    if (versions && !allowSnapshots) {
        versions = versions.filter(entry => entry.stable)
    }
    return versions.map(entry => entry.version)
}
