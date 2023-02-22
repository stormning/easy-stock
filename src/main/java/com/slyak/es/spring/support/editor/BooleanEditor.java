package com.slyak.es.spring.support.editor;

import org.apache.commons.lang3.BooleanUtils;

import java.beans.PropertyEditorSupport;

public class BooleanEditor extends PropertyEditorSupport {



    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || "".equals(text)) {
            setValue(false);
        } else {
            setValue(BooleanUtils.toBoolean(text.trim()));
        }
    }
}
