package com.slyak.es.spring.support.editor;

import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class NumberEditor extends PropertyEditorSupport {

    private final Class<? extends Number> numberClass;

    public NumberEditor(Class<? extends Number> numberClass) {
        this.numberClass = numberClass;
    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(NumberUtils.parseNumber(StringUtils.hasLength(text) ? text.trim() : "0", numberClass));
    }
}
