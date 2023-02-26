import {defineStore} from "pinia"
import {reqNamespaces, reqSearch} from "./backend"
import {addAlert} from "./alerts"
import {applicableMappingsVersions, useMappingsStore} from "./mappings-store"
import axios from "axios"
import {VersionEntry} from "./dependencies-data"

export interface Namespace {
    id: string,
    versions: VersionEntry[],
    supportsAT: boolean,
    supportsAW: boolean,
    supportsMixin: boolean,
    supportsFieldDescription: boolean,
    supportsSource?: boolean,
}

export interface MappingsData {
    namespaces: Namespace[],
    entries: string[],
}

export const mappingTypes = ["class", "field", "method"] as const
export type MappingType = typeof mappingTypes[number]

export interface MappingEntry {
    obf?: string,
    intermediary: string,
    named?: string,
    descObf?: string,
    descIntermediary?: string,
    descNamed?: string,
    ownerObf?: string,
    ownerIntermediary?: string,
    ownerNamed?: string,
    ownerObfClient?: string,
    obfClient?: string,
    descObfClient?: string,
    ownerObfServer?: string,
    obfServer?: string,
    descObfServer?: string,
    type: MappingType,
    translatedTo?: MappingEntry,
}

interface InfoData {
    namespace?: string,
    version?: string,
    allowClasses?: boolean,
    allowFields?: boolean,
    allowMethods?: boolean,
    translateAs?: string,
    query?: string,
    entries: MappingEntry[],
    fuzzy: boolean,
}

export interface State {
    mappingsData: MappingsData,
    infoData: InfoData,
    reqNamespacesPromise: Promise<any> | undefined,
    searchController: AbortController | undefined,
    hasFirstLoaded: boolean,
}

function newState(): State {
    return {
        mappingsData: {
            namespaces: [],
            entries: [],
        },
        infoData: {
            entries: [],
            fuzzy: false,
        },
        reqNamespacesPromise: undefined,
        searchController: undefined,
        hasFirstLoaded: false,
    }
}

export const useMappingsDataStore = defineStore({
    id: "mappings-data",
    state: newState,
})

export function updateMappingsData(applyURL: boolean = false) {
    let store = useMappingsDataStore()
    if (applyURL || (Object.keys(store.mappingsData.namespaces).length == 0 && !store.reqNamespacesPromise)) {
        store.reqNamespacesPromise = reqNamespaces().then(value => {
            store.mappingsData.namespaces = value.data
            ensureMappingsData(applyURL)
        }).catch(reason => {
            addAlert({
                type: "error",
                message: `Failed to fetch namespaces: ${reason.message}`,
            })
        }).finally(() => {
            store.reqNamespacesPromise = undefined
        })
    }
}

export function ensureMappingsData(applyURL: boolean = false) {
    let store = useMappingsDataStore()
    if (applyURL) {
        const urlParams = new URLSearchParams(window.location.search)

        if (store.mappingsData.namespaces.map(namespace => namespace.id).includes(urlParams.get("namespace") ?? "")) {
            useMappingsStore().namespace = (urlParams.get("namespace") ?? "") as string
            useMappingsStore().translateAs = undefined
            useMappingsStore().version = undefined
        }

        if (store.mappingsData.namespaces.map(namespace => namespace.id).includes(urlParams.get("translateAs") ?? "")
            && useMappingsStore().namespace && useMappingsStore().namespace !== urlParams.get("translateAs")) {
            useMappingsStore().translateAs = (urlParams.get("translateAs") ?? "") as string
        }

        if (applicableMappingsVersions().map(version => version.version).includes(urlParams.get("version") ?? "")) {
            useMappingsStore().version = (urlParams.get("version") ?? "") as string
        }

        if (urlParams.has("search")) useMappingsStore().searchText = urlParams.get("search") ?? ""
        if (urlParams.has("allowClasses")) useMappingsStore().allowClasses = urlParams.get("allowClasses") === "true"
        if (urlParams.has("allowMethods")) useMappingsStore().allowMethods = urlParams.get("allowMethods") === "true"
        if (urlParams.has("allowFields")) useMappingsStore().allowFields = urlParams.get("allowFields") === "true"
        
        useMappingsDataStore().hasFirstLoaded = true
    }
   
    let {namespace, version, allowSnapshots, translateAs} = useMappingsStore()
    if (!namespace) {
        namespace = store.mappingsData.namespaces[0]?.id
        useMappingsStore().namespace = namespace
    }
    let namespaceObj = store.mappingsData.namespaces.find(value => value.id === namespace)
    let applicable_versions = namespaceObj?.versions
    if (applicable_versions) {
        if (!allowSnapshots) {
            applicable_versions = applicable_versions.filter(entry => entry.stable)
        }
        if (translateAs) {
            let translateAsObj = store.mappingsData.namespaces.find(value => value.id === translateAs)
            let retain = translateAsObj?.versions?.map(entry => entry.version) ?? []
            applicable_versions = applicable_versions.filter(value => retain.includes(value.version))
        }
        if (!version || !applicable_versions.find(entry => entry.version === version)) {
            version = applicable_versions.find(_ => true)?.version
            useMappingsStore().version = version
        }
    }

    if (useMappingsDataStore().hasFirstLoaded) {
        updateMappingsWindowUrl()
    }
}

export function updateMappingsInfo() {
    let store = useMappingsDataStore()
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs} = useMappingsStore()
    if (namespace && version && searchText && (allowClasses || allowMethods || allowFields)) {
        if (store.infoData.namespace !== namespace || store.infoData.version !== version || store.infoData.query !== searchText
            || store.infoData.allowClasses !== allowClasses || store.infoData.allowFields !== allowFields || store.infoData.allowMethods !== allowMethods
            || store.infoData.translateAs !== translateAs) {
            if (store.searchController) {
                store.searchController.abort()
            }
            store.searchController = new AbortController()
            reqSearch(namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs, store.searchController).then(value => {
                if (value.data.error) {
                    if (value.data.error !== "No results found!") {
                        addAlert({
                            type: "error",
                            message: `Failed to search: ${value.data.error}`,
                        })
                    }
                    store.infoData.entries = []
                    store.infoData.fuzzy = false
                    return
                }
                store.infoData.fuzzy = value.data.fuzzy
                store.infoData.entries = (value.data.entries as any[]).map(mapEntryToMappingEntry)
                if (useMappingsDataStore().hasFirstLoaded) {
                    updateMappingsWindowUrl()
                }
            }).catch(reason => {
                if (!axios.isCancel(reason)) {
                    addAlert({
                        type: "error",
                        message: `Failed to search: ${reason.message}`,
                    })
                    store.infoData.entries = []
                    store.infoData.fuzzy = false
                }
            }).finally(() => {
                store.searchController = undefined
            })
        }
    } else {
        store.infoData.entries = []
        store.infoData.fuzzy = false
    }
    setInfoDataToCurrent()
}

export function setInfoDataToCurrent() {
    let store = useMappingsDataStore()
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs} = useMappingsStore()
    store.infoData.namespace = namespace
    store.infoData.version = version
    store.infoData.query = searchText
    store.infoData.allowClasses = allowClasses
    store.infoData.allowMethods = allowMethods
    store.infoData.allowFields = allowFields
    store.infoData.translateAs = translateAs
}

export function mapEntryToMappingEntry(obj: any): MappingEntry {
    let type: string
    if (obj.t === "c") type = "class"
    else if (obj.t === "f") type = "field"
    else if (obj.t === "m") type = "method"
    else type = obj.t
    let translatedTo: undefined | MappingEntry
    if (obj.l) translatedTo = mapEntryToMappingEntry(obj.l)
    else translatedTo = undefined
    return {
        obf: obj.o,
        intermediary: obj.i,
        named: obj.n,
        descObf: obj.d,
        descIntermediary: obj.e,
        descNamed: obj.f,
        ownerObf: obj.a,
        ownerIntermediary: obj.b,
        ownerNamed: obj.c,
        ownerObfClient: obj.g,
        obfClient: obj.h,
        descObfClient: obj.j,
        ownerObfServer: obj.k,
        obfServer: obj.l,
        descObfServer: obj.m,
        type,
        translatedTo,
    } as MappingEntry
}

export function updateMappingsWindowUrl() {
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs} = useMappingsStore()
    let url = new URL(window.location.href)
    url.searchParams.set("namespace", namespace ?? "")
    url.searchParams.set("version", version ?? "")
    url.searchParams.set("search", searchText ?? "")

    if (!allowClasses) url.searchParams.set("allowClasses", "false")
    else url.searchParams.delete("allowClasses")

    if (!allowFields) url.searchParams.set("allowFields", "false")
    else url.searchParams.delete("allowFields")

    if (!allowMethods) url.searchParams.set("allowMethods", "false")
    else url.searchParams.delete("allowMethods")

    if (translateAs) url.searchParams.set("translateAs", translateAs)
    else url.searchParams.delete("translateAs")

    window.history.replaceState({}, "", url.toString())
}
