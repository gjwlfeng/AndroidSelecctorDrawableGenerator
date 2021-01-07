package com.zf.androidplugin.selectdrawable;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import javax.annotation.Nullable;

public class MyNotifier {

    public static void notifyError(@Nullable Project project, NotificationType notificationType, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(Constants.NOTIFICATION_GROUP_NAME)
                .createNotification(content, notificationType)
                .notify(project);
    }

}