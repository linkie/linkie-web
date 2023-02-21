import copy from "copy-to-clipboard";
import {useNotificationStore} from "./notification-store";
import {VueI18n} from "vue-i18n"

export function copyAs(i18n: VueI18n, str: string | undefined) {
    if (str === undefined) return;
    copy(str);
    useNotificationStore().addNotification({message: i18n.t("message.copy.clipboard")});
}