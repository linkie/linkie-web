import {usePreferenceStore} from "./preference-store"

export function formatMaven(url: string): string {
    let codeLanguage = usePreferenceStore().codeLanguage
    if (codeLanguage === "groovy") {
        return `maven { url "${url}" }`
    } else if (codeLanguage === "kotlin") {
        return `maven { url = uri("${url}") }`
    } else {
        return url
    }
}

export function formatDepLine(configuration: string, notation: string): string {
    let codeLanguage = usePreferenceStore().codeLanguage
    if (codeLanguage === "groovy") {
        return `${configuration} "${notation}"`
    } else if (codeLanguage === "kotlin") {
        return `${configuration}("${notation}")`
    } else {
        return notation
    }
}

export function formatDep(configuration: string, notation: string, block: boolean): string {
    if (!block) return formatDepLine(configuration, notation)
    return `dependencies {
    ${formatDepLine(configuration, notation)}
}`
}

export function formatApi(notation: string, block: boolean = true): string {
    return formatDep("modApi", notation, block)
}

export function formatImpl(notation: string, block: boolean = true): string {
    return formatDep("modImplementation", notation, block)
}

export function formatCompileOnly(notation: string, block: boolean = true): string {
    return formatDep("modCompileOnly", notation, block)
}

export function formatRuntimeOnly(notation: string, block: boolean = true): string {
    return formatDep("modRuntimeOnly", notation, block)
}

export const dependencyTypes = ["Api", "Implementation", "CompileOnly", "RuntimeOnly", "Mappings"] as const
export type DependencyType = typeof dependencyTypes[number]

export function formatDependency(type: DependencyType, notation: string, block: boolean = true): string {
    if (type === "Api") {
        return formatApi(notation, block)
    } else if (type === "Implementation") {
        return formatImpl(notation, block)
    } else if (type === "CompileOnly") {
        return formatCompileOnly(notation, block)
    } else if (type === "RuntimeOnly") {
        return formatRuntimeOnly(notation, block)
    } else if (type === "Mappings") {
        return formatDep("mappings", notation, block)
    } else if (type === "Forge") {
        return formatDep("forge", notation, block)
    } else {
        return notation
    }
}

export function tab(): string {
    return "\xa0\xa0\xa0\xa0"
}