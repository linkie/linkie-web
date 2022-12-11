import {reqVersions} from "./backend"
import {addAlert} from "./alerts"
import {useDependencySearchStore} from "./dependency-store"
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
}

function newState(): State {
    return {
        reqVersionsPromise: undefined,
        searchData: {
            versions: {},
        },
    }
}

export const useDependenciesDataStore = defineStore({
    id: "dependencies-data",
    state: newState
})

export function updateDependencyData() {
    let store = useDependenciesDataStore()
    if (Object.keys(store.searchData.versions).length == 0 && !store.reqVersionsPromise) {
        store.reqVersionsPromise = reqVersions().then(value => {
            store.searchData.versions = value.data
            ensureDependencyData()
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

export function ensureDependencyData() {
    let store = useDependenciesDataStore()
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
}
