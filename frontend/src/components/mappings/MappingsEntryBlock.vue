<template>
    <Block>
        <SubHeader>
            <div class="flex">
                <div class="flex-1 overflow-x-auto flex flex-nowrap items-center">
                    <Copyable :copy="getDisplayName(entry)">{{ getDisplayName(entry) }}</Copyable>
                    <svg v-if="hasTranslation" xmlns="http://www.w3.org/2000/svg" class="mx-1 min-w-[22px]" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="7 7 12 12 7 17"></polyline>
                        <polyline points="13 7 18 12 13 17"></polyline>
                    </svg>
                    <Copyable v-if="hasTranslation" :copy="getDisplayName(entry.translatedTo!!)">{{ getDisplayName(entry.translatedTo) }}</Copyable>
                    <div class="rounded-full text-[.75rem] px-[.438rem] inline-flex items-center justify-center h-4 ml-2 text-base-content"
                         :class="{
                            'bg-primary': entry.type === 'class',
                            'bg-secondary': entry.type === 'field',
                            'bg-tertiary': entry.type === 'method',
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
        <div class="my-1 text-sm whitespace-nowrap flex items-center" v-if="entry.type === 'method'">
            <div class="mr-2 relative" v-if="!!entry.argsGuessed || !!entry.argsParchment">
                <svg v-if="!!entry.argsGuessed" xmlns="http://www.w3.org/2000/svg" class="peer" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M12 1.67c.955 0 1.845 .467 2.39 1.247l.105 .16l8.114 13.548a2.914 2.914 0 0 1 -2.307 4.363l-.195 .008h-16.225a2.914 2.914 0 0 1 -2.582 -4.2l.099 -.185l8.11 -13.538a2.914 2.914 0 0 1 2.491 -1.403zm.01 13.33l-.127 .007a1 1 0 0 0 0 1.986l.117 .007l.127 -.007a1 1 0 0 0 0 -1.986l-.117 -.007zm-.01 -7a1 1 0 0 0 -.993 .883l-.007 .117v4l.007 .117a1 1 0 0 0 1.986 0l.007 -.117v-4l-.007 -.117a1 1 0 0 0 -.993 -.883z" stroke-width="0" fill="currentColor"/>
                </svg>
                <svg v-else-if="!!entry.argsParchment" xmlns="http://www.w3.org/2000/svg" class="peer" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M19 2a3 3 0 0 1 2.995 2.824l.005 .176v14a3 3 0 0 1 -2.824 2.995l-.176 .005h-14a3 3 0 0 1 -2.995 -2.824l-.005 -.176v-14a3 3 0 0 1 2.824 -2.995l.176 -.005h14zm-7 9h-1l-.117 .007a1 1 0 0 0 0 1.986l.117 .007v3l.007 .117a1 1 0 0 0 .876 .876l.117 .007h1l.117 -.007a1 1 0 0 0 .876 -.876l.007 -.117l-.007 -.117a1 1 0 0 0 -.764 -.857l-.112 -.02l-.117 -.006v-3l-.007 -.117a1 1 0 0 0 -.876 -.876l-.117 -.007zm.01 -3l-.127 .007a1 1 0 0 0 0 1.986l.117 .007l.127 -.007a1 1 0 0 0 0 -1.986l-.117 -.007z" stroke-width="0" fill="currentColor"/>
                </svg>
                <Tooltip>
                    <div v-if="!!entry.argsGuessed" class="flex flex-col gap-1">
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="mr-1.5" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                <path d="M12 9v4"/>
                                <path d="M10.363 3.591l-8.106 13.534a1.914 1.914 0 0 0 1.636 2.871h16.214a1.914 1.914 0 0 0 1.636 -2.87l-8.106 -13.536a1.914 1.914 0 0 0 -3.274 0z"/>
                                <path d="M12 16h.01"/>
                            </svg>
                            <span class="font-semibold">Warning</span>
                        </div>
                        The method argument mappings are aggregated from other mapping projects,<br>
                        and may not be accurate or fit the current context.<br><br>
                        This may be the result of the lack of a proper source for this mappings or this version.
                    </div>
                    <div v-else-if="!!entry.argsParchment" class="flex flex-col gap-1">
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="mr-1.5" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                <path d="M12 9h.01"/>
                                <path d="M3 5a2 2 0 0 1 2 -2h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2h-14a2 2 0 0 1 -2 -2v-14z"/>
                                <path d="M11 12h1v4h1"/>
                            </svg>
                            <span class="font-semibold">Info</span>
                        </div>
                        The method argument mappings are provided from a community project, ParchmentMC, <br>
                        and may not be accurate, or may be subject to a different license.
                    </div>
                </Tooltip>
            </div>
            <div class="font-mono overflow-x-auto epic-scroller">
                <span class="font-bold">{{ methodReturnType(entry) }}</span>
                {{ onlyClass(getOptimumName(entry)) }}(<!---->
                <AutoBold class="[&_.bold]:font-bold">{{ methodArgs(entry) }}</AutoBold><!--
                -->)
            </div>
        </div>
        <div class="breadcrumbs text-sm max-w-full overflow-x-auto epic-scroller py-2" v-if="breadcrumbs.length > 1">
            <ul class="flex items-center whitespace-nowrap">
                <li v-for="breadcrumb in breadcrumbs" class="flex items-center">
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
            <EntryDetails v-if="entry.type === 'field' && translatedToNamespace!!.supportsFieldDescription" :title="$t('mappings.entry.type')" :code="false">
                <Copyable :copy="fieldType(entry.translatedTo!!)" strokeWidth="1">{{ fieldType(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="entry.type !== 'class' && translatedToNamespace!!.supportsMixin" :title="$t('mappings.entry.mixin.target')">
                <Copyable :copy="mixinTarget(entry.translatedTo!!)" strokeWidth="1">{{ mixinTarget(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="translatedToNamespace!!.supportsAT" :title="$t('mappings.entry.at')">
                <Copyable :copy="atText(entry.translatedTo!!)" strokeWidth="1">{{ atText(entry.translatedTo) }}</Copyable>
            </EntryDetails>
            <EntryDetails v-if="translatedToNamespace!!.supportsAW" :title="$t('mappings.entry.aw')">
                <Copyable :copy="awText(entry.translatedTo!!)" strokeWidth="1">{{ awText(entry.translatedTo) }}</Copyable>
            </EntryDetails>
        </div>

        <div :class="[expandSource === namespace?.id + ' ' + version + ' ' + query ? 'expanded' : '', 'expand-height !ease-in']">
            <div class="h-2"/>
            <div class="rounded-lg bg-base-dark-200 p-3 text-sm h-[28rem] overflow-x-auto resize-y epic-scroller">
                <div class="h-full items-center justify-center grid" v-if="source === ''">
                    <div class="flex gap-4 items-center justify-center animate-pulse animate-bounce text-base-dark-content" ref="source-loading">
                        <svg xmlns="http://www.w3.org/2000/svg" class="animate-spin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2"
                             stroke="currentColor"
                             fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <path d="M10 17a3 3 0 1 1 -1.543 -2.623l2.087 -3.754a3 3 0 0 1 1.456 -5.623a3 3 0 0 1 1.457 5.623l2.087 3.754a3 3 0 1 1 -1.538 2.8l-.006 -.177h-4z"></path>
                            <path d="M17 17v.01"></path>
                            <path d="M7 17v.01"></path>
                            <path d="M12 8v.01"></path>
                        </svg>
                        <p class="font-medium text-xl">Generating Sources... This is slow on the first run</p>
                    </div>
                </div>
                <code ref="source" class="whitespace-pre text-base-dark-content"></code>
            </div>
        </div>
    </Block>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import Block from "../Block.vue"
import Header from "../dependencies/Header.vue"
import SubHeader from "../dependencies/SubHeader.vue"
import EntryDetails from "./EntryDetails.vue"
import CodeBlock from "../dependencies/CodeBlock.vue"
import {reqSource} from "../../app/backend"
import {addAlert} from "../../app/alerts"
import {MappingEntry, Namespace} from "../../app/mappings-data"
import Copyable from "../Copyable.vue"
import Prism, {TokenStream} from "prismjs"
import GeneratorDescription from "../generator/GeneratorDescription.vue"
import AutoBold from "./AutoBold.vue"
import Tooltip from "../Tooltip.vue"

function getOptimumName(entry: MappingEntry): string {
    return entry.named || entry.intermediary || ""
}

function getOptimumOwnerName(entry: MappingEntry): string {
    return entry.ownerNamed || entry.ownerIntermediary || ""
}

function getOptimumDesc(entry: MappingEntry): string {
    return entry.descNamed || entry.descIntermediary || ""
}

function formatName<T extends string | undefined>(str: T): T {
    if (str) {
        return str.replaceAll("$", ".").replaceAll("/", ".") as T
    } else {
        return str
    }
}

function onlyClass<T extends string | undefined>(str: T): T {
    if (str) {
        let indexOf = str.lastIndexOf("/")
        let substr = indexOf == -1 ? str : str.substring(indexOf + 1)
        return formatName(substr) as T
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
                case "V":
                    str += "void"
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
    components: {Tooltip, AutoBold, GeneratorDescription, Copyable, CodeBlock, EntryDetails, SubHeader, Header, Block},
    data() {
        return {
            getDisplayName,
            expandSource: null as string | null,
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
                let sourceElement = this.$refs.source as HTMLFormElement
                if (!sourceElement) return
                let toFind = onlyClass(getOptimumName(this.entry)) ?? ""
                if (toFind.includes("."))
                    toFind = toFind.substring(0, toFind.indexOf("."))

                function extractToken(token: TokenStream): string {
                    if (typeof token === "string") {
                        return token
                    } else if (Array.isArray(token)) {
                        let result = ""
                        token.forEach((t) => {
                            result += extractToken(t)
                        })
                        return result
                    } else {
                        let classes = "token " + token.type
                        if (token.alias) {
                            classes += " " + token.alias
                        }
                        return `<span class="${classes}">${extractToken(token.content)}</span>`
                    }
                }

                let inner = ""
                let code = ""
                Prism.tokenize(this.source, Prism.languages.java).flatMap(extractToken).forEach((token: string) => {
                    if (token.startsWith("<") || token.endsWith(">")) {
                        code += token
                    } else {
                        let split = token.split("\n")
                        for (let j = 0; j < split.length; j++) {
                            let lineToken = split[j]
                            if (j !== 0) code += "\n"
                            code += "<span class='token text'>"
                            code += lineToken
                            code += "</span>"
                        }
                    }
                })
                let i = 0
                for (let line of code.split("\n")) {
                    inner += "<div class='flex gap-2 ml-[-.75rem] pl-3 inline-block' id='source-line-"
                    inner += ++i
                    inner += "'><span class='shrink-0 w-6 text-right font-semibold select-none'>"
                    inner += i
                    inner += "</span>"
                    inner += "<span class='grow'>"
                    inner += line
                    inner += "</span></div>"
                }

                sourceElement.innerHTML = inner

                for (let i = 0; i < sourceElement.children.length; i++) {
                    let entireLine = sourceElement.children[i]
                    let line = entireLine.children[1]
                    for (let j = 0; j < line.children.length; j++) {
                        let child = line.children[j]
                        let tokenType = "field"
                        for (let key of child.className.split(" ")) {
                            if (key !== "token") {
                                tokenType = key
                            }
                        }
                        if (this.entry.type === "class" && tokenType === "class-name" && child.textContent === toFind) {
                            if (!line.children[j - 2]?.className?.includes("keyword")) continue
                            if (line.children[j - 2]?.textContent !== "class"
                                && line.children[j - 2]?.textContent !== "interface"
                                && line.children[j - 2]?.textContent !== "enum") continue
                        } else if (this.entry.type === "method" && tokenType === "function" && child.textContent === toFind) {
                            if (!line.children[j + 1]?.className?.includes("punctuation")) continue
                            if (line.children[j + 1]?.textContent !== "(") continue
                            if (!line.children[j - 1]?.className?.includes("text")) continue
                            if (line.children[j - 1]?.textContent !== " ") continue
                            if (!(line.children[j - 2]?.className?.includes("keyword") && line.children[j - 1]?.textContent !== "return")
                                && !line.children[j - 2]?.className?.includes("class-name")
                                && !line.children[j - 2]?.className?.includes("generics")) continue
                        } else if (this.entry.type === "field" && tokenType === "text" && child.textContent === " " + toFind) {
                            if (line.children[j + 1]?.textContent !== ";") continue
                        } else if (this.entry.type === "field" && tokenType === "text" && child.textContent === " " + toFind + " ") {
                            if (line.children[j + 1]?.textContent !== "=") continue
                        } else if (this.entry.type === "field" && tokenType === "constant" && child.textContent === toFind) {
                            if (line.children[j + 1]?.textContent !== " ") continue
                            if (line.children[j + 2]?.textContent !== "=") continue
                        } else continue
                        if (this.entry.type === "field") {
                            let type = this.entry.descNamed ?? this.entry.descIntermediary
                            if (!type) continue
                            type = beautifyFieldType(type)
                            if (type.includes(".")) {
                                // get the last
                                type = type.substring(type.lastIndexOf(".") + 1)
                            }
                            let prev = tokenType === "constant" ? 2 : 1
                            if (line.children[j - prev]?.className?.includes("generics")) {
                                prev++
                            }
                            if (line.children[j - prev]?.textContent !== type) continue
                        }
                        entireLine.className += " font-bold bg-base-dark-400"
                        line.className += " font-bold bg-base-dark-400"
                        this.$nextTick(() => {
                            entireLine.scrollIntoView({behavior: "smooth", block: "center"})
                        })
                        return
                    }
                }
            })
        },
        expandSource() {
            this.$nextTick(() => {
                let loadingElement = this.$refs["source-loading"] as HTMLFormElement
                if (!loadingElement || !loadingElement[0]) return
                loadingElement[0].scrollIntoView({behavior: "smooth", block: "center"})
            })
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
        methodReturnType(entry: MappingEntry) {
            let desc = getOptimumDesc(entry)
            desc = desc.substring(desc.lastIndexOf(")") + 1)
            return onlyClass(beautifyFieldType(desc).replaceAll(".", "/"))
        },
        getMethodArgs(entry: MappingEntry): string[][] {
            let desc = getOptimumDesc(entry)
            let args = [] as string[][]
            // Args can be single char or full class name wrapped with L and ;
            let index = desc.indexOf("(") + 1
            let array = 0
            while (desc[index] !== ")") {
                let start = desc[index]
                if (start === "[") {
                    array++
                    index++
                } else if (start === "L") {
                    let end = desc.indexOf(";", index)
                    let arg = desc.substring(index, end + 1)
                    args.push(["[".repeat(array) + arg, onlyClass(beautifyFieldType(arg).replaceAll(".", "/")) + "[]".repeat(array)])
                    array = 0
                    index = end + 1
                } else {
                    args.push(["[".repeat(array) + desc[index], beautifyFieldType(desc[index]) + "[]".repeat(array)])
                    array = 0
                    index++
                }
            }
            return args
        },
        methodArgs(entry: MappingEntry) {
            let args = this.getMethodArgs(entry)
            let min = 1000000, max = 0, twoSpaces = 0
            if (entry.args) {
                Object.keys(entry.args).filter(it => entry.args!!.hasOwnProperty(it)).map(it => +it).forEach(it => {
                    if (it < min) min = it
                    if (it > max) max = it
                })
                for (let i = 0; i < args.length - 1; i++) {
                    if (args[i][0] == "J" || args[i][0] == "D") {
                        twoSpaces++
                    }
                }
                console.log(Object.values(entry.args), {...entry.args}, [...args], min, max, args.length, max - min + 1 - twoSpaces)
            }
            
            let apply = false
            if (max - min + 1 - twoSpaces == args.length) apply = true
            return args.map((arg, index) => {
                let name = `var${index}`
                if (entry.args && apply) {
                    let argName = Object.values(entry.args)[index]
                    if (argName) {
                        name = argName
                    }
                }
                return `*${arg[1]}* ${name}`
            }).join(", ")
        },
        requestSource() {
            this.expandSource = !this.expandSource || this.expandSource !== this.namespace?.id + " " + this.version + " " + this.query ? this.namespace?.id + " " + this.version + " " + this.query : null
            this.source = ""

            if (this.expandSource === this.namespace?.id + " " + this.version + " " + this.query) {
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
            required: true,
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
        query: {
            type: String,
        },
    },
})
</script>

<style scoped>
.breadcrumbs > ul > li + *:before {
    content: "";
    @apply mx-2 block opacity-40 rotate-45 border-t border-r border-solid bg-transparent h-1.5 w-1.5;
    border-color: initial;
}
</style>