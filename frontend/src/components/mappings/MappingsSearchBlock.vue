<template>
    <div class="px-5 pt-6">
        <div class="card bg-base-100 shadow-xl rounded-lg overflow-x-auto">
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
                <input type="text" placeholder="Search..." class="input rounded-lg flex-1 px-0" :value="searchText"
                       @keyup="searchTimeOut($event)" autofocus/>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapState} from "pinia"
import {useMappingsStore} from "../../app/mappings-store"

export default defineComponent({
    name: "MappingsSearchBlock",
    data() {
        return {
            timer: undefined as any,
        }
    },
    computed: {
        ...mapState(useMappingsStore, ["searchText"]),
    },
    methods: {
        searchTimeOut(event: any) {
            clearTimeout(this.timer)

            this.timer = setTimeout(() => {
                useMappingsStore().searchText = (event.target as any)?.value
            }, 1000)
        },
    },
})
</script>

<style scoped>
.input {
    outline: 0 !important;
}
</style>