package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
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
    String stordrawName = null;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        Collections.sort(drawableFileList);
        SelectorRunable runnable = new SelectorRunable(drawableFileList, secondParent, stordrawName);
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), runnable);
        drawableFileList.clear();
        stordrawName = null;
    }

    @Override
    public void update(AnActionEvent e)
    {
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


            if (stordrawName == null && (drawableStatusByName != DrawableStatus.none))
            {
                stordrawName = simpleName.replace(drawableStatusByName.name(), "") + Constants.SELECTOR_XML;
            }

            if (stordrawName == null && virtualFiles.length == i + 1)
            {
                stordrawName = simpleName + Constants.SELECTOR_XML;
            }

            if (!e.getPresentation().isEnabled())
                e.getPresentation().setEnabled(true);
        }
    }
}
