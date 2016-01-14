package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.vfs.VirtualFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableFile;
import com.zf.androidplugin.selectdrawable.dto.DrawableStatus;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class SelectorDrawableGenerator
{
    private static final String SELECTOR = "selector";
    private static final String SCHEMA = "http://schemas.android.com/apk/res/android";
    private static final String NS = "android";
    private static final String DRAWABLE = "drawable";
    private static final String STATUS_PREFIX = "state";
    private static final String SELECTOR_ITEM = "item";
    private static final String AT = "@";
    private static final String BACKSLASH = "/";

    public static void generate(List<DrawableFile> drawableFileList, VirtualFile outputFile)
    {
        Element root = new Element(SELECTOR);
        root.addNamespaceDeclaration(NS, SCHEMA);

        for (DrawableFile drawableFile : drawableFileList)
        {
            Element element = new Element(SELECTOR_ITEM);
            element.addAttribute(generatorDrawable(drawableFile));
            if (drawableFile.getDrawableStatus() != DrawableStatus._normal && drawableFile.getDrawableStatus() != DrawableStatus.none)
            {
                element.addAttribute(generatorState(drawableFile));
            }
            root.appendChild(element);
        }

        outputFile(root, outputFile);
    }


    private static void outputFile(Element element, VirtualFile outputFile)
    {
        Document doc = new Document(element);
        OutputStream os = null;

        try
        {
            os = outputFile.getOutputStream(null);
            Serializer serializer = new Serializer(os);
            serializer.setIndent(4);
            serializer.write(doc);
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (os != null)
                try
                {
                    os.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
    }


    private static Attribute generatorDrawable(DrawableFile drawableFile)
    {
        StringBuffer append = new StringBuffer().append(AT).append(DRAWABLE).append(BACKSLASH).append(drawableFile.getSimpleName());
        Attribute attribute = new Attribute(DRAWABLE, append.toString());
        attribute.setNamespace(NS, SCHEMA);

        return attribute;
    }

    private static Attribute generatorState(DrawableFile drawableFile)
    {
        StringBuffer append = new StringBuffer().append(STATUS_PREFIX).append(drawableFile.getDrawableStatus().name());
        Attribute attribute = new Attribute(append.toString(), String.valueOf(drawableFile.isStatus()));
        attribute.setNamespace(NS, SCHEMA);
        return attribute;
    }
}
