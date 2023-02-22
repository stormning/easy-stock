package com.slyak.es.spring.support.editor;

import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.util.StringUtils;

public class CharEditor extends CharacterEditor {

    public CharEditor() {
        super(false);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasLength(text)) {
            setValue(text.trim());
        } else {
            setValue('0');
        }
    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }
}
