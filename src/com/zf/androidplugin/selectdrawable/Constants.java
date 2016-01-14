package com.zf.androidplugin.selectdrawable;

import com.intellij.openapi.fileEditor.FileEditorManager;

import java.util.regex.Pattern;

/**
 * Created by Lenovo on 2016/1/8.
 */
public interface Constants
{
    public static final String RES="res";
    public static final String POINT9SUFFX=".9";
    public static final String DRAWABLE="drawable";
    public static Pattern VALID_FOLDER_PATTERN = Pattern.compile("^drawable(-[a-zA-Z0-9]+)*$");
    public static final String SELECTOR_XML="_selector.xml";

}
