<script lang="ts">
import {defineComponent} from "vue"

import Home from "./routes/Home.vue"
import Dependencies from "./routes/Dependencies.vue"
import Mappings from "./routes/Mappings.vue"
import OpenSourceLicenses from "./routes/OpenSourceLicenses.vue"
import NotFound from "./routes/NotFound.vue"
import {Alert, useAlertsStore} from "./app/alerts-store"
import {mapActions, mapState} from "pinia"

import Footer from "./components/Footer.vue"
import Navbar from "./components/Navbar.vue"
import {useNotificationStore} from "./app/notification-store"

const routes: { [route: string]: any; } = {
    "/": Home,
    "/dependencies": Dependencies,
    "/mappings": Mappings,
    "/oss": OpenSourceLicenses,
}

export default defineComponent({
    data() {
        return {
            current: window.location.pathname as string,
        }
    },
    components: {Footer, Navbar},
    computed: {
        currentView() {
            return routes[this.current || "/"] || NotFound
        },
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
    mounted() {
        let theme = localStorage.getItem("theme")
        if (theme) {
            document.documentElement.setAttribute("data-theme", theme)
        } else {
            localStorage.setItem("theme", "cupcake")
            document.documentElement.setAttribute("data-theme", "cupcake")
        }
    },
})

</script>

<template>
    <div class="h-screen flex flex-col">
        <Navbar/>

        <div class="flex-1 overflow-y-auto overflow-x-auto">
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

            <div class="absolute right-10 z-10">
                <TransitionGroup name="list" tag="div">
                    <div v-for="notification in Object.keys(notifications)" @click="removeNotification(notification as unknown as number)"
                         class="toast mt-4 bg-base-100 p-5 shadow-2xl rounded-xl">
                        {{ notifications[notification as unknown as number].message }}
                    </div>
                </TransitionGroup>
            </div>

            <component :is="currentView"/>
        </div>
        <Footer/>
    </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap');

body {
    @apply bg-base-200
}

.select {
    outline: 0 !important;
    border: 0 !important;
}

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
