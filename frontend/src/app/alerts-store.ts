import {defineStore} from "pinia"

export interface Alert {
    message: string,
    type: "info" | "success" | "warning" | "error",
}

export interface State {
    alerts: Alert[],
}

function newState(): State {
    return {
        alerts: [],
    }
}

export const useAlertsStore = defineStore({
    id: "alerts",
    state: newState,
    actions: {
        addAlert(alert: Alert) {
            this.alerts = this.alerts.filter(value => value.message != alert.message)
            this.alerts.push(alert)
        },
        removeAlert(alert: Alert) {
            this.alerts = this.alerts.filter(value => value.message != alert.message)
        },
    },
})
