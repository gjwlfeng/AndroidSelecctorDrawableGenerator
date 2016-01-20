package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.zf.androidplugin.selectdrawable.dto.DrawableFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableStatus;
import com.zf.androidplugin.selectdrawable.i18n.I18n;
import com.zf.androidplugin.selectdrawable.i18n.ResourceI18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Lenovo on 2016/1/8.
 */
public class SelectDrawableAction extends AnAction
{
    List<DrawableFile> drawableFileList = new ArrayList<>();
    VirtualFile secondParent = null;
    String selectorDrawableName;

    boolean isDirectory = false;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        if (anActionEvent == null)
            return;
        if (isDirectory)
        {
            String string = I18n.getString(ResourceI18n.PLEASE_DONT_SELECT_FOLDER);
            showInfoDialog(string, anActionEvent);
            return;
        }

        final AnActionEvent finalAnActionEvent = anActionEvent;
        final Project project = anActionEvent.getProject();

        String title = I18n.getString(ResourceI18n.SET_TITLE);
        String message = I18n.getString(ResourceI18n.PLEASE_ENTER_SELECTORDRAWABLE_NAME);
        do
        {
            if (selectorDrawableName == null)
            {
            } else if ("".equals(selectorDrawableName.trim()))
            {
                String string = I18n.getString(ResourceI18n.PLEASE_ENTER_SELECTORDRAWABLE_NAME);
                showErrorDialog(string, anActionEvent);
            } else if (FileOperation.isFindChild(secondParent, FileOperation.addSuffixXml(selectorDrawableName)))
            {
                String string = I18n.getString(ResourceI18n.FILE_ALREADY_EXISTS);
                showErrorDialog(string, anActionEvent);
            } else if (!FileOperation.isValidFileName(FileOperation.addSuffixXml(selectorDrawableName)))
            {
                String string = I18n.getString(ResourceI18n.FILE_NAME_INVALID);
                showErrorDialog(string, anActionEvent);
            }

            selectorDrawableName = Messages.showInputDialog(project, message, title, Messages.getQuestionIcon());
        }
        while ((selectorDrawableName != null) && ("".equals(selectorDrawableName.trim()) || (FileOperation.isFindChild(secondParent, FileOperation.addSuffixXml(selectorDrawableName))) || (!FileOperation.isValidFileName(FileOperation.addSuffixXml(selectorDrawableName)))));

        if (selectorDrawableName == null)
            return;

        selectorDrawableName = FileOperation.addSuffixXml(selectorDrawableName);

        Collections.sort(drawableFileList);

        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            @Override
            public void run()
            {
                //创建drawable 文件夹
                VirtualFile virtualFile = null;
                try
                {
                    virtualFile = FileOperation.creteDir(secondParent, Constants.DRAWABLE);
                } catch (IOException e)
                {
                    String string = I18n.getString(ResourceI18n.CREATE_DRAWABLE_DIR_FAILED);
                    showErrorDialog(string, finalAnActionEvent);
                    e.printStackTrace();
                    return;
                }
                //创建 selector 文件
                VirtualFile selectorVirtualFile = null;
                try
                {
                    selectorVirtualFile = FileOperation.creteFile(virtualFile, selectorDrawableName);
                } catch (IOException e)
                {
                    String string = I18n.getString(ResourceI18n.CREATE_SELECTORDRAWABLE_FILE_FAILED);
                    showErrorDialog(string, finalAnActionEvent);
                    e.printStackTrace();
                    return;
                }
                //生成selector文件内容
                try
                {
                    SelectorDrawableGenerator.generate(drawableFileList, selectorVirtualFile);
                } catch (IOException e)
                {
                    String string = I18n.getString(ResourceI18n.GENERATE_SELECTORDRAWABLE_FILE_CONTENT_FAIL);
                    showErrorDialog(string, finalAnActionEvent);
                    e.printStackTrace();
                    return;
                }

                //打开文件
                FileOperation.openFile(project, selectorVirtualFile);
            }
        });
    }


    @Override
    public void update(AnActionEvent e)
    {
        drawableFileList.clear();
        selectorDrawableName = null;
        isDirectory = false;

        VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        DrawableFile drawableFile = new DrawableFile();
        for (int i = 0; i < virtualFiles.length; i++)
        {
            VirtualFile virtualFile = virtualFiles[i];

            //如果是文件夹，则不可用
            if (virtualFile.isDirectory())
            {
                isDirectory = true;
                break;
            }

            VirtualFile firstParent = virtualFile.getParent();
            if ((!firstParent.exists()) || (!firstParent.isDirectory()))
            {
                e.getPresentation().setEnabled(false);
                break;
            }

            String name = firstParent.getName();
            Matcher matcher = Constants.VALID_FOLDER_PATTERN.matcher(name);
            if (!matcher.matches())
            {
                e.getPresentation().setEnabled(false);
                return;
            }

            secondParent = firstParent.getParent();
            if (secondParent == null || (!secondParent.isDirectory()) || (!secondParent.getName().equals(Constants.RES)))
            {
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
    }

    private void showInfoDialog(String text, AnActionEvent e)
    {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));

        if (statusBar != null)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.INFO, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    private void showErrorDialog(String text, AnActionEvent e)
    {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));

        if (statusBar != null)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.ERROR, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
}
