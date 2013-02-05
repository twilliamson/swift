package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * starttimeResultWindow: start time of the resulting window
 * <p/>
 * columnNameValueMap: map of results for the above window. The column names
 * are the keys in the map and their corresponding values are values in the map
 */
@Immutable
@ThriftStruct
public class ReadResultQueryInfo {
  private final long starttimeResultWindow;
  private final Map<String, String> columnNameValueMap;

  @ThriftConstructor
  public ReadResultQueryInfo(long starttimeResultWindow, Map<String, String> columnNameValueMap) {
    this.starttimeResultWindow = starttimeResultWindow;
    this.columnNameValueMap = columnNameValueMap == null ?
      ImmutableMap.<String, String>of() : Collections.unmodifiableMap(columnNameValueMap);
  }

  @ThriftField(1)
  public long getStarttimeResultWindow() {
    return starttimeResultWindow;
  }

  @ThriftField(2)
  public Map<String, String> getColumnNameValueMap() {
    return columnNameValueMap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReadResultQueryInfo that = (ReadResultQueryInfo) o;

    return  starttimeResultWindow == that.starttimeResultWindow &&
      Objects.equals(columnNameValueMap, that.columnNameValueMap);
  }

  @Override
  @SuppressWarnings("NumericCastThatLosesPrecision")
  public int hashCode() {
    int result = (int) (starttimeResultWindow ^ (starttimeResultWindow >>> 32));

    result = 31 * result + (columnNameValueMap != null ? columnNameValueMap.hashCode() : 0);

    return result;
  }

  @Override
  public String toString() {
    return "ReadResultQueryInfo{" +
      "starttimeResultWindow=" + starttimeResultWindow +
      ", columnNameValueMap=" + columnNameValueMap +
      '}';
  }
}

