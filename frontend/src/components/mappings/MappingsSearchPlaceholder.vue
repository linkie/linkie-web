<template>
    <div v-if="searching" class="m-10 flex flex-col items-center">
        <!--Searching-->
        <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-search m-4" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <circle cx="10" cy="10" r="7"></circle>
            <line x1="21" y1="21" x2="15" y2="15"></line>
        </svg>
        <p class="font-bold"> {{ $t("mappings.searching") }} </p>
        <p> {{ $t("mappings.searching.subtitle") }} </p>
    </div>
    <div v-else class="m-10 flex flex-col items-center">
        <!--No Results-->
        <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-ban m-4" width="24" height="24" viewBox="0 0 24 24"
             stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <circle cx="12" cy="12" r="9"></circle>
            <line x1="5.7" y1="5.7" x2="18.3" y2="18.3"></line>
        </svg>
        <p class="font-bold"> {{ $t("mappings.searching.none") }} </p>
        <p v-if="!hasQuery"> {{ $t("mappings.searching.none.try") }} </p>
        <p v-else> {{ $t("mappings.searching.none.mistake") }} </p>
    </div>
</template>

<script lang="ts">

import {defineComponent} from "vue"

export default defineComponent({
    name: "MappingsSearchPlaceholder",
    props: {
        searching: Boolean,
        hasQuery: Boolean,
    },
})
</script>

<style scoped>
</style>