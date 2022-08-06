import axios, {AxiosResponse} from "axios"

export const HTTP = axios.create({
    baseURL: "https://linkieapi.shedaniel.me",
    // baseURL: "http://localhost:6969",
})

export function reqVersions<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions/all`)
}

export function reqNamespaces<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/namespaces`)
}

export function reqSearch<T = any>(namespace: string, version: string, query: string, allowClasses: boolean, allowFields: boolean, allowMethods: boolean,
                                   translate?: string, abortController?: AbortController, limit: number = 50): Promise<AxiosResponse<T>> {
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

export function reqOss<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/oss`)
}

export let namespaceGroups: { [key: string]: string | string[] } = {
    "yarn": "Fabric",
    "mojang": "Fabric",
    "mojang_srg": "Forge",
    "mojang_hashed": "Quilt",
    "mcp": "Forge",
    "quilt-mappings": "Quilt",
}

export let namespaceLocalizations: { [namespace: string]: string } = {
    "yarn": "Yarn",
    "mojang": "Mojang (Intermediary)",
    "mojang_srg": "Mojang (SRG)",
    "mojang_hashed": "Mojang (Hashed)",
    "mcp": "MCP",
    "quilt-mappings": "Quilt Mappings",
    "legacy-yarn": "Legacy Yarn",
    "yarrn": "Yarrn",
    "plasma": "Plasma",
} 
