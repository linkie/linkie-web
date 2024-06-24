<template>
    <div class="flex flex-col">
        <SubHeader class="mt-[-.25rem] mb-1 flex items-center gap-1.5">
            {{ $t("mappings.mode") }}
            <IconBrandCodepen :size="20"/>
        </SubHeader>

      <div class="pt-2 pb-2.5 flex gap-3">
        <button v-for="item in ['mappings', 'stacktrace']"
                :aria-selected="(mode ?? 'mappings') === item" @click="selectMode(item)" @mouseenter="modeHover = true" @mouseleave="modeHover = false"
                class="selection-button py-2 px-3 flex items-center text-left rounded-lg bg-base-500 dark:bg-base-dark-200 bg-opacity-60 hover:bg-opacity-100 dark:hover:bg-base-dark-400 dark:focus:bg-base-dark-400 transition-all duration-150 border-none select-none group overflow-clip flex-shrink-0">
          <IconTriangles class="flex-shrink-0" :size="20" v-if="item === 'mappings'"/>
          <IconFileSad class="flex-shrink-0" :size="20" v-else/>
          <span :aria-expanded="!modeHover && (mode ?? 'mappings') === item"
                class="group-hover:opacity-100 group-hover:w-fit group-hover:pl-2 aria-expanded:opacity-100 aria-expanded:w-fit aria-expanded:pl-2 w-0 pl-0 transition-all duration-150 opacity-[-100] text-ellipsis whitespace-nowrap overflow-hidden">
                    {{ $t(`mappings.mode.${item}`) }}</span>
        </button>
      </div>

      <div class="divider mt-0 mb-0 pb-0.5"/>

        <SubHeader class="mt-[-.25rem] mb-1 flex items-center gap-1.5">
            {{ $t("mappings.namespace") }}
            <IconDatabase :size="20"/>
        </SubHeader>

        <div v-for="[group, nses] in namespacesGrouped">
            <div v-if="!hiddenNamespaceGroups.includes(group)" class="pb-1">
                <p class="text-xs font-bold uppercase">{{ group }}</p>
                <div v-for="ns in nses" :class="[
                    namespace === ns.id ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
                    'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']"
                     @click="namespace = ns.id">
                    {{ localizeNamespace(ns) ?? "" }}
                </div>
            </div>
        </div>

        <div v-for="[group, nses] in namespacesGrouped"
             :class="[expandNamespaces ? 'expanded' : '', 'expand-height']">
            <div v-if="hiddenNamespaceGroups.includes(group)" class="pb-1">
                <p class="text-xs font-bold uppercase">{{ group }}</p>
                <div v-for="ns in nses" :class="[
                    namespace === ns.id ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
                    'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']"
                     @click="namespace = ns.id">
                    {{ localizeNamespace(ns) ?? "" }}
                </div>
            </div>
        </div>

        <div class="mx-[-.5rem] px-2 py-1 justify-center cursor-pointer flex gap-1 transition-all rounded bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70"
             @click="expandNamespaces=!expandNamespaces">
            <svg xmlns="http://www.w3.org/2000/svg" :class="[expandNamespaces ? 'rotate-180' : '', 'transition-transform duration-200']" width="24" height="24" viewBox="0 0 24 24"
                 stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
            <span v-if="!expandNamespaces">
                {{ $t("mappings.namespace.show.more") }} 
            </span>
            <span v-else>
                {{ $t("mappings.namespace.show.less") }} 
            </span>
        </div>

        <div class="divider mt-0 mb-0"/>
        <SubHeader class="pb-1 flex items-center gap-1.5">
            {{ $t("mappings.version") }}
            <IconGitCherryPick class="-scale-100" :size="20"/>
        </SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
            <div class="flex gap-2 select-none justify-between">
                <label for="allow-snapshots"> {{ $t("dependencies.version.snapshots") }} </label>
                <input type="checkbox" v-model="allowSnapshots" id="allow-snapshots" aria-label="Enable Snapshots"/>
            </div>
        </div>

        <div class="mx-[-.25rem] bg-base-l2 rounded-lg">
            <div class="p-1 h-40 overflow-x-clip gradient-mask-b-80 overflow-y-scroll epic-scroller">
                <p v-for="v in applicableVersions"
                   :class="[version === v.version && v.hasTranslation ? 'font-medium' : 'font-normal opacity-80 decoration-base-500/60 hover:decoration-base-700/60 dark:decoration-base-dark-400/70 dark:hover:decoration-base-dark-600/60',
                    v.hasTranslation ? 'transition-all bg-base-700 dark:hover:bg-base-dark-600 bg-opacity-0 hover:bg-opacity-60 rounded-md cursor-pointer' : 'cursor-not-allowed line-through decoration-base-content hover:decoration-base-content dark:decoration-base-dark-content dark:hover:decoration-base-dark-content',
                    'px-2 py-1 underline underline-offset-2 decoration-2']"
                   @click="version = v.hasTranslation ? v.version : version">
                    {{
                        // noinspection TypeScriptValidateTypes
                        !v.hasTranslation ? $t("mappings.version.no.translation", {version: v.version}) : v.version
                    }}
                </p>
            </div>
        </div>

        <div class="divider mt-0 mb-0" v-if="mode === 'mappings'"/>
        <div class="mt-2" v-if="mode === 'mappings'">
            <SubHeader class="flex items-center gap-1.5">
                {{ $t("mappings.translation") }}
                <IconWorld :size="20"/>
            </SubHeader>
            <div class="pt-2 flex flex-col gap-3">
                <button :aria-selected="(translateMode ?? 'none') === 'none'" @click="translateMode = 'none'; translateAs = undefined; translateAsVersion = undefined"
                        class="selection-button py-2 px-3 flex-1 flex items-center gap-3 text-left rounded-lg bg-base-500 dark:bg-base-dark-200 bg-opacity-60 hover:bg-opacity-100
                dark:hover:bg-base-dark-400 dark:focus:bg-base-dark-400 transition-all duration-150 border-none select-none">
                    <IconArrowBarToRight class="flex-shrink-0" :size="20"/>
                    {{ $t("mappings.translation.none") }}
                </button>
                <button :aria-selected="(translateMode ?? 'none') === 'ns'" @click="translateMode = 'ns'; translateAs = undefined; translateAsVersion = undefined"
                        class="selection-button py-2 px-3 flex-1 flex items-center gap-3 text-left rounded-lg bg-base-500 dark:bg-base-dark-200 bg-opacity-60 hover:bg-opacity-100
                dark:hover:bg-base-dark-400 dark:focus:bg-base-dark-400 transition-all duration-150 border-none select-none">
                    <IconArrowBounce class="flex-shrink-0" :size="20"/>
                    {{ $t("mappings.translation.another.namespace") }}
                </button>
                <button :aria-selected="(translateMode ?? 'none') === 'ver'" @click="translateMode = 'ver'; translateAs = undefined; translateAsVersion = undefined"
                        class="selection-button py-2 px-3 flex-1 flex items-center gap-3 text-left rounded-lg bg-base-500 dark:bg-base-dark-200 bg-opacity-60 hover:bg-opacity-100
                dark:hover:bg-base-dark-400 dark:focus:bg-base-dark-400 transition-all duration-150 border-none select-none">
                    <IconArrowIteration class="flex-shrink-0" :size="20"/>
                    {{ $t("mappings.translation.another.version") }}
                </button>
            </div>
            <div v-if="(translateMode ?? 'none') === 'ns'" class="pt-4">
                <p class="text-xs font-bold uppercase"> {{ $t("mappings.translation.none") }} </p>
                <div :class="[
                translateAs === undefined ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
                    'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70 uppercase']"
                     @click="translateAs = undefined">
                    {{ $t("mappings.translation.n/a") }}
                </div>
                <div :class="[expandTranslations ? 'expanded' : '', 'expand-height']">
                    <div v-for="[group, nses] in namespacesGrouped" class="pb-1">
                        <p class="text-xs font-bold uppercase">{{ group }}</p>
                        <div v-for="ns in nses">
                            <div v-if="ns?.id !== namespace" :class="[
                                translateAs === ns.id ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
                                'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']"
                                 @click="translateAs = ns.id">
                                {{ localizeNamespace(ns) ?? "" }}
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mx-[-.5rem] px-2 py-1 justify-center cursor-pointer flex gap-1 transition-all rounded bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70"
                     @click="expandTranslations=!expandTranslations">
                    <svg xmlns="http://www.w3.org/2000/svg" :class="[expandTranslations ? 'rotate-180' : '', 'transition-transform duration-200']" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                    <span v-if="!expandTranslations">
                        {{ $t("mappings.namespace.show.more") }} 
                    </span>
                    <span v-else>
                        {{ $t("mappings.namespace.show.less") }} 
                    </span>
                </div>
            </div>
            <div v-if="(translateMode ?? 'none') === 'ver'" class="pt-4">
                <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
                    <div class="flex gap-2 select-none justify-between">
                        <label for="allow-snapshots"> {{ $t("dependencies.version.snapshots") }} </label>
                        <input type="checkbox" v-model="allowSnapshots" id="allow-snapshots" aria-label="Enable Snapshots"/>
                    </div>
                </div>

                <div class="mx-[-.25rem] bg-base-l2 rounded-lg">
                    <div class="p-1 h-40 overflow-x-clip gradient-mask-b-80 overflow-y-scroll epic-scroller">
                        <p v-for="v in ['n/a', ...applicableVersions.filter(poss => poss.version !== version).map(poss => poss.version)] as string[]"
                           :class="[translateAsVersion === v || (v === 'n/a' && translateAsVersion === undefined) ? 'font-medium' : 'font-normal opacity-80 decoration-base-500/60 hover:decoration-base-700/60 dark:decoration-base-dark-400/70 dark:hover:decoration-base-dark-600/60',
                                'transition-all bg-base-700 dark:hover:bg-base-dark-600 bg-opacity-0 hover:bg-opacity-60 rounded-md cursor-pointer',
                                'px-2 py-1 underline underline-offset-2 decoration-2',
                                v === 'n/a' && 'uppercase']"
                           @click="translateAsVersion = (v === 'n/a' ? undefined : v)">
                            {{ v === "n/a" ? $t("mappings.translation.n/a") : v }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import {computed, ref} from "vue"
import {storeToRefs} from "pinia"
import {applicableMappingsVersions, useMappingsStore} from "../../app/mappings-store"
import SubHeader from "../dependencies/SubHeader.vue"
import {allNamespaceGroups, hiddenNamespaceGroups, namespaceGroups, namespaceLocalizations} from "../../app/backend"
import {MappingsData, Namespace} from "../../app/mappings-data"
import {IconArrowBarToRight, IconArrowBounce, IconArrowIteration, IconBrandCodepen, IconDatabase, IconFileSad, IconGitCherryPick, IconTriangles, IconWorld} from "@tabler/icons-vue"
import {useI18n} from "vue-i18n"

const {data} = defineProps<{
    data: MappingsData
}>()

const expandNamespaces = ref(false)
const expandTranslations = ref(false)
const modeHover = ref(false)

const { t } = useI18n();

const {namespace, version, allowSnapshots, mode, translateMode, translateAs, translateAsVersion} = storeToRefs(useMappingsStore())

const namespaces = computed<Namespace[]>(() => data.namespaces)
const namespacesGrouped = computed<[string, Namespace[]][]>(() => {
    let groups = {} as { [group: string]: Namespace[] }
    for (let ns in namespaces.value) {
        let groupsApplicable = namespaceGroups[namespaces.value[ns].id] ?? "Others"
        if (typeof groupsApplicable === "string") {
            groupsApplicable = [groupsApplicable]
        }
        for (let group of groupsApplicable) {
            if (!groups[group]) {
                groups[group] = []
            }
            groups[group].push(namespaces.value[ns])
        }
    }
    for (let groupsKey in groups) {
        //sort
        groups[groupsKey].sort((a, b) => (localizeNamespace(a) ?? "").localeCompare(localizeNamespace(b) ?? ""))
    }
    return Object.entries(groups).sort((a, b) => allNamespaceGroups.indexOf(a[0]) - allNamespaceGroups.indexOf(b[0]))
})
const applicableVersions = computed(() => applicableMappingsVersions())

function localizeNamespace(namespace?: Namespace | string): string | undefined {
    if (typeof namespace === "string") {
        return t(`namespace.${namespace.toLowerCase()}`) || namespaceLocalizations[namespace.toLowerCase()] || namespace.toLowerCase()
    } else if (namespace) {
        let id = namespace.id
        return t(`namespace.${id.toLowerCase()}`) || namespaceLocalizations[id.toLowerCase()] || id.toLowerCase()
    } else {
        return undefined
    }
}

function selectMode(_mode: string) {
    mode.value = _mode
    if (mode.value === "stacktrace") {
        translateMode.value = "none"
        translateAs.value = undefined
        translateAsVersion.value = undefined
    }
}
</script>

<style scoped>
.checkbox {
    vertical-align: -0.1rem;
}

.selection-button[aria-selected=true] {
    @apply ring-[0.15rem] ring-offset-2 ring-base-content dark:ring-base-dark-900;
    @apply outline-0;
}
</style>