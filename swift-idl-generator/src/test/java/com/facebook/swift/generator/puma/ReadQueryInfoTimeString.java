package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Some explanation on the timezone.
 * In the log line, the time is always specified in UTC (number of milliseconds from ...)
 * The user also has the option of specifying his local timezone.
 * Consider the following scenario:
 * Based on the time specified in UTC, the time is 2011-08-02 2:00  -- 2am August 2nd
 * However, the local timezone is 4 hours behind UTC, so based on that, the time should be
 * 2011-07-01 22:00   -- 10pm August 1st in the local timezone.
 * The aggregation will take this into account, and the above entry will be counted in Aug 1st,
 * if it was a daily aggregation. As far as the aggregation is concerned, the entry is
 * 2011-07-01 22:00 in UTC
 * <p/>
 * On the read side, the user cannot specify the timezone. It is assumed that the time is in UTC
 */
@Immutable
@ThriftStruct
public class ReadQueryInfoTimeString {
  private final String name;
  private final String starttime;
  private final String endtime;
  private final int windowSize;
  private final Map<String, String> filter;
  private final List<String> selectList;

  @ThriftConstructor
  public ReadQueryInfoTimeString(
    String name,
    String starttime,
    String endtime,
    int windowSize,
    Map<String, String> filter,
    List<String> selectList
  ) {
    this.name = name;
    this.starttime = starttime;
    this.endtime = endtime;
    this.windowSize = windowSize;
    this.filter = filter == null ?
      ImmutableMap.<String, String>of() : Collections.unmodifiableMap(filter);
    this.selectList = selectList == null ?
      ImmutableList.<String>of() : Collections.unmodifiableList(selectList);
  }

  @ThriftField(1)
  public String getName() {
    return name;
  }

  @ThriftField(2)
  public String getStarttime() {
    return starttime;
  }

  @ThriftField(3)
  public String getEndtime() {
    return endtime;
  }

  @ThriftField(4)
  public int getWindowSize() {
    return windowSize;
  }

  @ThriftField(5)
  public Map<String, String> getFilter() {
    return filter;
  }

  @ThriftField(6)
  public List<String> getSelectList() {
    return selectList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReadQueryInfoTimeString that = (ReadQueryInfoTimeString) o;

    return windowSize == that.windowSize &&
      Objects.equals(name, that.name) &&
      Objects.equals(starttime, that.starttime) &&
      Objects.equals(endtime, that.endtime) &&
      Objects.equals(filter, that.filter) &&
      Objects.equals(selectList, that.selectList);
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;

    result = 31 * result + (starttime != null ? starttime.hashCode() : 0);
    result = 31 * result + (endtime != null ? endtime.hashCode() : 0);
    result = 31 * result + windowSize;
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (selectList != null ? selectList.hashCode() : 0);

    return result;
  }

  @Override
  public String toString() {
    return "ReadQueryInfoTimeString{" +
      "name='" + name + '\'' +
      ", starttime='" + starttime + '\'' +
      ", endtime='" + endtime + '\'' +
      ", windowSize=" + windowSize +
      ", filter=" + filter +
      ", selectList=" + selectList +
      '}';
  }
}

