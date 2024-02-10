import {getQuery, reqVersions} from "./backend"
import {addAlert} from "./alerts"
import {applicableDependencyVersions, useDependencySearchStore} from "./dependency-store"
import {DependencyType} from "./dep-format"
import {defineStore} from "pinia"
import {LocationQuery} from "vue-router"

export interface VersionEntry {
    version: string,
    stable: boolean,
    blocks: DependencyBlocks,
}

export interface DependencyEntry {
    name: string,
    type: DependencyType,
    notation: string,
    version: string,
}

export interface MavenInfo {
    subtitle?: string,
    url: string,
}

export interface DependencyBlockData {
    mavens: MavenInfo[],
    dependencies: DependencyEntry[],
}

export type DependencyBlocks = { [name: string]: DependencyBlockData }

export interface DependencySearchData {
    versions: { [loader: string]: VersionEntry[]; }
}

export interface State {
    reqVersionsPromise: Promise<any> | undefined,
    searchData: DependencySearchData,
    hasFirstLoaded: boolean,
}

function newState(): State {
    return {
        reqVersionsPromise: undefined,
        searchData: {
            versions: {},
        },
        hasFirstLoaded: false,
    }
}

export const useDependenciesDataStore = defineStore({
    id: "dependencies-data",
    state: newState,
})

export function updateDependencyData(fullPath?: string, query: LocationQuery | null = null) {
    let store = useDependenciesDataStore()
    if (query || (Object.keys(store.searchData.versions).length == 0 && !store.reqVersionsPromise)) {
        store.reqVersionsPromise = reqVersions().then(value => {
            store.searchData.versions = value.data
            ensureDependencyData(fullPath, query)
        }).catch(reason => {
            addAlert({
                type: "error",
                message: `Failed to fetch versions: ${reason.message}`,
            })
        }).finally(() => {
            store.reqVersionsPromise = undefined
        })
    }
}

export function ensureDependencyData(fullPath?: string, query: LocationQuery | null = null) {
    let store = useDependenciesDataStore()

    if (query) {
        let {searchData} = useDependenciesDataStore()
        let loaders = Object.keys(searchData.versions)

        if (loaders.includes(getQuery(query, "loader") ?? "")) {
            useDependencySearchStore().loader = (getQuery(query, "loader") ?? "") as string
            useDependencySearchStore().version = undefined
        }

        if (applicableDependencyVersions().includes(getQuery(query, "version") ?? "")) {
            useDependencySearchStore().version = (getQuery(query, "version") ?? "") as string
        }

        useDependenciesDataStore().hasFirstLoaded = true
    }

    let {loader, version, allowSnapshots} = useDependencySearchStore()
    if (!loader) {
        loader = Object.keys(store.searchData.versions)[0]
        useDependencySearchStore().loader = loader
    }
    let applicable_versions = store.searchData.versions[loader!!]
    if (applicable_versions) {
        if (!allowSnapshots) {
            applicable_versions = applicable_versions.filter(entry => entry.stable)
        }
        if (!version || !applicable_versions.find(entry => entry.version === version)) {
            version = applicable_versions.find(entry => entry.stable)?.version
            useDependencySearchStore().version = version
        }
    }

    if (useDependenciesDataStore().hasFirstLoaded && fullPath) {
        updateDependenciesWindowUrl(fullPath)
    }
}

export function updateDependenciesWindowUrl(fullPath: string) {
    let {loader, version} = useDependencySearchStore()
    let url = new URL(fullPath)
    url.searchParams.set("loader", loader ?? "")
    url.searchParams.set("version", version ?? "")
    window.history.replaceState({}, "", url.toString())
}
