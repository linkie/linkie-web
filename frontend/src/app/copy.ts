import copy from "copy-to-clipboard";
import {useNotificationStore} from "./notification-store";

export function copyAs(str: string) {
    copy(str);
    useNotificationStore().addNotification({message: "Copied to clipboard"});
}