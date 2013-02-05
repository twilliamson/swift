package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import java.io.Closeable;
import java.util.List;

@ThriftService("PumaReadService")
@SuppressWarnings({"unused", "InterfaceNeverImplemented"})
public interface PumaService extends Closeable {
    @ThriftMethod
    public void foo();

  @ThriftMethod
  public List<List<ReadResultQueryInfo>> getResult(
    @ThriftField(name = "reader") List<ReadQueryInfo> reader
  ) throws ReadSemanticException;

  @ThriftMethod
  public List<List<ReadResultQueryInfoTimeString>> getResultTimeString(
    @ThriftField(name = "reader") List<ReadQueryInfoTimeString> reader
  ) throws ReadSemanticException;

  /**
   * Runs query on the list of aggregations and merges the result.
   * The ReadResultQueryInfo will use the names of the columns
   * in the last SingleQueryInfo.
   * The aggregation column in the ReadResultQueryInfo will be
   * named __MERGED_RESULT__
   */
  @ThriftMethod
  public List<ReadResultQueryInfo> mergeQueryAggregation(
    @ThriftField(name = "mergeAggregationQueryInfo")
    MergeAggregationQueryInfo mergeAggregationQueryInfo
  ) throws ReadSemanticException;

  /**
   * An approximate estimate of the latest timestamp to which puma has
   * finished processing
   * This can be used to figure out the endtime for ReadQueryInfo for a
   * particular application and scribe category combination.
   *
   * @param category:      The scribe category your application is operating on
   * @param appName:       The name of your application. It is currently specified
   *                       via the 'app_name' or 'stream_name' parameters in the config
   * @param bucketNumbers: The scribe category buckets. If you are
   *                       querying for data which you know goes to a particular scribe buckets,
   *                       this will allow you to gauge the progress of puma for those bucket.
   *                       This is useful in cases where you data is bucketed such that you can
   *                       publish results for one/few buckets independent of the other buckets
   *                       Specify an empty list to find the latestQueryableTime over all buckets
   * @return A single timestamp (seconds since epoch) which represents the
   *         minimum queryable time over all the buckets specified. A return value of 0
   *         indicates no data is available yet for at least one of the buckets
   *         specified
   */
  @ThriftMethod
  public long latestQueryableTime(
    @ThriftField(name = "category") String category,
    @ThriftField(name = "appName") String appName,
    @ThriftField(name = "bucketNumbers") List<Integer> bucketNumbers
  ) throws ReadSemanticException;

  /**
   * An approximate estimate of the latest timestamp to which puma has
   * finished processing
   * This can be used to figure out the endtime for ReadQueryInfo for a
   * particular application and scribe category combination.
   *
   * @param category:      The scribe category your application is operating on
   * @param appName:       The name of your application. It is currently specified
   *                       via the 'app_name' or 'stream_name' parameters in the config
   * @param bucketNumbers: The scribe category buckets. If you are
   *                       querying for data which you know goes to a particular scribe buckets,
   *                       this will allow you to gauge the progress of puma for those bucket.
   *                       This is useful in cases where you data is bucketed such that you can
   *                       publish results for one/few buckets independent of the other buckets
   *                       Specify an empty list to find the latestQueryableTime over all buckets
   * @return A list of timestamps (seconds since epoch), one corresponding to
   *         each bucket specified, which represents the minimum queryable time for the
   *         bucket. If no data is available for a bucket the corresponding timestamp
   *         returned is 0
   */
  @ThriftMethod
  public List<Long> latestQueryableTimes(
    @ThriftField(name = "category") String category,
    @ThriftField(name = "appName") String appName,
    @ThriftField(name = "bucketNumbers") List<Integer> bucketNumbers
  ) throws ReadSemanticException;


  /**
   * Get results for life time aggregations.
   */
  @ThriftMethod
  public List<List<ReadResultQueryInfo>> getLifetimeResult(
    @ThriftField(name = "queries") List<SingleQueryInfo> queries
  ) throws ReadSemanticException;


  /**
   * Lifetime aggregation: merge query results from multiple sources
   */
  @ThriftMethod
  public List<ReadResultQueryInfo> mergeLifetimeResult(
    @ThriftField(name = "queries") List<SingleQueryInfo> queries
  ) throws ReadSemanticException;

  @Override
  public void close();
}
