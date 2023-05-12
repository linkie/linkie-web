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
            <div class="px-4">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                     stroke-width="2"
                     stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                    <circle cx="10" cy="10" r="7"></circle>
                    <line x1="21" y1="21" x2="15" y2="15"></line>
                </svg>
            </div>
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
    @apply ring-transparent !important;
}
</style>