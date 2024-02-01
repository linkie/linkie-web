import axios, {AxiosResponse} from "axios"
import {isTauri} from "./tauri/tauri"

export const backendServer = "https://linkieapi.shedaniel.me"
export const localBackendServer = "http://localhost:6969"

export const HTTP = axios.create({
    baseURL: currentBackendServer(),
})

export function currentBackendServer(): string {
    return isTauri() ? localBackendServer : backendServer
}

export function reqVersions<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions/all`)
}

export function reqNamespaces<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/namespaces`)
}

export function reqSearch<T = any>(namespace: string, version: string, query: string, allowClasses: boolean, allowFields: boolean, allowMethods: boolean,
                                   translate?: string, abortController?: AbortController, limit: number = 100): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/search`, {
        signal: abortController?.signal,
        params: {
            namespace,
            query,
            version,
            limit,
            allowClasses,
            allowFields,
            allowMethods,
            translate,
        },
    })
}

export function reqSource<T = any>(namespace: string, version: string, className: string): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/source`, {
        params: {
            namespace,
            "class": className,
            version,
        },
    })
}

export function reqOss<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/oss`)
}

export function reqStatusSource<T = any>(namespace: string): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/status/sources/${namespace}`)
}

export let allNamespaceGroups: string[] = [
    "Official",
    "Fabric",
    "Forge",
    "Quilt",
]

export let namespaceGroups: { [key: string]: string | string[] } = {
    "yarn": "Fabric",
    "mojang": "Fabric",
    "mojang_raw": "Official",
    "mojang_srg": "Forge",
    "mojang_hashed": "Quilt",
    "mcp": "Forge",
    "quilt-mappings": "Quilt",
}

export let namespaceLocalizations: { [namespace: string]: string } = {
    "yarn": "Yarn",
    "mojang_raw": "Mojang",
    "mojang": "Mojang (via Intermediary)",
    "mojang_srg": "Mojang (via SRG)",
    "mojang_hashed": "Mojang (via Hashed)",
    "mcp": "MCP",
    "quilt-mappings": "Quilt Mappings",
    "legacy-yarn": "Legacy Yarn",
    "yarrn": "Yarrn",
    "plasma": "Plasma",
    "barn": "Barn",
    "feather": "Feather",
} 
