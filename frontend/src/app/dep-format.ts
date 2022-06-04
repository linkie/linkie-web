import {usePreferenceStore} from "./preference-store"
import {useDependencySearchStore} from "./dependency-store"

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

function isFG(): boolean {
    let {loader, forgeGradle} = useDependencySearchStore()
    return loader === "forge" && forgeGradle
}

export function formatDepLine(configuration: string, notation: string, wrap?: string): string {
    let codeLanguage = usePreferenceStore().codeLanguage
    if (!wrap) wrap = "\"%%\""
    if (codeLanguage === "groovy") {
        return `${configuration} ${wrap.replace("%%", notation)}`
    } else if (codeLanguage === "kotlin") {
        return `${configuration}(${wrap.replace("%%", notation)})`
    } else {
        return notation
    }
}

export function formatDep(configuration: string, notation: string, block: boolean, wrap?: string): string {
    if (!block) return formatDepLine(configuration, notation, wrap)
    return `dependencies {
    ${formatDepLine(configuration, notation, wrap)}
}`
}

export function formatApi(notation: string, block: boolean = true): string {
    if (!isFG()) {
        return formatDep("modApi", notation, block)
    } else {
        return formatDep("api", notation, block, `fg.deobf("%%")`)
    }
}

export function formatImpl(notation: string, block: boolean = true): string {
    if (!isFG()) {
        return formatDep("modImplementation", notation, block)
    } else {
        return formatDep("implementation", notation, block, `fg.deobf("%%")`)
    }
}

export function formatCompileOnly(notation: string, block: boolean = true): string {
    if (!isFG()) {
        return formatDep("modCompileOnly", notation, block)
    } else {
        return formatDep("compileOnly", notation, block, `fg.deobf("%%")`)
    }
}

export function formatRuntimeOnly(notation: string, block: boolean = true): string {
    if (!isFG()) {
        return formatDep("modRuntimeOnly", notation, block)
    } else {
        return formatDep("runtimeOnly", notation, block, `fg.deobf("%%")`)
    }
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