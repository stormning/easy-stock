package com.slyak.es.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slyak.es.domain.Plan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectUtils {
    public static Object convertToMapOrList(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Optional) {
            return ((Optional<?>) object).orElse(null);
        } else if (object instanceof Map) {
            Map<Object, Object> map = new LinkedHashMap<>();
            ((Map<?, ?>) object).forEach((key, value) -> {
                if (key != null) {
                    map.put(key.toString(), convertToMapOrList(value));
                }
            });
            return map;
        } else if (object instanceof Collection) {
            List<Object> list = new ArrayList<>();
            for (Object o : ((Collection<?>) object)) {
                list.add(convertToMapOrList(o));
            }
            return list;
        } else if (ClassUtils.isPrimitiveOrWrapper(object.getClass()) || object instanceof String || object instanceof Date) {
            return object;
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(object.getClass());
            for (PropertyDescriptor descriptor : descriptors) {
                 Method readMethod = descriptor.getReadMethod();
                if (readMethod != null && !readMethod.getName().contains("Class") && !descriptor.getReadMethod().isAnnotationPresent(JsonIgnore.class)) {
                    String name = descriptor.getName();
                    System.out.println("name: " + name);
                    try {
                        Object fieldValue = readMethod.invoke(object);
                        System.out.println(fieldValue);
                        if (fieldValue == null) {
                            map.put(name, null);
                        } else if (ClassUtils.isPrimitiveOrWrapper(fieldValue.getClass()) || fieldValue instanceof String) {
                            map.put(name, fieldValue);
                        } else if (fieldValue instanceof Optional) {
                            Optional<?> optional = (Optional<?>) fieldValue;
                            optional.ifPresent(o -> map.put(name, convertToMapOrList(o)));
                        } else if (fieldValue instanceof Number) {
                            map.put(name, fieldValue.toString());
                        } else {
                            map.put(name, convertToMapOrList(fieldValue));
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new FatalBeanException("Could not copy property '" +
                                name + "' from source to target", e);
                    }
                }
            }
            return map;
        }
    }

    public static void main(String[] args) {
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(Plan.class);
        for (PropertyDescriptor descriptor : descriptors) {
            System.out.println(descriptor.getName());
//            System.out.println(descriptor.getReadMethod());
        }
    }
}
