<template>
    <div class="bg-base-300 w-full mt-6">
        <footer class="max-w-[78rem] w-full mx-auto">
            <div class="items-center font-medium py-4 flex">
                <p class="flex-1">Copyright Linkie & shedaniel © 2020 - 2022. All right reserved.</p>
                <a href="/oss">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-brand-open-source" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round" aria-label="Open Source Licenses">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <path d="M12 3a9 9 0 0 1 3.618 17.243l-2.193 -5.602a3 3 0 1 0 -2.849 0l-2.193 5.603a9 9 0 0 1 3.617 -17.244z"></path>
                    </svg>
                </a>
            </div>
        </footer>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "Footer",
})
</script>

<style scoped>

</style>