package com.zf.androidplugin.selectdrawable;

import java.util.regex.Pattern;

/**
 * Created by Lenovo on 2016/1/8.
 */
public interface Constants {
    String RES = "res";
    String POINT9SUFFX = ".9";
    String DRAWABLE = "drawable";
    Pattern VALID_FOLDER_PATTERN = Pattern.compile("^drawable(-[a-zA-Z0-9]+)*$");
    String SELECTOR_XML = "_selector.xml";
    String NOTIFICATION_GROUP_NAME="SelectorDrawableGenerator";

}
