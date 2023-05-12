<template>
    <div class="grid grid-cols-2 stepper gap-3 sm:gap-4">
        <div v-for="i in (4 * steps)">
            <div v-if="i % 4 === 1" class="w-6 h-6 sm:w-8 sm:h-8 text-sm sm:text-md grid items-center justify-center rounded-full bg-base-400 text-base-dark-400 
                dark:bg-base-dark-300 dark:text-base-dark-content font-bold element"
                 :aria-disabled="step < (i + 3) / 4">{{ (i + 3) / 4 }}
            </div>
            <div v-else-if="i % 4 === 2" :aria-disabled="step < (i + 2) / 4" class="w-full h-full element">
                <slot :name="`title-${(i - 2) / 4}`"/>
            </div>
            <div v-else-if="i % 4 === 3" class="w-[.2rem] mx-auto h-full pb-[-2rem] bg-base-400 dark:bg-base-dark-300 rounded-full element "
                 :aria-disabled="step < (i + 1) / 4"/>
            <div v-else-if="i % 4 === 0" class="w-full element" :aria-disabled="step < i / 4">
                <slot :name="`content-${(i - 4) / 4}`"/>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "GeneratorStepper",
    props: {
        step: {
            type: Number,
            required: true,
        },
        steps: {
            type: Number,
            required: true,
        },
    },
})
</script>

<style scoped>
.stepper {
    grid-template-columns: 1fr minmax(0, auto);
}

.element {
    @apply transition-opacity;
}

.element[aria-disabled="true"] {
    @apply opacity-30 pointer-events-none select-none;
}
</style>