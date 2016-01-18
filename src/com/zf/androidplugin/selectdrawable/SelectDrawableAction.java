package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Lenovo on 2016/1/8.
 */
public class SelectDrawableAction extends AnAction
{
    List<DrawableFile> drawableFileList = new ArrayList<DrawableFile>();
    VirtualFile secondParent = null;
    String selectorDrawableName ;

    private static final String SUFFIX_XML = ".xml";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        Project project = anActionEvent.getProject();

        String title = "设置文件名";
        String message = "请输入selector drawabel 文件名";
        do
        {
            if (selectorDrawableName == null)
            {
            } else if ("".equals(selectorDrawableName.trim()))
            {
                showErrorDialog("请输入文件名", anActionEvent);
            } else if (!isFindChild(selectorDrawableName))
            {
                showErrorDialog("该目录下已经该文件名", anActionEvent);
            } else if (!FileGenerator.isValidFileName(selectorDrawableName))
            {
                showErrorDialog("文件名无效", anActionEvent);
            }

            selectorDrawableName = Messages.showInputDialog(project, message, title, Messages.getQuestionIcon());
        }
        while ((selectorDrawableName != null) && ("".equals(selectorDrawableName.trim()) || (!isFindChild(selectorDrawableName)) || (!FileGenerator.isValidFileName(selectorDrawableName))));

        if (selectorDrawableName == null)
            return;

        selectorDrawableName = selectorDrawableName.endsWith("SUFFIX_XML") ? selectorDrawableName : selectorDrawableName + SUFFIX_XML;

        Collections.sort(drawableFileList);
        SelectorRunable runnable = new SelectorRunable(drawableFileList, secondParent, selectorDrawableName);
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), runnable);
    }

    public boolean isFindChild(String fileName)
    {
        return secondParent.findChild(fileName.endsWith("SUFFIX_XML") ? fileName : fileName + SUFFIX_XML) == null;
    }

    @Override
    public void update(AnActionEvent e)
    {
        drawableFileList.clear();
        e.getPresentation().setEnabled(false);
        VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        DrawableFile drawableFile = new DrawableFile();
        for (int i = 0; i < virtualFiles.length; i++)
        {
            VirtualFile virtualFile = virtualFiles[i];

            //如果是文件夹，则不可用
            if (virtualFile.isDirectory())
            {
                e.getPresentation().setEnabled(false);
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
                break;
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


            if (selectorDrawableName == null && (drawableStatusByName != DrawableStatus.none))
            {
                selectorDrawableName = simpleName.replace(drawableStatusByName.name(), "") + Constants.SELECTOR_XML;
            }

            if (selectorDrawableName == null && virtualFiles.length == i + 1)
            {
                selectorDrawableName = simpleName + Constants.SELECTOR_XML;
            }

            if (!e.getPresentation().isEnabled())
                e.getPresentation().setEnabled(true);
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
