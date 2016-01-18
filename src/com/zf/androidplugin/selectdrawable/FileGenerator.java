package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.zf.androidplugin.selectdrawable.Constants;

import java.io.IOException;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class FileGenerator
{
    public static void creteDir(VirtualFile baseDir, String childdir)
    {
        VirtualFile child = baseDir.findChild(childdir);
        if (child != null && child.exists() && child.isDirectory())
            return;
        try
        {
            baseDir.createChildDirectory(null, childdir);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void creteFile(VirtualFile baseDir, String selectorfile)
    {
        try
        {
            if (baseDir.findChild(selectorfile) == null)
                baseDir.createChildData(null, selectorfile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isValidFileName(String fileName)
    {
        if (fileName == null || fileName.length() > 255) return false;
        else
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }
}
