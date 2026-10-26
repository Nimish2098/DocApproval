package com.Project.DocApproval.analysis.check;

// TextUtils.java
import java.util.*;
import java.util.regex.*;

final class TextUtils {
  private static final Pattern BULLET = Pattern.compile(
      "^\\s*(?:[\\u2022\\u25CF\\u25AA\\u25E6\\u2023\\u2219\\u00B7\\uF0B7\\uF0A7\\uF0D8]\\s*"
      + "|[-\\u2013\\u2014*]\\s+)(\\S.*)$");

  private TextUtils() {}

  static boolean isBullet(String line) { return BULLET.matcher(line).matches(); }

  static List<String> bullets(String text) {
    List<String> out = new ArrayList<>();
    if (text == null) return out;
    for (String line : text.split("\\R")) {
      Matcher m = BULLET.matcher(line);
      if (m.matches()) out.add(m.group(1).trim());
    }
    return out;
  }

  static int wordCount(String s) {
    if (s == null) return 0;
    String t = s.trim();
    return t.isEmpty() ? 0 : t.split("\\s+").length;
  }
}