package net.codestory.http.convert;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class TypeConvert {
  private TypeConvert() {
    // static class
  }

  public static Object[] convert(Map<String, String> keyValues, String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length + 1];
    converted[0] = TypeConvert.convert(keyValues, types[0]);
    for (int i = 0; i < values.length; i++) {
      converted[i + 1] = TypeConvert.convert(values[i], types[i]);
    }
    return converted;
  }

  public static Object[] convert(String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      converted[i] = TypeConvert.convert(values[i], types[i]);
    }
    return converted;
  }

  public static Object convert(String value, Class<?> type) {
    if ((type == int.class) || (type == Integer.class)) {
      return Integer.parseInt(value);
    } else if ((type == float.class) || (type == Float.class)) {
      return Float.parseFloat(value);
    } else if ((type == long.class) || (type == Long.class)) {
      return Long.parseLong(value);
    } else if ((type == double.class) || (type == Double.class)) {
      return Double.parseDouble(value);
    } else if ((type == boolean.class) || (type == Boolean.class)) {
      return Boolean.parseBoolean(value);
    }
    return value;
  }

  public static Object convert(Map<String, String> keyValues, Class<?> type) {
    if (type.isAssignableFrom(Map.class)) {
      return keyValues;
    }
    return new ObjectMapper().convertValue(keyValues, type);
  }
}
