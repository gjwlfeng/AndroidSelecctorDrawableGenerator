package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.zf.androidplugin.selectdrawable.Constants;

import java.io.IOException;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class FileOperation
{
    //.xml 后缀
    private static final String SUFFIX_XML = ".xml";

    /**
     * 判断是否有该文件
     *
     * @param virtualFile
     * @param fileName
     * @return
     */
    public static boolean isFindChild(VirtualFile virtualFile, String fileName)
    {
        if (!virtualFile.isDirectory())
            return false;
        return virtualFile.findChild(fileName.toLowerCase().endsWith(SUFFIX_XML) ? fileName.toLowerCase() : fileName.toLowerCase() + SUFFIX_XML) != null;
    }

    /**
     * 打开文件
     * @param project
     * @param xmlVirtualFile
     */
    public static void openFile(Project project, VirtualFile xmlVirtualFile)
    {
        FileEditorManagerEx fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);
        if (fileEditorManagerEx == null)
            return;
        if(xmlVirtualFile.isDirectory())
            throw  new IllegalArgumentException("Parameter error\n");
        fileEditorManagerEx.openFile(xmlVirtualFile, true);
    }

    /**
     * 添加后缀 .xml
     *
     * @param fileName
     * @return
     */
    public static String addSuffixXml(String fileName)
    {
        if (fileName == null)
            throw new IllegalArgumentException("File name cannot be empty\n");
        StringBuilder stringBuilder = new StringBuilder(fileName.toLowerCase());
        return isSuffixXml(fileName) ? stringBuilder.toString() : stringBuilder.append(SUFFIX_XML).toString();
    }

    /**
     * 判断后缀是不是.xml结尾
     *
     * @param content
     * @return
     */
    public static boolean isSuffixXml(String content)
    {
        if (content == null)
            throw new IllegalArgumentException("Parameters cannot be empty\n");
        return content.toLowerCase().endsWith(SUFFIX_XML);
    }

    /**
     * 创建文件夹
     *
     * @param baseDir
     * @param childdir
     */
    public static VirtualFile creteDir(VirtualFile baseDir, String childdir) throws IOException
    {
        if (!baseDir.isDirectory())
            throw new IllegalArgumentException("Must be a folder\n");
        VirtualFile child = baseDir.findChild(childdir);
        return child == null ? baseDir.createChildDirectory(null, childdir) : child;
    }

    /**
     * 创建文件
     *
     * @param baseDir
     * @param selectorfile
     */
    public static VirtualFile creteFile(VirtualFile baseDir, String selectorfile) throws IOException
    {
        if (!baseDir.isDirectory())
            throw new IllegalArgumentException("Must be a folder\n");

        VirtualFile child = baseDir.findChild(selectorfile);
        return child == null ? baseDir.createChildData(null, selectorfile) : child;

    }

    /**
     * 判断文件名是否有效
     *
     * @param fileName
     * @return
     */
    public static boolean isValidFileName(String fileName)
    {
        if (fileName == null || fileName.length() > 255) return false;
        else
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }
}
