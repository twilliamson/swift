package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Immutable
@ThriftStruct
public class MergeAggregationQueryInfo {
  private final List<SingleQueryInfo> queries;
  private final long starttime;
  private final long endtime;
  private final int windowSize;

  @ThriftConstructor
  public MergeAggregationQueryInfo(
    @JsonProperty("queries") List<SingleQueryInfo> queries,
    @JsonProperty("starttime") long starttime,
    @JsonProperty("endtime") long endtime,
    @JsonProperty("numSteps") int windowSize
  ) {
    this.queries = queries == null ?
      ImmutableList.<SingleQueryInfo>of() : Collections.unmodifiableList(queries);
    this.starttime = starttime;
    this.endtime = endtime;
    this.windowSize = windowSize;
  }

  @ThriftField(1)
  @JsonProperty("queries")
  public List<SingleQueryInfo> getQueries() {
    return queries;
  }

  @ThriftField(2)
  @JsonProperty("starttime")
  public long getStarttime() {
    return starttime;
  }

  @ThriftField(3)
  @JsonProperty("endtime")
  public long getEndtime() {
    return endtime;
  }

  @ThriftField(4)
  @JsonProperty("numSteps")
  public int getWindowSize() {
    return windowSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MergeAggregationQueryInfo that = (MergeAggregationQueryInfo) o;

    return starttime == that.starttime &&
      endtime == that.endtime &&
      windowSize == that.windowSize &&
      Objects.equals(queries, that.queries);
  }

  @Override
  @SuppressWarnings("NumericCastThatLosesPrecision")
  public int hashCode() {
    int result = queries != null ? queries.hashCode() : 0;

    result = 31 * result + (int) (starttime ^ (starttime >>> 32));
    result = 31 * result + (int) (endtime ^ (endtime >>> 32));
    result = 31 * result + windowSize;

    return result;
  }

  @Override
  public String toString() {
    return "MergeAggregationQueryInfo{" +
      "queries=" + queries +
      ", starttime=" + starttime +
      ", endtime=" + endtime +
      ", windowSize=" + windowSize +
      '}';
  }
}

