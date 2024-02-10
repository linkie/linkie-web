<template>
    <NavbarButton :hover-dim="false">
        <div class="relative">
            <div @click="dropdownVisible = !dropdownVisible" class="hover:opacity-70">
                <slot/>
            </div>

            <transition enter-active-class="transition ease-out duration-100" enter-from-class="transform opacity-0 scale-95"
                        enter-to-class="transform opacity-100 scale-100" leave-active-class="transition ease-in duration-75"
                        leave-from-class="transform opacity-100 scale-100" leave-to-class="transform opacity-0 scale-95">
                <div v-if="dropdownVisible"
                     class="locale-dropdown absolute right-0 z-10 mt-3 min-w-56 max-w-80 divide-y-2 divide-black/20 origin-top-right rounded-md bg-base-100 dark:bg-base-dark-300 dark:text-base-dark-content shadow-xl ring-4 ring-black ring-opacity-20">
                    <div class="py-1">
                        <a v-for="option in options"
                           @click="option.onClick(); dropdownVisible = false"
                           class="cursor-pointer block px-4 py-2 hover:bg-base-300 dark:hover:bg-base-dark-400 transition-colors flex gap-2 items-center">
                            <p class="flex-1 pr-2">{{ option.name }}</p>
                            <slot :name="`option-${option.id}`"/>
                        </a>
                        <slot name="option-extra"/>
                    </div>
                    <div class="py-1">
                        <slot name="option-new-group"/>
                    </div>
                </div>
            </transition>
        </div>
    </NavbarButton>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import NavbarButton from "./NavbarButton.vue"

export interface DropdownOption {
    id: string,
    name: string,
    onClick: () => void,
}

export default defineComponent({
    name: "NavbarDropdown",
    components: {NavbarButton},
    data() {
        return {
            dropdownVisible: false,
        }
    },
    props: {
        options: {
            type: Array as () => DropdownOption[],
            required: true,
        },
    },
})
</script>

<style scoped>
</style>