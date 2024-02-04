<template>
    <div class="bg-base-l1 shadow-sm rounded-lg overflow-x-auto epic-scroller">
        <div class="flex px-2 pt-2">
            <span class="flex-1 items-center justify-start grid ml-2 mr-3 font-bold whitespace-nowrap"> {{ $t("mappings.search.type") }} </span>
            <div class="flex">
                <MappingsFilterPill :enabled="allowClasses" :text="$t('mappings.search.type.classes')" @click="allowClasses = !allowClasses"/>
                <MappingsFilterPill :enabled="allowMethods" :text="$t('mappings.search.type.methods')" @click="allowMethods = !allowMethods"/>
                <MappingsFilterPill :enabled="allowFields" :text="$t('mappings.search.type.fields')" @click="allowFields = !allowFields"/>
                <div class="border-base-l1 border-[0.25rem] border-solid"/>
            </div>
        </div>
        <div class="flex flex-nowrap items-center">
            <IconSearch class="mx-4 flex-shrink-0"/>
            <input type="text" placeholder="Search..." class="input h-12 bg-transparent rounded-lg flex-1 px-0 text-lg" :value="searchText"
                   @keyup="searchTimeOut($event)" @keydown.space.prevent autofocus/>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapState, mapWritableState} from "pinia"
import {useMappingsStore} from "../../app/mappings-store"
import MappingsFilterPill from "./MappingsFilterPill.vue"
import {IconSearch} from "@tabler/icons-vue"

export default defineComponent({
    name: "MappingsSearchBlock",
    data() {
        return {
            timer: undefined as any,
        }
    },
    components: {
        MappingsFilterPill,
        IconSearch,
    },
    computed: {
        ...mapWritableState(useMappingsStore, ["allowClasses", "allowFields", "allowMethods"]),
        ...mapState(useMappingsStore, ["searchText"]),
    },
    methods: {
        searchTimeOut(event: any) {
            clearTimeout(this.timer)

            this.timer = setTimeout(() => {
                useMappingsStore().searchText = (event.target as any)?.value?.replaceAll(" ", "")
            }, 100)
        },
    },
})
</script>

<style scoped>
.input {
    outline: 0 !important;
    border: 0 !important;
    @apply ring-transparent !important;
}
</style>