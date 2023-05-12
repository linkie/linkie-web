<template>
    <TransitionGroup name="alert" tag="div">
        <div class="fixed top-20 left-10 z-5" v-for="alert in alerts">
            <div class="items-center flex gap-4 mt-4 bg-base-100 dark:bg-base-dark-300 p-3 shadow-lg rounded-md ring-4 ring-black ring-opacity-20" :class="{
                        'bg-lime-300 dark:bg-lime-300 text-lime-900 dark:text-lime-900': alert.type === 'success',
                        'bg-orange-300 dark:bg-orange-300 text-orange-900 dark:text-orange-900': alert.type === 'warning',
                        'bg-red-300 dark:bg-red-300 text-red-900 dark:text-red-900': alert.type === 'error',
                    }">
                <div>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-info flex-shrink-0 w-6 h-6"
                         v-if="alert.type === 'info'">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"
                         v-if="alert.type === 'success'">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"
                         v-if="alert.type === 'warning'">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                    </svg>
                    <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current flex-shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"
                         v-if="alert.type === 'error'">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                </div>
                <span class="font-medium">{{ alert.message }}</span>
                <button class="rounded px-3 py-2 bg-black bg-opacity-0 hover:bg-opacity-10 transition-[background-color]" @click="removeAlert(alert)">Close
                </button>
            </div>
        </div>
    </TransitionGroup>

    <div class="fixed top-20 right-10 z-5">
        <TransitionGroup name="list" tag="div">
            <div v-for="notification in Object.keys(notifications)" :key="notifications[notification as unknown as number].message"
                 @click="removeNotification(notification as unknown as number)"
                 class="mt-4 bg-base-100 dark:bg-base-dark-300 p-3 shadow-lg rounded-md ring-4 ring-black ring-opacity-20">
                {{ notifications[notification as unknown as number].message }}
            </div>
        </TransitionGroup>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {Alert, useAlertsStore} from "../app/alerts-store"
import {mapActions, mapState} from "pinia"
import {useNotificationStore} from "../app/notification-store"

export default defineComponent({
    name: "Alerts",
    data() {
        return {
            timer: undefined as any,
        }
    },
    computed: {
        ...mapState(useAlertsStore, ["alerts"]),
        ...mapState(useNotificationStore, ["notifications"]),
    },
    methods: {
        getAlertFor(type: string): Alert[] {
            return this.alerts.filter((alert: Alert) => alert.type === type)
        },
        ...mapActions(useAlertsStore, ["removeAlert"]),
        ...mapActions(useNotificationStore, ["removeNotification"]),
    },
})
</script>

<style>
.list-move, /* apply transition to moving elements */
.list-enter-active,
.list-leave-active {
    transition: all 0.5s ease;
}

.list-enter-from,
.list-leave-to {
    opacity: 0;
    transform: translateX(30px);
}

.alert-move, /* apply transition to moving elements */
.alert-enter-active,
.alert-leave-active {
    transition: all 0.5s ease;
}

.alert-enter-from,
.alert-leave-to {
    opacity: 0;
    transform: translateX(-30px);
}
</style>