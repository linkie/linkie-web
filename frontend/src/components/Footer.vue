<template>
    <div class="bg-base-300 dark:bg-base-dark-200 text-base-dark-600 dark:text-base-dark-900 text-sm px-4 w-full mt-6">
        <footer class="max-w-[78rem] w-full mx-auto">
            <div class="items-center font-medium py-4 flex">
                <div class="flex-1 flex flex-col gap-1">
                    <p class="flex flex-wrap gap-1">
                        <span class="whitespace-nowrap">Copyright © 2020 - 2024</span>
                        <span class="whitespace-nowrap">Linkie & shedaniel.</span>
                    </p>
                    <p>
                        <span class="whitespace-nowrap">
                            Made with <IconHeartFilled class="text-red-400 inline-block" size="18"/> in Hong Kong.
                        </span>
                    </p>
                </div>
                <a href="https://twitter.com/shedaniel_/">
                    <IconBrandTwitter/>
                </a>
                <a href="mailto:daniel@shedaniel.me" class="ml-3">
                    <IconMail/>
                </a>
                <a href="https://github.com/linkie/linkie-web/" class="ml-3">
                    <IconBrandGithub/>
                </a>
                <a href="/oss" class="ml-3">
                    <IconBrandOpenSource/>
                </a>
            </div>
        </footer>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {IconBrandGithub, IconBrandOpenSource, IconBrandTwitter, IconHeartFilled, IconMail} from "@tabler/icons-vue"

export default defineComponent({
    name: "Footer",
    components: {IconBrandGithub, IconBrandOpenSource, IconBrandTwitter, IconHeartFilled, IconMail},
})
</script>

<style scoped>

</style>