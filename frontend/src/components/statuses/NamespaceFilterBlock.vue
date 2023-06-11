<template>
    <div class="flex flex-col">
        <SubHeader class="mt-[-.25rem]"> {{ $t("mappings.namespace") }}</SubHeader>

        <div class="flex gap-2 select-none justify-between mb-2">
            <label for="allow-snapshots"> {{ $t("dependencies.version.snapshots") }} </label>
            <input type="checkbox" :checked="snapshots" @change="setSnapshots!!(($event.target as any).checked)"
                   id="allow-snapshots" aria-label="Enable Snapshots"/>
        </div>

        <div v-for="namespace in [null, ...mappingsData.namespaces]">
            <div v-if="!namespace || namespace!!.supportsSource" :class="[
            ns === namespace?.id ? 'font-medium' : 'font-normal opacity-80 decoration-base-400/50 hover:decoration-base-500/70 dark:decoration-base-dark-400/50 dark:hover:decoration-base-dark-400/70',
            'underline underline-offset-2 decoration-2 cursor-pointer mx-[-.5rem] px-2 py-1 rounded transition-all bg-base-500 dark:hover:bg-base-dark-400 bg-opacity-0 hover:bg-opacity-70']"
                 @click="setNs!!(namespace?.id as (string | null))">
                <span v-if="!!namespace">
                    {{ namespaceLocalizations[namespace.id.toLowerCase()] || namespace.id.toLowerCase() }}
                </span>
                <span v-else>
                    Next in Queue
                </span>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {mapState} from "pinia"
import {useMappingsDataStore} from "../../app/mappings-data"
import {namespaceLocalizations} from "../../app/backend"
import SubHeader from "../dependencies/SubHeader.vue"

export default defineComponent({
    name: "NamespaceFilterBlock",
    components: {SubHeader},
    data() {
        return {
            namespaceLocalizations,
        }
    },
    computed: {
        ...mapState(useMappingsDataStore, ["mappingsData"]),
    },
    props: {
        ns: {
            type: Object as PropType<string | null>,
        },
        setNs: {
            type: Object as PropType<(ns: string | null) => void>,
        },
        snapshots: {
            type: Boolean,
        },
        setSnapshots: {
            type: Object as PropType<(snapshots: boolean) => void>,
        },
    },
})
</script>

<style scoped>
.select-label {
    display: flex !important;
}
</style>