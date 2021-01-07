package com.zf.androidplugin.selectdrawable;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableStatus;
import com.zf.androidplugin.selectdrawable.i18n.I18n;
import com.zf.androidplugin.selectdrawable.i18n.ResourceI18n;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Lenovo on 2016/1/8.
 */
public class SelectDrawableAction extends AnAction {
    List<DrawableFile> drawableFileList = new ArrayList<>();
    VirtualFile firstParent = null;
    String selectorDrawableName;

    boolean isDirectory = false;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (isDirectory) {
            String message = I18n.getString(ResourceI18n.PLEASE_DONT_SELECT_FOLDER);
            showErrorDialog(anActionEvent, message);
            return;
        }

        final AnActionEvent finalAnActionEvent = anActionEvent;
        final Project project = anActionEvent.getProject();

        boolean isNeedInputDialog = true;
        String title = I18n.getString(ResourceI18n.SET_TITLE);
        String tipMessage = I18n.getString(ResourceI18n.PLEASE_ENTER_SELECTOR_DRAWABLE_NAME);
        while (isNeedInputDialog) {

            String initialValue = null;
            if (!drawableFileList.isEmpty()) {
                for (DrawableFile drawableFile : drawableFileList) {
                    if (drawableFile.getDrawableStatus() == DrawableStatus.none) {
                        initialValue = drawableFile.getSimpleName();
                    }
                }

                if (initialValue == null) {
                    initialValue = drawableFileList.get(0).getSimpleName();
                }
            }

            selectorDrawableName = Messages.showInputDialog(project, tipMessage, title, Messages.getQuestionIcon(), initialValue, null);
            if (selectorDrawableName != null) {
                if ("".equals(selectorDrawableName.trim())) {
                    String message = I18n.getString(ResourceI18n.PLEASE_ENTER_SELECTOR_DRAWABLE_NAME);
                    showErrorDialog(anActionEvent, message);
                } else if (FileOperation.isFindChild(firstParent, FileOperation.addSuffixXml(selectorDrawableName))) {
                    String message = I18n.getString(ResourceI18n.FILE_ALREADY_EXISTS);
                    showErrorDialog(anActionEvent, message);
                } else if (!FileOperation.isValidFileName(FileOperation.addSuffixXml(selectorDrawableName))) {
                    String message = I18n.getString(ResourceI18n.FILE_NAME_INVALID);
                    showErrorDialog(anActionEvent, message);
                } else {
                    isNeedInputDialog = false;
                }
            } else {
                isNeedInputDialog = false;
            }
        }
        if (selectorDrawableName == null) {
            return;
        }
        selectorDrawableName = FileOperation.addSuffixXml(selectorDrawableName);
        Collections.sort(drawableFileList);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                //创建drawable 文件夹
                VirtualFile virtualFile = null;
                try {
                    virtualFile = FileOperation.creteDir(firstParent, Constants.DRAWABLE);
                } catch (IOException e) {
                    String message = I18n.getString(ResourceI18n.CREATE_DRAWABLE_DIR_FAILED);
                    showErrorDialog(finalAnActionEvent, message);
                    e.printStackTrace();
                    return;
                }
                //创建 selector 文件
                VirtualFile selectorVirtualFile = null;
                try {
                    selectorVirtualFile = FileOperation.creteFile(virtualFile, selectorDrawableName);
                } catch (IOException e) {
                    String message = I18n.getString(ResourceI18n.CREATE_SELECTOR_DRAWABLE_FILE_FAILED);
                    showErrorDialog(finalAnActionEvent, message);
                    e.printStackTrace();
                    return;
                }
                //生成selector文件内容
                try {
                    SelectorDrawableGenerator.generate(drawableFileList, selectorVirtualFile);
                    //打开文件
                    FileOperation.openFile(project, selectorVirtualFile);
                } catch (IOException e) {
                    String message = I18n.getString(ResourceI18n.GENERATE_SELECTOR_DRAWABLE_FILE_CONTENT_FAIL);
                    showErrorDialog(finalAnActionEvent, message);
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void update(AnActionEvent e) {
        drawableFileList.clear();
        selectorDrawableName = null;
        isDirectory = false;

        e.getPresentation().setEnabled(true);

        VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (virtualFiles == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        DrawableFile drawableFile = new DrawableFile();
        for (VirtualFile virtualFile : virtualFiles) {
            //如果是文件夹，则不可用
            if (virtualFile.isDirectory()) {
                isDirectory = true;
                break;
            }

            firstParent = virtualFile.getParent();
            if ((!firstParent.exists()) || (!firstParent.isDirectory())) {
                e.getPresentation().setEnabled(false);
                break;
            }

            String name = firstParent.getName();
            Matcher matcher = Constants.VALID_FOLDER_PATTERN.matcher(name);
            if (!matcher.matches()) {
                e.getPresentation().setEnabled(false);
                return;
            }

            VirtualFile secondParent = firstParent.getParent();
            if (secondParent == null || (!secondParent.isDirectory()) || (!secondParent.getName().equals(Constants.RES))) {
                e.getPresentation().setEnabled(false);
                break;
            }

            String simpleName = virtualFile.getNameWithoutExtension();
            String replacePoint9Name = simpleName.replace(Constants.POINT9SUFFX, "");
            DrawableStatus drawableStatusByName = DrawableStatus.getDrawableStatusByName(replacePoint9Name);

            DrawableFile clone = (DrawableFile) drawableFile.clone();
            clone.setSimpleName(replacePoint9Name);
            clone.setStatus(true);
            clone.setFullPathName(virtualFile.getPresentableUrl());
            clone.setDrawableStatus(drawableStatusByName);

            if (!drawableFileList.contains(clone))
                drawableFileList.add(clone);
        }

        if (drawableFileList.isEmpty()) {
            e.getPresentation().setEnabled(false);
        }
    }

    private void showInfoDialog(AnActionEvent e, String content) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        MyNotifier.notifyError(project, NotificationType.INFORMATION, content);
    }

    private void showErrorDialog(AnActionEvent e, String content) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        MyNotifier.notifyError(project, NotificationType.ERROR, content);
    }

//    private void showInfoDialog(String text, AnActionEvent e) {
//        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));
//
//        if (statusBar != null)
//            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.INFO, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
//    }
//
//    private void showErrorDialog(String text, AnActionEvent e) {
//        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));
//
//        if (statusBar != null)
//            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.ERROR, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
//    }
}
