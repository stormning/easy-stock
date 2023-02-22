package com.slyak.es.controller;

import com.slyak.es.spring.support.editor.BooleanEditor;
import com.slyak.es.spring.support.editor.CharEditor;
import com.slyak.es.spring.support.editor.DateEditor;
import com.slyak.es.spring.support.editor.NumberEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class MainExceptionHandler extends BaseController {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result onError(Exception e) {
        log.error("Controller exception", e);
        return fail(e);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(boolean.class, new BooleanEditor());
        binder.registerCustomEditor(byte.class, new NumberEditor(Byte.class));
        binder.registerCustomEditor(short.class, new NumberEditor(Short.class));
        binder.registerCustomEditor(char.class, new CharEditor());
        binder.registerCustomEditor(int.class, new NumberEditor(Integer.class));
        binder.registerCustomEditor(long.class, new NumberEditor(Long.class));
        binder.registerCustomEditor(float.class, new NumberEditor(Float.class));
        binder.registerCustomEditor(double.class, new NumberEditor(Double.class));
        binder.registerCustomEditor(Date.class, new DateEditor());
    }
}
