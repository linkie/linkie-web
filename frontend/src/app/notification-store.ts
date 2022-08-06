import {defineStore} from "pinia"

export interface Notification {
    message: string,
}

export interface State {
    notifications: Notification[],
}

function newState(): State {
    return {
        notifications: [],
    }
}

export const useNotificationStore = defineStore({
    id: "notification",
    state: newState,
    actions: {
        addNotification(alert: Notification) {
            this.notifications.push(alert)
        },
        removeNotification(index: number) {
            this.notifications = this.notifications.filter((value, i) => i != index)
        },
    },
})
