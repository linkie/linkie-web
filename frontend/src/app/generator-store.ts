import {defineStore} from "pinia"

export interface State {
    template: string | null
    modLoaders: ("fabric" | "forge" | "neoforge" | "quilt")[]
    fabricLike: boolean
    splitEnv: boolean
    gradleSystem: "loom" | "fg"
    mappings: "yarn" | "mcp" | "mojmap" | "quilt" | null
    parchment: boolean
    gradleLanguage: "groovy" | "kts" | null
    name: string
    description: string
    license: string
    modId: string | null
    version: string
    package: string
    mainClass: string | null
    clientClass: string | null
    authors: string[]
    mixin: boolean | null
    dependencies: string[] | null
    minecraftVersion: string | null
    generating: boolean
}

function newState(): State {
    return {
        template: null,
        modLoaders: ["fabric", "forge"],
        fabricLike: true,
        splitEnv: true,
        gradleSystem: "fg",
        mappings: null,
        parchment: false,
        gradleLanguage: null,
        name: "Mod Name",
        description: "This is an example description! Tell everyone what your mod is about!",
        license: "ARR",
        modId: null,
        version: "0.1.0",
        package: "net.example.mod",
        mainClass: null,
        clientClass: null,
        authors: ["Your Name"],
        mixin: null,
        dependencies: null,
        minecraftVersion: null,
        generating: false,
    }
}

export const useGeneratorStore = defineStore({
    id: "generator",
    state: newState,
})

export function afterSelectTemplate() {
    let {template} = useGeneratorStore()
    if (template === "fabric") {
        useGeneratorStore().dependencies = ["fabric-api"]
    } else if (template === "forge") {
        useGeneratorStore().dependencies = []
    } else if (template === "architectury") {
        useGeneratorStore().dependencies = ["architectury-api"]
    } else if (template === "multiloader") {
        useGeneratorStore().dependencies = []
    }
}

interface Dependency {
    id: string
    name: string
    descriptions?: string[]
    modLoaders?: ("fabric" | "forge" | "neoforge" | "quilt")[]
}

export function isTemplateMultiPlatform() {
    const template = useGeneratorStore().template
    return template === "architectury" || template === "multiloader"
}

export function freeDependencies(): Dependency[] {
    let {template, modLoaders} = useGeneratorStore()
    let dependencies: Dependency[] = []
    let has = (id: typeof modLoaders[number]) => template === id || (isTemplateMultiPlatform() && modLoaders.includes(id))
    if (has("fabric") || has("quilt")) {
        dependencies.push({
            id: "modmenu", name: "ModMenu",
            descriptions: ["Adds a screen for viewing a list of installed mods."],
            modLoaders: ["fabric", "quilt"],
        })
    }
    dependencies.push({
        id: "cloth-config", name: "Cloth Config",
        descriptions: ["A client-sided configuration screen library, includes Auto Config."],
        modLoaders: ["forge", "neoforge", "fabric", "quilt"],
    })
    dependencies.push({
        id: "moonlight", name: "Moonlight",
        descriptions: ["Forge and Fabric lightweight library with utilities such as custom Villagers AI, custom Map Markers..."],
        modLoaders: ["forge", "fabric", "quilt"],
    })
    dependencies.push({
        id: "trinkets", name: "Trinkets",
        descriptions: ["A data-driven accessory mod"],
        modLoaders: ["forge", "fabric", "quilt"],
    })
    dependencies.push({
        id: "jei", name: "Just Enough Items",
        descriptions: ["View Items and Recipes."],
        modLoaders: ["forge", "fabric", "quilt"],
    })
    dependencies.push({
        id: "rei", name: "Roughly Enough Items",
        descriptions: ["Clean and Customizable. An easy way to browse recipes."],
        modLoaders: ["forge", "neoforge", "fabric", "quilt"],
    })
    if (has("fabric") || has("quilt")) {
        dependencies.push({
            id: "forge-config-api", name: "Forge Config API Port",
            descriptions: ["Forge's whole config system provided to the Fabric ecosystem. Designed for a multiloader architecture."],
            modLoaders: ["fabric", "quilt"],
        })
        dependencies.push({
            id: "yacl", name: "YetAnotherConfigLib",
            descriptions: ["A builder-based configuration library for Minecraft."],
            modLoaders: ["fabric", "quilt"],
        })
        dependencies.push({
            id: "cc", name: "Cardinal Components",
            descriptions: ["API for data-driven content."],
            modLoaders: ["fabric", "quilt"],
        })
        dependencies.push({
            id: "owo", name: "oωo",
            descriptions: ["A general utility, GUI and config library for modding on Fabric and Quilt"],
            modLoaders: ["fabric", "quilt"],
        })
    }

    return dependencies
}

export function generateMainClassName() {
    const state = useGeneratorStore()
    if (state.mainClass) return state.mainClass
    let name = state.name.trim()
    // title case each word
    name = name.replace(/(?:^|\s|-)\S/g, (c) => c.toUpperCase())
    name = name.replace(/[^a-zA-Z]/g, " ")
    name = name.replace(/\s/g, "")
    if (name.length == 0) name = "Main"
    return name
}

export function generateClientClassName() {
    const state = useGeneratorStore()
    if (state.clientClass) return state.clientClass
    return generateMainClassName() + "Client"
}

export function generateModID() {
    const state = useGeneratorStore()
    if (state.modId) return state.modId
    let name = state.name.trim()
    name = name.replace(/[^a-zA-Z]/g, " ")
    name = name.replace(/\s/g, "-")
    name = name.toLowerCase()
    if (name.length == 0) name = "modid"
    name = name.substring(0, 32)
    return name
}
