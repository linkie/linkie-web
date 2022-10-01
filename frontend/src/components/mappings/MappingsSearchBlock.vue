<template>
    <div class="px-5 pt-6">
        <div class="card bg-base-100 shadow-xl rounded-lg overflow-x-auto">
            <div class="flex pl-2 pt-2">
                <span class="flex items-center justify-center ml-2 mr-3 font-bold">Search Type: </span>
                <a :class="['m-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 shadow transition-all', allowClasses ? 'bg-neutral text-white font-medium' : 'filter-chips']"
                   @click="allowClasses = !allowClasses">
                    <div class="flex mx-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <line v-if="!allowClasses" x1="3" y1="3" x2="21" y2="21"></line>
                            <path d="M5.5 5h13a1 1 0 0 1 .5 1.5l-5 5.5l0 7l-4 -3l0 -4l-5 -5.5a1 1 0 0 1 .5 -1.5"></path>
                        </svg>
                        <span class="w-20">Classes</span>
                    </div>
                </a>
                <a :class="['m-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 shadow transition-all', allowMethods ? 'bg-neutral text-white font-medium' : 'filter-chips']"
                   @click="allowMethods = !allowMethods">
                    <div class="flex mx-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <line v-if="!allowMethods" x1="3" y1="3" x2="21" y2="21"></line>
                            <path d="M5.5 5h13a1 1 0 0 1 .5 1.5l-5 5.5l0 7l-4 -3l0 -4l-5 -5.5a1 1 0 0 1 .5 -1.5"></path>
                        </svg>
                        <span class="w-20">Methods</span>
                    </div>
                </a>
                <a :class="['m-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 shadow transition-all', allowFields ? 'bg-neutral text-white font-medium' : 'filter-chips']"
                   @click="allowFields = !allowFields">
                    <div class="flex mx-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <line v-if="!allowFields" x1="3" y1="3" x2="21" y2="21"></line>
                            <path d="M5.5 5h13a1 1 0 0 1 .5 1.5l-5 5.5l0 7l-4 -3l0 -4l-5 -5.5a1 1 0 0 1 .5 -1.5"></path>
                        </svg>
                        <span class="w-20">Fields</span>
                    </div>
                </a>
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

export default defineComponent({
    name: "MappingsSearchBlock",
    data() {
        return {
            timer: undefined as any,
        }
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

.filter-chips:hover {
    filter: brightness(0.87);
}
</style>