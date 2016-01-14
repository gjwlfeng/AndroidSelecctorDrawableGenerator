package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.vfs.VirtualFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class SelectorRunable implements  Runnable
{
    private List<DrawableFile> drawableFileList=null;
    private VirtualFile baseVirtualFile=null;
    private String seletorfile=null;

    public  SelectorRunable(final List<DrawableFile> drawableFileList, final VirtualFile baseVirtualFile, final String seletorfile)
    {
        this.drawableFileList=new ArrayList<DrawableFile>(drawableFileList);
        this.baseVirtualFile=baseVirtualFile;
        this.seletorfile=seletorfile;
    }

    @Override
    public void run()
    {
        VirtualFile outputFile = baseVirtualFile.findChild(Constants.DRAWABLE);
        if (outputFile == null || (!outputFile.exists()))
            FileGenerator.creteDir(baseVirtualFile, Constants.DRAWABLE);


        VirtualFile  drawablefile = outputFile.findChild(seletorfile);
        if(drawablefile==null || (!drawablefile.exists()))
        {
            FileGenerator.creteFile(outputFile, seletorfile);
            VirtualFile outputFileChild = outputFile.findChild(seletorfile);
            SelectorDrawableGenerator.generate(drawableFileList, outputFileChild);
        }

        drawableFileList.clear();
        drawableFileList=null;
    }
}
