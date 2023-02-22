package com.slyak.es.spring.support.editor;

import com.slyak.es.util.DateUtils;

import java.beans.PropertyEditorSupport;

public class DateEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        return DateUtils.DATETIME_FORMAT.format(getValue());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(DateUtils.parse(text));
    }
}
