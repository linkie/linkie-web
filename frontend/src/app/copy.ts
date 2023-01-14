import copy from "copy-to-clipboard";
import {useNotificationStore} from "./notification-store";

export function copyAs(str: string | undefined) {
    if (str === undefined) return;
    copy(str);
    useNotificationStore().addNotification({message: "Copied to clipboard"});
}