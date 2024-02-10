import {defineStore} from "pinia"
import {getQuery, hasQuery, reqNamespaces, reqSearch} from "./backend"
import {addAlert} from "./alerts"
import {applicableMappingsVersions, useMappingsStore} from "./mappings-store"
import axios from "axios"
import {VersionEntry} from "./dependencies-data"
import {LocationQuery} from "vue-router"

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
    args?: { [index: number]: string },
    argsGuessed?: boolean,
    argsParchment?: boolean,
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
    translateAsVersion?: string,
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

export function updateMappingsData(fullPath?: string, query: LocationQuery | null = null) {
    let store = useMappingsDataStore()
    if (query || (Object.keys(store.mappingsData.namespaces).length == 0 && !store.reqNamespacesPromise)) {
        store.reqNamespacesPromise = reqNamespaces().then(value => {
            useMappingsDataStore().mappingsData.namespaces = value.data
            ensureMappingsData(fullPath, query)
        }).catch(reason => {
            addAlert({
                type: "error",
                message: `Failed to fetch namespaces: ${reason.message}`,
            })
        }).finally(() => {
            useMappingsDataStore().reqNamespacesPromise = undefined
        })
    }
}

export function ensureMappingsData(fullPath?: string, query: LocationQuery | null = null) {
    let store = useMappingsDataStore()
    if (query) {
        if (store.mappingsData.namespaces.map(namespace => namespace.id).includes(getQuery(query, "namespace") ?? "")) {
            useMappingsStore().namespace = (getQuery(query, "namespace") ?? "") as string
            useMappingsStore().translateMode = undefined
            useMappingsStore().translateAs = undefined
            useMappingsStore().translateAsVersion = undefined
            useMappingsStore().version = undefined
        }

        if (getQuery(query, "translateMode")) {
            useMappingsStore().translateMode = getQuery(query, "translateMode") ?? ""
            
            if (useMappingsStore().translateMode !== undefined && useMappingsStore().translateMode !in ["ns", "ver"]) {
                useMappingsStore().translateMode = undefined
                useMappingsStore().translateAs = undefined
                useMappingsStore().translateAsVersion = undefined
            }
        }

        if (useMappingsStore().translateMode === "ns" && store.mappingsData.namespaces.map(namespace => namespace.id).includes(getQuery(query, "translateAs") ?? "")
            && useMappingsStore().namespace && useMappingsStore().namespace !== getQuery(query, "translateAs")) {
            useMappingsStore().translateAs = (getQuery(query, "translateAs") ?? "") as string
        }

        if (applicableMappingsVersions().map(version => version.version).includes(getQuery(query, "version") ?? "")) {
            useMappingsStore().version = (getQuery(query, "version") ?? "") as string
        }
        if (useMappingsStore().translateMode === "ver" && applicableMappingsVersions().map(version => version.version).includes(getQuery(query, "translateAsVersion") ?? "")) {
            useMappingsStore().translateAsVersion = (getQuery(query, "translateAsVersion") ?? "") as string
        }

        if (hasQuery(query, "search")) useMappingsStore().searchText = getQuery(query, "search") ?? ""
        if (hasQuery(query, "allowClasses")) useMappingsStore().allowClasses = getQuery(query, "allowClasses") === "true"
        if (hasQuery(query, "allowMethods")) useMappingsStore().allowMethods = getQuery(query, "allowMethods") === "true"
        if (hasQuery(query, "allowFields")) useMappingsStore().allowFields = getQuery(query, "allowFields") === "true"
        
        useMappingsDataStore().hasFirstLoaded = true
    }
   
    let {namespace, version, allowSnapshots, translateAs, translateAsVersion} = useMappingsStore()
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
        if (!applicable_versions.find(entry => entry.version === translateAsVersion)) {
            useMappingsStore().translateAsVersion = undefined
        }
    }

    if (useMappingsDataStore().hasFirstLoaded && fullPath) {
        updateMappingsWindowUrl(fullPath)
    }
}

export function updateMappingsInfo(fullPath: string | undefined) {
    let store = useMappingsDataStore()
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateMode, translateAs, translateAsVersion} = useMappingsStore()
    if (namespace && version && searchText && (allowClasses || allowMethods || allowFields)) {
        if (store.infoData.namespace !== namespace || store.infoData.version !== version || store.infoData.query !== searchText
            || store.infoData.allowClasses !== allowClasses || store.infoData.allowFields !== allowFields || store.infoData.allowMethods !== allowMethods
            || store.infoData.translateAs !== translateAs || store.infoData.translateAsVersion !== translateAsVersion) {
            if (store.searchController) {
                store.searchController.abort()
            }
            store.searchController = new AbortController()
            reqSearch(namespace, version, searchText, allowClasses, allowFields, allowMethods, translateMode, translateMode === "ns" ? translateAs : translateMode === "ver" ? translateAsVersion : undefined, store.searchController).then(value => {
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
                if (useMappingsDataStore().hasFirstLoaded && fullPath) {
                    updateMappingsWindowUrl(fullPath)
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
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs, translateAsVersion} = useMappingsStore()
    store.infoData.namespace = namespace
    store.infoData.version = version
    store.infoData.query = searchText
    store.infoData.allowClasses = allowClasses
    store.infoData.allowMethods = allowMethods
    store.infoData.allowFields = allowFields
    store.infoData.translateAs = translateAs
    store.infoData.translateAsVersion = translateAsVersion
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
        obfServer: obj.s,
        descObfServer: obj.m,
        args: obj.p,
        argsGuessed: obj.q,
        argsParchment: obj.r,
        type,
        translatedTo,
    } as MappingEntry
}

export function updateMappingsWindowUrl(fullPath: string) {
    let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateMode, translateAs, translateAsVersion} = useMappingsStore()
    let url = new URL(fullPath)
    url.searchParams.set("namespace", namespace ?? "")
    url.searchParams.set("version", version ?? "")
    url.searchParams.set("search", searchText ?? "")

    if (!allowClasses) url.searchParams.set("allowClasses", "false")
    else url.searchParams.delete("allowClasses")

    if (!allowFields) url.searchParams.set("allowFields", "false")
    else url.searchParams.delete("allowFields")

    if (!allowMethods) url.searchParams.set("allowMethods", "false")
    else url.searchParams.delete("allowMethods")

    url.searchParams.set("translateMode", translateMode ?? "none")
    
    if (translateAs) url.searchParams.set("translateAs", translateAs)
    else url.searchParams.delete("translateAs")

    if (translateAsVersion) url.searchParams.set("translateAsVersion", translateAsVersion)
    else url.searchParams.delete("translateAsVersion")

    window.history.replaceState({}, "", url.toString())
}
