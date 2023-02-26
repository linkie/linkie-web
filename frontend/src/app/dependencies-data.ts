import {reqVersions} from "./backend"
import {addAlert} from "./alerts"
import {applicableDependencyVersions, useDependencySearchStore} from "./dependency-store"
import {DependencyType} from "./dep-format"
import {defineStore} from "pinia"

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

export function updateDependencyData(applyURL: boolean = false) {
    let store = useDependenciesDataStore()
    if (applyURL || (Object.keys(store.searchData.versions).length == 0 && !store.reqVersionsPromise)) {
        store.reqVersionsPromise = reqVersions().then(value => {
            store.searchData.versions = value.data
            ensureDependencyData(applyURL)
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

export function ensureDependencyData(applyURL: boolean = false) {
    let store = useDependenciesDataStore()

    if (applyURL) {
        const urlParams = new URLSearchParams(window.location.search)
        let {searchData} = useDependenciesDataStore()
        let loaders = Object.keys(searchData.versions)

        if (loaders.includes(urlParams.get("loader") ?? "")) {
            useDependencySearchStore().loader = (urlParams.get("loader") ?? "") as string
            useDependencySearchStore().version = undefined
        }

        if (applicableDependencyVersions().includes(urlParams.get("version") ?? "")) {
            useDependencySearchStore().version = (urlParams.get("version") ?? "") as string
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

    if (useDependenciesDataStore().hasFirstLoaded) {
        updateDependenciesWindowUrl()
    }
}

export function updateDependenciesWindowUrl() {
    let {loader, version} = useDependencySearchStore()
    let url = new URL(window.location.href)
    url.searchParams.set("loader", loader ?? "")
    url.searchParams.set("version", version ?? "")
    window.history.replaceState({}, "", url.toString())
}
