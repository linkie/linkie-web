import axios, {AxiosResponse} from "axios"

export const HTTP = axios.create({
    baseURL: "http://localhost:6969",
})

export function reqVersions<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions`)
}

export function reqVersionsFor<T = any>(loader: string): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions`)
}

export function reqNamespaces<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/namespaces`)
}

export function reqSearch<T = any>(namespace: string, version: string, query: string, allowClasses: boolean, allowFields: boolean, allowMethods: boolean, limit: number = 50): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/search`, {
        params: {
            namespace,
            query,
            version,
            limit,
            allowClasses,
            allowFields,
            allowMethods,
        },
    })
}

export function reqOss<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/oss`)
}
