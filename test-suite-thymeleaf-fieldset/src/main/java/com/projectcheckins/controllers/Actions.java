package com.projectcheckins.controllers;

public final class Actions {
    private Actions() {
    }

    public static final String SLASH = "/";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_LIST = "list";
    public static final String ACTION_SAVE = "save";

    public static final String ACTION_SHOW = "show";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_UPDATE = "update";
    public static final String PATH_CREATE = SLASH + "create";
    public static final String PATH_LIST = SLASH + "list";
    public static final String PATH_SAVE = SLASH + "save";
    public static final String PATH_EDIT = SLASH + "edit";
    public static final String PATH_DELETE = SLASH + "delete";
    public static final String PATH_UPDATE = SLASH + "update";
    public static final String MODEL_KEY_FORM = "form";
}
