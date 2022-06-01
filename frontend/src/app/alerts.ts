import {Alert, useAlertsStore} from "./alerts-store"

export function addAlert(alert: Alert) {
    useAlertsStore().addAlert(alert)
}
