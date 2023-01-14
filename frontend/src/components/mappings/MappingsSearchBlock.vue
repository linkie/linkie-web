<template>
    <div class="px-5 pb-6">
        <div class="bg-base-100 shadow-xl rounded-lg overflow-x-auto">
            <div class="flex px-2 pt-2">
                <span class="flex items-center justify-center ml-2 mr-3 font-bold whitespace-nowrap"> {{ $t("mappings.search.type") }} </span>
                <MappingsFilterPill :enabled="allowClasses" :text="$t('mappings.search.type.classes')" @click="allowClasses = !allowClasses"/>
                <MappingsFilterPill :enabled="allowMethods" :text="$t('mappings.search.type.methods')" @click="allowMethods = !allowMethods"/>
                <MappingsFilterPill :enabled="allowFields" :text="$t('mappings.search.type.fields')" @click="allowFields = !allowFields"/>
                <div class="border-base-100 border-[0.25rem] border-solid"/>
            </div>
            <div class="flex flex-wrap items-center">
                <div class="flex-none px-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-search" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2"
                         stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <circle cx="10" cy="10" r="7"></circle>
                        <line x1="21" y1="21" x2="15" y2="15"></line>
                    </svg>
                </div>
                <input type="text" placeholder="Search..." class="input rounded-lg flex-1 px-0 text-lg" :value="searchText"
                       @keyup="searchTimeOut($event)" @keydown.space.prevent autofocus/>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapState, mapWritableState} from "pinia"
import {useMappingsStore} from "../../app/mappings-store"
import MappingsFilterPill from "./MappingsFilterPill.vue"

export default defineComponent({
    name: "MappingsSearchBlock",
    data() {
        return {
            timer: undefined as any,
        }
    },
    components: {
        MappingsFilterPill,
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
}
</style>