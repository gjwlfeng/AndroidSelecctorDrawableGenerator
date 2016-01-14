package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class RunnableHelper
{
    public static void runReadCommand(Project project, Runnable cmd)
    {
        WriteCommandAction.runWriteCommandAction(project,cmd);
    }

    public static void runWriteCommand(Project project, Runnable cmd)
    {
        WriteCommandAction.runWriteCommandAction(project,cmd);
    }
}
