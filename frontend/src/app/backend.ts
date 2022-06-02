import axios, {AxiosResponse} from "axios"

export const HTTP = axios.create({
    baseURL: "https://linkieapi.shedaniel.me",
})

export function reqVersions<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions`)
}

export function reqVersionsFor<T = any>(loader: string): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/versions/${loader}`)
}

export function reqNamespaces<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/namespaces`)
}

export function reqSearch<T = any>(namespace: string, version: string, query: string, allowClasses: boolean, allowFields: boolean, allowMethods: boolean,
                                   abortController?: AbortController, limit: number = 50): Promise<AxiosResponse<T>> {
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
        },
    })
}

export function reqOss<T = any>(): Promise<AxiosResponse<T>> {
    return HTTP.get(`/api/oss`)
}
