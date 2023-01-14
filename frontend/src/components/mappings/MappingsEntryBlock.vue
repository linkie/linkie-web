<template>
    <Block>
        <SubHeader :addPadding="false">
            <div class="flex">
                <div class="flex-1 overflow-x-auto flex flex-nowrap items-center">
                    <Copyable :copy="getDisplayName(entry)">{{ getDisplayName(entry) }}</Copyable>
                    <svg v-if="hasTranslation" xmlns="http://www.w3.org/2000/svg" class="mx-1 min-w-[22px]" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="7 7 12 12 7 17"></polyline>
                        <polyline points="13 7 18 12 13 17"></polyline>
                    </svg>
                    <Copyable v-if="hasTranslation" :copy="getDisplayName(entry.translatedTo)">{{ getDisplayName(entry.translatedTo) }}</Copyable>
                    <div class="badge badge-sm ml-2" :class="{
                    'badge-primary': entry.type === 'class',
                    'badge-secondary': entry.type === 'field',
                    'badge-accent': entry.type === 'method',
                    }">{{ $t(`mappings.entry.type.${entry.type}`) }}
                    </div>
                </div>
                <div class="cursor-pointer" v-if="namespace?.supportsSource" @click="requestSource()">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-code" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="7 8 3 12 7 16"></polyline>
                        <polyline points="17 8 21 12 17 16"></polyline>
                        <line x1="14" y1="4" x2="10" y2="20"></line>
                    </svg>
                </div>
            </div>
        </SubHeader>
        <div class="text-sm breadcrumbs" v-if="breadcrumbs.length > 1">
            <ul>
                <li v-for="breadcrumb in breadcrumbs">
                    <Copyable :copy="breadcrumb" strokeWidth="1">{{ breadcrumb }}</Copyable>
                </li>
            </ul>
        </div>
        <div class="divider mt-0 mb-0"/>
        <EntryDetails v-if="entry.type === 'field' && namespace.supportsFieldDescription" :title="$t('mappings.entry.type')" :code="false">
            <Copyable :copy="fieldType(entry)" strokeWidth="1">{{ fieldType(entry) }}</Copyable>
        </EntryDetails>
        <EntryDetails v-if="entry.type !== 'class' && namespace.supportsMixin" :title="$t('mappings.entry.mixin.target')">
            <Copyable :copy="mixinTarget(entry)" strokeWidth="1">{{ mixinTarget(entry) }}</Copyable>
        </EntryDetails>
        <EntryDetails v-if="namespace.supportsAT" :title="$t('mappings.entry.at')">
            <Copyable :copy="atText(entry)" strokeWidth="1">{{ atText(entry) }}</Copyable>
        </EntryDetails>
        <EntryDetails v-if="namespace.supportsAW" :title="$t('mappings.entry.aw')">
            <Copyable :copy="awText(entry)" strokeWidth="1">{{ awText(entry) }}</Copyable>
        </EntryDetails>

        <div v-if="hasTranslation">
            <div class="divider mt-0 mb-0"/>
            <EntryDetails v-if="entry.type === 'field' && translatedToNamespace.supportsFieldDescription" :title="$t('mappings.entry.type')" :code="false">
                <Copyable :copy="fieldType(entry.translatedTo)" strokeWidth="1">{{ fieldType(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="entry.type !== 'class' && translatedToNamespace.supportsMixin" :title="$t('mappings.entry.mixin.target')">
                <Copyable :copy="mixinTarget(entry.translatedTo)" strokeWidth="1">{{ mixinTarget(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="translatedToNamespace.supportsAT" :title="$t('mappings.entry.at')">
                <Copyable :copy="atText(entry.translatedTo)" strokeWidth="1">{{ atText(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="translatedToNamespace.supportsAW" :title="$t('mappings.entry.aw')">
                <Copyable :copy="awText(entry.translatedTo)" strokeWidth="1">{{ awText(entry.translatedTo) }}</Copyable>
            </EntryDetails>
        </div>

        <div :class="['rounded-lg bg-base-300 p-3 text-sm mt-2 mb-1 h-[20rem]', source === '' ? 'animate-pulse' : '']" v-if="expandSource">
            <pre class="pl-2 pb-1 overflow-x-auto h-[20rem]"><code
                    v-for="[index, line] of Object.entries(source.split('\n'))"
                    :class="['block break-all whitespace-pre', (line + '').includes((entry.type === 'class' ? 'class ' : ' ') + onlyClass(getOptimumName(entry)) + (entry.type === 'method' ? '(' : '')) ? 'font-bold' : '']"
                    id="code-block"
                    :ref="'source-line-' + index">{{ line === "" ? " " : line }}</code></pre>
        </div>
    </Block>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import Block from "../Block.vue"
import Header from "../dependencies/Header.vue"
import SubHeader from "../dependencies/SubHeader.vue"
import EntryDetails from "./EntryDetails.vue"
import {copyAs} from "../../app/copy";
import CodeBlock from "../dependencies/CodeBlock.vue";
import {reqSource} from "../../app/backend";
import {addAlert} from "../../app/alerts";
import {MappingEntry, Namespace} from "../../app/mappings-data"
import Copyable from "../Copyable.vue"

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
            switch (clear[0]) {
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
    components: {Copyable, CodeBlock, EntryDetails, SubHeader, Header, Block},
    data() {
        return {
            getDisplayName,
            copyAs,
            expandSource: false,
            source: "",
            getOptimumName,
            onlyClass,
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
    watch: {
        source() {
            this.$nextTick(() => {
                for (let entry of Object.entries(this.source.split('\n'))) {
                    if ((entry[1] + "").includes((this.entry.type === "class" ? "class " : " ") + onlyClass(getOptimumName(this.entry)) + (this.entry.type === "method" ? "(" : ""))) {
                        (this.$refs['source-line-' + entry[0]] as HTMLFormElement)[0].scrollIntoView({behavior: 'smooth', block: 'center'})
                        break
                    }
                }
            });
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
        requestSource() {
            this.expandSource = !this.expandSource
            this.source = ''

            if (this.expandSource) {
                reqSource(this.namespace!!.id, this.version!!, this.entry.ownerNamed ?? this.entry.ownerIntermediary ?? this.entry.named ?? this.entry.intermediary).then(value => {
                    this.source = value.data
                }).catch(reason => {
                    addAlert({
                        type: "error",
                        message: `Failed to fetch source: ${reason.message}`,
                    })
                })
            }
        },
    },
    props: {
        namespace: {
            type: Object as PropType<Namespace>,
        },
        translatedToNamespace: {
            type: Object as PropType<Namespace>,
        },
        version: {
            type: String,
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