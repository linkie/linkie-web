<script setup lang="ts">

import Block from "../Block.vue"
import SubHeader from "../dependencies/SubHeader.vue"
import {onMounted, ref} from "vue"
import {HTTP} from "../../app/backend"
import {useMappingsStore} from "../../app/mappings-store"
import {storeToRefs} from "pinia"

import "prism-code-editor/prism/languages/markup"
import "prism-code-editor/prism/languages/java"
import "prism-code-editor/layout.css"
import "prism-code-editor/copy-button.css"
import "prism-code-editor/search.css"
import "prism-code-editor/themes/prism-tomorrow.css"

// @ts-ignore
import {createEditor, PrismEditor} from "prism-code-editor"
// @ts-ignore
import {matchBrackets} from "prism-code-editor/match-brackets"
// @ts-ignore
import {indentGuides} from "prism-code-editor/guides"
// @ts-ignore
import {defaultCommands} from "prism-code-editor/commands"
// @ts-ignore
import {copyButton} from "prism-code-editor/copy-button"
// @ts-ignore
import {highlightBracketPairs} from "prism-code-editor/highlight-brackets"
// @ts-ignore
import {searchWidget} from "prism-code-editor/search"

const editor = ref<PrismEditor>()
const {namespace, version} = storeToRefs(useMappingsStore())

type Class = Record<string, string> & {
    mappedName?: string
}
type Mappings = Record<string, Class>

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

function translate() {
    HTTP.get("api/mappings", {
        params: {
            namespace: namespace.value,
            version: version.value,
        },
    }).then(response => {
        let mappings: Mappings = response.data
        if (!editor.value) {
            console.error("Editor not initialized")
            return
        }
        let stacktraces: string[] = editor.value.value.split("\n")
        let replacements: [string | RegExp, string][] = []

        function replaceAll(find: string | RegExp, replace: string) {
            replacements.push([find, replace])
        }

        for (let [intermediaryName, clazz] of Object.entries(mappings).toReversed()) {
            let intermediaryNameOnly = onlyClass(intermediaryName)
            if (!!clazz.mappedName) {
                let mappedNameOnly = onlyClass(clazz.mappedName)
                replaceAll(intermediaryName.replaceAll("/", "."), clazz.mappedName.replaceAll("/", "."))
                replaceAll(intermediaryNameOnly + ".java:", mappedNameOnly + ".java:")
                replaceAll(new RegExp(intermediaryNameOnly + "(?!\\d)", "g"), mappedNameOnly)
                for (let [functionName, mappedName] of Object.entries(clazz)) {
                    if (functionName != "mappedName") {
                        replaceAll(intermediaryNameOnly + "." + functionName, mappedNameOnly + "." + mappedName)
                        replaceAll(mappedNameOnly + "." + functionName, mappedNameOnly + "." + mappedName)
                    }
                }
            } else {
                for (let [functionName, mappedName] of Object.entries(clazz)) {
                    replaceAll(intermediaryNameOnly + "." + functionName, intermediaryNameOnly + "." + mappedName)
                }
            }
        }

        for (let [intermediaryName, clazz] of Object.entries(mappings).toReversed()) {
            for (let [functionName, mappedName] of Object.entries(clazz)) {
                if (functionName != "mappedName") {
                    replaceAll("." + functionName + "(", "." + mappedName + "(")
                }
            }
        }

        stacktraces = stacktraces.map(stacktrace => {
            for (let [find, replace] of replacements) {
                stacktrace = stacktrace.replaceAll(find, replace)
            }
            return stacktrace
        })

        editor.value.setOptions({
            value: stacktraces.join("\n"),
        })
        editor.value.textarea.focus()
        editor.value.textarea.scrollTop = 0
        editor.value.textarea.scrollLeft = 0
    })
}

onMounted(() => {
    editor.value = createEditor(
        "#editor",
        {
            language: "java",
            tabSize: 4,
        },
        indentGuides(),
        matchBrackets(),
        defaultCommands(),
        highlightBracketPairs(),
        searchWidget(),
        copyButton(),
    )
})
</script>

<template>
    <Block class="p-[-.25rem]">
        <SubHeader class="mb-1.5 w-full flex justify-between items-center">
            <span>Stacktrace</span>
            <button class="bg-base-500/60 hover:bg-base-500/90 dark:bg-base-dark-300 dark:hover:bg-base-dark-400 transition-all rounded-lg px-3 py-2 text-base font-medium select-none" @click="translate()">Translate</button>
        </SubHeader>
<!--        <textarea ref="textarea" rows="30" class="w-full border-0 rounded-md py-2 mb-[-0.25rem] text-base-content dark:text-base-dark-content shadow-sm focus:ring-2 focus:ring-offset-2 focus:ring-base-content dark:focus:ring-base-dark-content caret-base-content dark:caret-base-dark-content focus:ring-offset-base-300 dark:focus:ring-offset-base-dark-200 text-sm leading-4 text-nowrap bg-base-l2 font-mono epic-scroller" autofocus @input="input" v-model="stacktrace"/>-->
        <div id="editor" class="w-full [&>*]:min-h-[20rem] [&>*]:max-h-[60rem] text-sm [&>*]:rounded-md"/>
    </Block>
</template>

<style scoped>

</style>