<template>
    <Block>
        <SubHeader :addPadding="false">
            {{ getDisplayName(entry) }}
            <span v-if="hasTranslation"> > {{ getDisplayName(entry.translatedTo) }}</span>
            <div class="badge badge-sm ml-2" :class="{
                    'badge-primary': entry.type === 'class',
                    'badge-secondary': entry.type === 'field',
                    'badge-accent': entry.type === 'method',
                }">{{ entry.type }}
            </div>
        </SubHeader>
        <div class="text-sm breadcrumbs" v-if="breadcrumbs.length > 1">
            <ul>
                <li v-for="breadcrumb in breadcrumbs">{{ breadcrumb }}</li>
            </ul>
        </div>
        <div class="divider mt-0 mb-0"/>
        <EntryDetails v-if="entry.type === 'field' && namespace.supportsFieldDescription" title="Type:" :content="fieldType(entry)" :code="false"/>
        <EntryDetails v-if="entry.type !== 'class' && namespace.supportsMixin" title="Mixin Target:" :content="mixinTarget(entry)"/>
        <EntryDetails v-if="namespace.supportsAT" title="AT:" :content="atText(entry)"/>
        <EntryDetails v-if="namespace.supportsAW" title="AW:" :content="awText(entry)"/>

        <div v-if="hasTranslation">
            <div class="divider mt-0 mb-0"/>
            <EntryDetails v-if="entry.type === 'field' && translatedToNamespace.supportsFieldDescription" title="Type:" :content="fieldType(entry.translatedTo)"
                          :code="false"/>
            <EntryDetails v-if="entry.type !== 'class' && translatedToNamespace.supportsMixin" title="Mixin Target:"
                          :content="mixinTarget(entry.translatedTo)"/>
            <EntryDetails v-if="translatedToNamespace.supportsAT" title="AT:" :content="atText(entry.translatedTo)"/>
            <EntryDetails v-if="translatedToNamespace.supportsAW" title="AW:" :content="awText(entry.translatedTo)"/>
        </div>
    </Block>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {MappingEntry, Namespace} from "../../routes/Mappings.vue"
import Block from "../Block.vue"
import Header from "../dependencies/Header.vue"
import SubHeader from "../dependencies/SubHeader.vue"
import EntryDetails from "./EntryDetails.vue"

function getOptimumName(entry: MappingEntry): string {
    return entry.named || entry.intermediary || ""
}

function getOptimumOwnerName(entry: MappingEntry): string {
    return entry.ownerNamed || entry.ownerIntermediary || ""
}

function getOptimumDesc(entry: MappingEntry): string {
    return entry.descNamed || entry.descIntermediary || ""
}

function formatName(str: string | undefined) {
    if (str) {
        return str.replaceAll("$", ".").replaceAll("/", ".")
    } else {
        return str
    }
}

function onlyClass(str: string | undefined) {
    if (str) {
        let indexOf = str.lastIndexOf("/")
        str = indexOf == -1 ? str : str.substring(indexOf + 1)
        return formatName(str)
    } else {
        return str
    }
}

function getObf(entry: MappingEntry) {
    if (entry.type == "class") {
        return formatName(entry.obf)
    } else {
        return `${formatName(entry.ownerObf)}.${onlyClass(entry.obf)}`
    }
}

function getObfClient(entry: MappingEntry) {
    if (entry.type == "class") {
        return formatName(entry.obfClient)
    } else {
        return `${formatName(entry.ownerObfClient)}.${onlyClass(entry.obfClient)}`
    }
}

function getObfServer(entry: MappingEntry) {
    if (entry.type == "class") {
        return formatName(entry.obfServer)
    } else {
        return `${formatName(entry.ownerObfServer)}.${onlyClass(entry.obfServer)}`
    }
}

function getIntermediaryName(entry: MappingEntry) {
    if (entry.type == "class") {
        return formatName(entry.intermediary)
    } else {
        return `${formatName(entry.ownerIntermediary)}.${onlyClass(entry.intermediary)}`
    }
}

function getDisplayName(entry: MappingEntry, simplify: boolean = true) {
    if (simplify) {
        if (entry.type == "class") {
            return onlyClass(getOptimumName(entry))
        } else {
            return `${onlyClass(getOptimumOwnerName(entry))}.${onlyClass(getOptimumName(entry))}`
        }
    } else {
        if (entry.type == "class") {
            return formatName(getOptimumName(entry))
        } else {
            return `${formatName(getOptimumOwnerName(entry))}.${onlyClass(getOptimumName(entry))}`
        }
    }
}

function getBreadcrumbs(hasTranslation: boolean, entry: MappingEntry) {
    let breadcrumbs = [] as string[]
    if (!hasTranslation) {
        if (entry.obf) {
            breadcrumbs.push(getObf(entry)!)
        } else {
            let list = [] as string[]
            if (entry.obfClient) list.push(getObfClient(entry)! + " (client)")
            if (entry.obfServer) list.push(getObfServer(entry)! + " (server)")
            if (list.length > 0) {
                breadcrumbs.push(list.join(" / "))
            }
        }
        let intermediary = getIntermediaryName(entry)!
        let named = getDisplayName(entry, false)!
        breadcrumbs.push(intermediary)
        if (intermediary !== named && named) {
            breadcrumbs.push(named)
        }
    } else {
        let translatedTo = entry.translatedTo!
        let intermediary = getIntermediaryName(entry)!
        let named = getDisplayName(entry, false)!
        if (intermediary !== named && named) {
            breadcrumbs.push(named)
        }
        breadcrumbs.push(intermediary)
        let obf = entry.obf ? getObf(entry)! : undefined
        let tObf = translatedTo.obf ? getObf(translatedTo)! : undefined
        if (tObf != obf) {
            if (obf) breadcrumbs.push(obf)
            if (tObf) breadcrumbs.push(tObf)
        }

        let Tintermediary = getIntermediaryName(translatedTo)!
        let Tnamed = getDisplayName(translatedTo, false)!
        if (Tintermediary !== intermediary) {
            breadcrumbs.push(Tintermediary)
        }
        if (Tintermediary !== Tnamed && Tnamed) {
            breadcrumbs.push(Tnamed)
        }
    }
    return breadcrumbs
}

function beautifyFieldType(type: string) {
    if (!type) return type
    let bracketLast = type.lastIndexOf("[")
    let clear = bracketLast == -1 ? type : type.substring(bracketLast + 1)
    let arrays = length - clear.length

    let str = ""
    if (clear.length > 0) {
        if (clear.startsWith("L")) {
            str += clear.substring(1, clear.length - 1).replaceAll("/", ".").replaceAll("$", ".")
        } else {
            switch (clear.at(0)) {
                case "Z":
                    str += "boolean"
                    break
                case "C":
                    str += "char"
                    break
                case "B":
                    str += "byte"
                    break
                case "S":
                    str += "short"
                    break
                case "I":
                    str += "int"
                    break
                case "F":
                    str += "float"
                    break
                case "J":
                    str += "long"
                    break
                case "D":
                    str += "double"
                    break
            }
        }
    }

    for (let i = 0; i < arrays; i++) {
        str += "[]"
    }

    return str
}

export default defineComponent({
    name: "MappingsEntryBlock",
    components: {EntryDetails, SubHeader, Header, Block},
    data() {
        return {
            getDisplayName,
        }
    },
    computed: {
        breadcrumbs() {
            return getBreadcrumbs(this.hasTranslation, this.entry)
        },
        hasTranslation(): boolean {
            return this.entry.translatedTo !== undefined && this.translatedToNamespace !== undefined
        },
    },
    methods: {
        mixinTarget(entry: MappingEntry) {
            return `L${getOptimumOwnerName(entry)};${getOptimumName(entry)}${entry.type === "field" ? ":" : ""}${getOptimumDesc(entry)}`
        },
        atText(entry: MappingEntry) {
            if (entry.type === "class") {
                return `public ${getOptimumName(entry).replaceAll("/", ".")}`
            } else if (entry.type === "method") {
                return `public ${getOptimumOwnerName(entry).replaceAll("/", ".")} ${entry.intermediary}${getOptimumDesc(entry)} # ${getOptimumName(entry)}`
            } else {
                return `public ${getOptimumOwnerName(entry).replaceAll("/", ".")} ${entry.intermediary} # ${getOptimumName(entry)}`
            }
        },
        awText(entry: MappingEntry) {
            if (entry.type === "class") {
                return `accessible ${entry.type} ${getOptimumName(entry)}`
            }
            return `accessible ${entry.type} ${getOptimumOwnerName(entry)} ${getOptimumName(entry)} ${getOptimumDesc(entry)}`
        },
        fieldType(entry: MappingEntry) {
            let desc = getOptimumDesc(entry)
            return beautifyFieldType(desc)
        },
    },
    props: {
        namespace: {
            type: Object as PropType<Namespace>,
        },
        translatedToNamespace: {
            type: Object as PropType<Namespace>,
        },
        entry: {
            type: Object as PropType<MappingEntry>,
            required: true,
        },
    },
})
</script>

<style scoped>
</style>