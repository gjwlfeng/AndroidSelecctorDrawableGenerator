package com.zf.androidplugin.selectdrawable.dto;

/**
 * Created by Lenovo on 2016/1/8.
 */
public class DrawableFile implements Cloneable, Comparable<DrawableFile> {
    //文件名
    private String simpleName;
    //文件全名
    private String fullPathName;
    //文件对应状态
    DrawableStatus drawableStatus;
    //文件对应状态的true,false
    private boolean status;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DrawableStatus getDrawableStatus() {
        return drawableStatus;
    }

    public String getFullPathName() {
        return fullPathName;
    }

    public void setFullPathName(String fullPathName) {
        this.fullPathName = fullPathName;
    }

    public void setDrawableStatus(DrawableStatus drawableStatus) {
        this.drawableStatus = drawableStatus;
    }

    @Override
    public String toString() {
        return "DrawableFile{" +
                "simpleName='" + simpleName + '\'' +
                ", fullPathName='" + fullPathName + '\'' +
                ", drawableStatus=" + drawableStatus +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawableFile)) return false;

        DrawableFile that = (DrawableFile) o;

        return simpleName != null ? simpleName.equals(that.simpleName) : that.simpleName == null;
    }

    @Override
    public int hashCode() {
        return simpleName != null ? simpleName.hashCode() : 0;
    }

    @Override
    public Object clone() {
        DrawableFile clone;
        try {
            clone = (DrawableFile) super.clone();
            clone.drawableStatus = DrawableStatus.none;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(DrawableFile o) {
        if (o.getDrawableStatus() == this.getDrawableStatus()) {
            return 1;
        } else if (o.getDrawableStatus() == DrawableStatus.none || o.getDrawableStatus() == DrawableStatus._normal) {
            return -1;
        }
        return 1;
    }
}
