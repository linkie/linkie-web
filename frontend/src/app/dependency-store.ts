import {defineStore} from "pinia"
import {useDependenciesDataStore} from "./dependencies-data"

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
