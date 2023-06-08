<template>
    <div v-if="searching" class="m-10 flex flex-col items-center">
        <!--Searching-->
        <div class="h-4"/>
        <svg xmlns="http://www.w3.org/2000/svg" class="animate-spin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
             fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <path d="M10 17a3 3 0 1 1 -1.543 -2.623l2.087 -3.754a3 3 0 0 1 1.456 -5.623a3 3 0 0 1 1.457 5.623l2.087 3.754a3 3 0 1 1 -1.538 2.8l-.006 -.177h-4z"></path>
            <path d="M17 17v.01"></path>
            <path d="M7 17v.01"></path>
            <path d="M12 8v.01"></path>
        </svg>
        <p class="font-bold pt-8"> {{ $t("mappings.searching") }} </p>
        <p> {{ $t("mappings.searching.subtitle") }} </p>
    </div>
    <div v-else class="m-10 flex flex-col items-center">
        <!--No Results-->
        <svg xmlns="http://www.w3.org/2000/svg" class="m-4" width="24" height="24" viewBox="0 0 24 24"
             stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <circle cx="12" cy="12" r="9"></circle>
            <line x1="5.7" y1="5.7" x2="18.3" y2="18.3"></line>
        </svg>
        <p class="font-bold pt-4"> {{ $t("mappings.searching.none") }} </p>
        <p v-if="!hasQuery" class="text-center"> {{ $t("mappings.searching.none.try") }} </p>
        <p v-else class="text-center"> {{ $t("mappings.searching.none.mistake") }} </p>
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