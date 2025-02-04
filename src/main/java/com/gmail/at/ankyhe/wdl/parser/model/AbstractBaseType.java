package com.gmail.at.ankyhe.wdl.parser.model;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractBaseType implements Type {

    private static final String TYPE_SUFFIX = Type.class.getSimpleName();

    @Override
    public String toString() {
        final String classSimpleName = getClass().getSimpleName();
        return StringUtils.removeEnd(classSimpleName, TYPE_SUFFIX);
    }
}
