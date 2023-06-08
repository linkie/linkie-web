<template>
    <div class="flex flex-col">
        <SubHeader class="mt-[-.25rem]"> {{ $t("dependencies.loader") }} </SubHeader>

        <div v-for="l in loaders" :class="[
            loader === l ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
            'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']" @click="loader = l">
            {{ $t("loader." + l) }}
        </div>

        <div :class="[loader === 'forge' ? 'expanded' : '', 'expand-height']">
            <div class="divider mt-0 mb-0"/>
            <SubHeader> {{ $t("dependencies.build.system") }} </SubHeader>

            <div :class="[
            !forgeGradle ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
            'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 capitalize rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']" @click="forgeGradle = false">
                {{ $t("dependencies.build.system.architectury.loom") }}
            </div>

            <div :class="[
            forgeGradle ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
            'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 capitalize rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']" @click="forgeGradle = true">
                {{ $t("dependencies.build.system.forge.gradle") }}
            </div>
        </div>

        <div class="divider mt-0 mb-0"/>
        <SubHeader class="pb-1"> {{ $t("dependencies.version") }} </SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap" :class="['expand-height', loader === 'fabric' ? 'expanded' : '']">
            <div class="flex gap-2 select-none justify-between pb-2">
                <label for="allow-snapshots"> {{ $t("dependencies.version.snapshots") }} </label>
                <input type="checkbox" v-model="allowSnapshots" id="allow-snapshots" aria-label="Enable Snapshots"/>
            </div>
        </div>

        <div class="mx-[-.25rem] bg-base-l2 rounded-lg">
            <div class="p-1 h-52 overflow-x-clip gradient-mask-b-80 overflow-y-scroll epic-scroller">
                <p v-for="v in applicableVersions"
                   :class="[version === v ? 'font-medium' : 'font-normal opacity-80 decoration-base-500/60 hover:decoration-base-700/60 dark:decoration-base-dark-400/70 dark:hover:decoration-base-dark-600/60',
                    'underline underline-offset-2 decoration-2 transition-all bg-base-700 dark:hover:bg-base-dark-600 bg-opacity-0 hover:bg-opacity-60 px-2 py-1 rounded-md cursor-pointer']"
                   @click="version = v">
                    {{ v }}
                </p>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {useDependencySearchStore} from "../../app/dependency-store"
import {mapWritableState} from "pinia"
import SubHeader from "./SubHeader.vue"
import {DependencySearchData} from "../../app/dependencies-data"

export default defineComponent({
    name: "DependencyFilterBlock",
    components: {SubHeader},
    computed: {
        ...mapWritableState(useDependencySearchStore, ["loader", "version", "allowSnapshots", "forgeGradle"]),
        loaders(): string[] {
            return Object.keys(this.searchData.versions)
        },
        applicableVersions(): string[] {
            let {loader, allowSnapshots} = useDependencySearchStore()
            if (!loader) return []
            let versions = this.searchData.versions[loader] ?? []
            if (versions && !allowSnapshots) {
                versions = versions.filter(entry => entry.stable)
            }
            return versions.map(entry => entry.version)
        },
    },
    props: {
        searchData: {
            type: Object as PropType<DependencySearchData>,
            required: true,
        },
    }
})
</script>

<style scoped>
.select-label {
    display: flex !important;
}
</style>