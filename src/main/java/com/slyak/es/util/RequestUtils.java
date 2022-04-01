/*
 * Project:  eqms
 * Module:   eqms
 * File:     RequestUtils.java
 * Modifier: oznyang@163.com
 * Modified: 2017-09-21 02:11
 *
 * Copyright (c) 2016 oznyang All Rights Reserved.
 *
 * Copying of this document or code and giving it to others and the
 * use or communication of the contents thereof, are forbidden without
 * expressed authority. Offenders are liable to the payment of damages.
 * All rights reserved in the event of the grant of a invention patent
 * or the registration of a utility model, design or code.
 */
package com.slyak.es.util;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:oznyang@163.com">oznyang</a>
 * @version 1.0.0
 * @since 2016-07-22
 */
public final class RequestUtils {

    public static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    public static UrlPathHelper URL_PATH_HELPER = new UrlPathHelper() {
        @Override
        public String getLookupPathForRequest(HttpServletRequest request) {
            String key = request.getRequestURI() + "_lookupPath";
            String path = (String) request.getAttribute(key);
            if (path == null) {
                request.setAttribute(key, path = super.getPathWithinApplication(request));
            }
            return path;
        }
    };

    public static String getParameter(HttpServletRequest request, String name) {
        String query = request.getQueryString();
        if (query != null) {
            Matcher m = QUERY_PARAM_PATTERN.matcher(query);
            while (m.find()) {
                if (name.equals(m.group(1))) {
                    return m.group(3);
                }
            }
        }
        return null;
    }

    public static String getFullUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequest(request).build().encode().toUriString();
    }

    public static String getClientIP(HttpServletRequest request) {
        String xForwardedFor;
        xForwardedFor = StringUtils.trimToNull(request.getHeader("$wsra"));
        if (xForwardedFor != null) {
            return xForwardedFor;
        }
        xForwardedFor = StringUtils.trimToNull(request.getHeader("X-Real-IP"));
        if (xForwardedFor != null) {
            return xForwardedFor;
        }
        xForwardedFor = StringUtils.trimToNull(request.getHeader("X-Forwarded-For"));
        if (xForwardedFor != null) {
            int spaceIndex = xForwardedFor.indexOf(',');
            if (spaceIndex > 0) {
                return xForwardedFor.substring(0, spaceIndex);
            } else {
                return xForwardedFor;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getDomain(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        int end = url.indexOf(".");
        if (end == -1)
            return "";
        int start = url.indexOf("//");
        return url.substring(start + 2, end);
    }

    public static String formatUrl(String base, String path) {
        StringBuilder sb = new StringBuilder();
        if (!"/".equals(base)) {
            sb.append(base);
        }
        if (path.charAt(0) != '/') {
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    @SneakyThrows
    public static String encode(Object url) {
        return URLEncoder.encode(url.toString(), StandardCharsets.UTF_8.name());
    }

    @SneakyThrows
    public static String decode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
    }

    public static boolean isPost(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    public static boolean matchAny(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, String[] patterns) {
        if (ArrayUtils.isNotEmpty(patterns)) {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean matchAny(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, Collection<String> patterns) {
        if (!CollectionUtils.isEmpty(patterns)) {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean match(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, String pattern) {
        return pathMatcher.match(pattern, urlPathHelper.getLookupPathForRequest(request));
    }

    public static boolean matchAny(HttpServletRequest request, String[] patterns) {
        return matchAny(request, URL_PATH_HELPER, PATH_MATCHER, patterns);
    }

    public static boolean matchAll(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, String[] patterns) {
        if (ArrayUtils.isNotEmpty(patterns)) {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            for (String pattern : patterns) {
                if (!pathMatcher.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean matchAll(HttpServletRequest request, String[] patterns) {
        return matchAll(request, URL_PATH_HELPER, PATH_MATCHER, patterns);
    }

    public static String getString(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    public static String getString(HttpServletRequest request, String name, String def) {
        String value = getString(request, name);
        return StringUtils.isEmpty(value) ? def : value;
    }

    public static Boolean getBool(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return StringUtils.isEmpty(value) ? null : BooleanUtils.toBooleanObject(value);
    }

    public static boolean getBool(HttpServletRequest request, String name, boolean def) {
        Boolean value = getBool(request, name);
        return value == null ? def : value;
    }

    public static Integer getInt(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return StringUtils.isEmpty(value) ? null : NumberUtils.createInteger(value);
    }

    public static int getInt(HttpServletRequest request, String name, int def) {
        Integer value = getInt(request, name);
        return value == null ? def : value;
    }

    public static Double getDouble(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return StringUtils.isEmpty(value) ? null : NumberUtils.createDouble(value);
    }

    public static double getDouble(HttpServletRequest request, String name, double def) {
        Double value = getDouble(request, name);
        return value == null ? def : value;
    }

    public static HttpServletRequest getCurrentRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getCurrentResponse() {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
    }

    public static FlashMap getOutputFlashMap() {
        return RequestContextUtils.getOutputFlashMap(getRequestAttributes().getRequest());
    }

    public static void saveOutputFlashMap(FlashMap outputFlashMap) {
        ServletRequestAttributes attributes = getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
        assert flashMapManager != null;
        assert attributes.getResponse() != null;
        flashMapManager.saveOutputFlashMap(outputFlashMap, request, attributes.getResponse());
    }

    public static void saveOutputFlashAttributes(Map<String, ?> attrs) {
        if (!CollectionUtils.isEmpty(attrs)) {
            FlashMap flashMap = getOutputFlashMap();
            flashMap.putAll(attrs);
            saveOutputFlashMap(flashMap);
        }
    }

    private static final Map<HandlerMethod, Boolean> HANDLER_METHOD_TYPES = Maps.newConcurrentMap();

    public static boolean isRestRequest(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            return HANDLER_METHOD_TYPES.computeIfAbsent((HandlerMethod) handler, hm -> hm.getBeanType().getAnnotation(RestController.class) != null || hm.getMethod().getAnnotation(ResponseBody.class) != null);
        }
        return StringUtils.contains(request.getHeader("Accept"), "/json") ||
                ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }

    public static void setSessionAttribute(String name, Object value) {
        HttpSession session = getRequestAttributes().getRequest().getSession();
        session.setAttribute(name, value);
    }

    public static void removeSessionAttribute(String name) {
        HttpSession session = getRequestAttributes().getRequest().getSession();
        session.removeAttribute(name);
    }

    public static Object getSessionAttribute(String name) {
        try {
            return getCurrentRequest().getSession().getAttribute(name);
        } catch (Exception e) {
            return null;
        }
    }
}
