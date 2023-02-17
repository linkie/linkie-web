<template>
    <div class="max-w-4xl px-5 pt-6 mx-auto" v-for="alert in alerts">
        <div class="alert shadow-lg rounded-lg" :class="{
                        'alert-success': alert.type === 'success',
                        'alert-warning': alert.type === 'warning',
                        'alert-error': alert.type === 'error',
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
                <span>{{ alert.message }}</span>
            </div>
            <div class="flex-none">
                <button class="btn btn-ghost btn-sm" @click="removeAlert(alert)">Close</button>
            </div>
        </div>
    </div>

    <div class="fixed top-20 right-10 z-5">
        <TransitionGroup name="list" tag="div">
            <div v-for="notification in Object.keys(notifications)" :key="notifications[notification as unknown as number].message" @click="removeNotification(notification as unknown as number)"
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
        ...mapActions(useNotificationStore, ["removeNotification"])
    }
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
</style>