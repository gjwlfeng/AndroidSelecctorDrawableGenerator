package com.zf.androidplugin.selectdrawable.dto;

/**
 * Created by Lenovo on 2016/1/8.
 */
public enum DrawableStatus
{
    _accelerated, _activated, _active, _checkable,//
    _checked, _drag_can_accept, _drag_hovered, _enable,//
    _first, _window_focused, _focused, _hovered, //
    _single, _selected, _last, _pressed, _middle,//
    _normal, none;


    public static DrawableStatus getDrawableStatusByName(String enumName)
    {
        if (enumName == null)
            return none;
        if (enumName.equals(""))
            return none;
        String enumNameLowerCase = enumName.toLowerCase();
        if (enumNameLowerCase.endsWith(_accelerated.name()))
            return _accelerated;
        if (enumNameLowerCase.endsWith(_activated.name()))
            return _activated;
        if (enumNameLowerCase.endsWith(_active.name()))
            return _active;
        if (enumNameLowerCase.endsWith(_checkable.name()))
            return _checkable;
        if (enumNameLowerCase.endsWith(_checked.name()))
            return _checked;
        if (enumNameLowerCase.endsWith(_drag_can_accept.name()))
            return _drag_can_accept;
        if (enumNameLowerCase.endsWith(_drag_hovered.name()))
            return _drag_hovered;
        if (enumNameLowerCase.endsWith(_enable.name()))
            return _enable;
        if (enumNameLowerCase.endsWith(_first.name()))
            return _first;
        if (enumNameLowerCase.endsWith(_window_focused.name()))
            return _window_focused;
        if (enumNameLowerCase.endsWith(_focused.name()))
            return _focused;
        if (enumNameLowerCase.endsWith(_hovered.name()))
            return _hovered;
        if (enumNameLowerCase.endsWith(_single.name()))
            return _single;
        if (enumNameLowerCase.endsWith(_selected.name()))
            return _selected;
        if (enumNameLowerCase.endsWith(_last.name()))
            return _last;
        if (enumNameLowerCase.endsWith(_last.name()))
            return _last;
        if (enumNameLowerCase.endsWith(_pressed.name()))
            return _pressed;
        if (enumNameLowerCase.endsWith(_middle.name()))
            return _middle;
        if (enumNameLowerCase.endsWith(_normal.name()))
            return _normal;
        return none;
    }


}
