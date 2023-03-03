package com.slyak.es.util;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String decodeUnicode(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i;
        int pos = 0;
        while ((i = str.indexOf("\\u", pos)) != -1) {
            sb.append(str, pos, i);
            if (i + 5 < str.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(str.substring(i + 2, i + 6), 16));
            }
        }
        //如果pos位置后，有非中文字符，直接添加
        sb.append(str.substring(pos));
        return sb.toString();
    }

    public static String find(String input, Pattern pattern, int groupIndex) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            int cnt = matcher.groupCount();
            if (cnt <= groupIndex) {
                return matcher.group(groupIndex);
            }
        }
        return null;
    }

    public static List<String> findGroups(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            int cnt = matcher.groupCount();
            if (cnt > 0) {
                List<@Nullable String> finds = Lists.newArrayList();
                for (int i = 0; i < cnt; i++) {
                    finds.add(matcher.group(i + 1));
                }
                return finds;
            }
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) {

    }
}
