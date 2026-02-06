/*
 * Copyright 2018-present Red Brick Lane Marketing Solutions Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.zapr.druid.druidry.query.aggregation;

import in.zapr.druid.druidry.aggregator.CountAggregator;
import in.zapr.druid.druidry.aggregator.DoubleSumAggregator;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.aggregator.LongSumAggregator;
import in.zapr.druid.druidry.dataSource.TableDataSource;
import in.zapr.druid.druidry.dimension.DruidDimension;
import in.zapr.druid.druidry.dimension.SimpleDimension;
import in.zapr.druid.druidry.filter.AndFilter;
import in.zapr.druid.druidry.filter.DruidFilter;
import in.zapr.druid.druidry.filter.SelectorFilter;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.postAggregator.ArithmeticFunction;
import in.zapr.druid.druidry.postAggregator.ArithmeticPostAggregator;
import in.zapr.druid.druidry.postAggregator.ConstantPostAggregator;
import in.zapr.druid.druidry.postAggregator.DruidPostAggregator;
import in.zapr.druid.druidry.postAggregator.FieldAccessPostAggregator;
import in.zapr.druid.druidry.query.config.Context;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.topNMetric.SimpleMetric;
import in.zapr.druid.druidry.topNMetric.TopNMetric;
import java.util.Arrays;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class TopNQueryTest {
    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSampleQuery() throws JacksonException, JSONException {

        SelectorFilter selectorFilter1 = new SelectorFilter("dim1", "some_value");
        SelectorFilter selectorFilter2 = new SelectorFilter("dim2", "some_other_val");

        AndFilter filter = new AndFilter(Arrays.asList(selectorFilter1, selectorFilter2));

        DruidAggregator aggregator1 = new LongSumAggregator("count", "count");
        DruidAggregator aggregator2 = new DoubleSumAggregator("some_metric", "some_metric");

        FieldAccessPostAggregator fieldAccessPostAggregator1 =
                new FieldAccessPostAggregator("some_metric", "some_metric");

        FieldAccessPostAggregator fieldAccessPostAggregator2 =
                new FieldAccessPostAggregator("count", "count");

        DruidPostAggregator postAggregator =
                ArithmeticPostAggregator.builder()
                        .name("sample_divide")
                        .function(ArithmeticFunction.DIVIDE)
                        .fields(
                                Arrays.asList(
                                        fieldAccessPostAggregator1, fieldAccessPostAggregator2))
                        .build();

        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);

        DruidDimension dimension = new SimpleDimension("sample_dim");
        TopNMetric metric = new SimpleMetric("count");

        DruidTopNQuery query =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .dimension(dimension)
                        .threshold(5)
                        .topNMetric(metric)
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Arrays.asList(aggregator1, aggregator2))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .intervals(Collections.singletonList(interval))
                        .build();

        String expectedJsonAsString =
                """
                {
                  "queryType": "topN",
                  "dataSource": {
                    "type": "table",
                    "name": "sample_data"
                  },
                  "dimension": "sample_dim",
                  "threshold": 5,
                  "metric": "count",
                  "granularity": "all",
                  "filter": {
                    "type": "and",
                    "fields": [
                      {
                        "type": "selector",
                        "dimension": "dim1",
                        "value": "some_value"
                      },
                      {
                        "type": "selector",
                        "dimension": "dim2",
                        "value": "some_other_val"
                      }
                    ]
                  },
                  "aggregations": [
                    {
                      "type": "longSum",
                      "name": "count",
                      "fieldName": "count"
                    },
                    {
                      "type": "doubleSum",
                      "name": "some_metric",
                      "fieldName": "some_metric"
                    }
                  ],
                  "postAggregations": [
                    {
                      "type": "arithmetic",
                      "name": "sample_divide",
                      "fn": "/",
                      "fields": [
                        {
                          "type": "fieldAccess",
                          "name": "some_metric",
                          "fieldName": "some_metric"
                        },
                        {
                          "type": "fieldAccess",
                          "name": "count",
                          "fieldName": "count"
                        }
                      ]
                    }
                  ],
                  "intervals": [
                    "2013-08-31T00:00:00.000Z/2013-09-03T00:00:00.000Z"
                  ]
                }\
                """;

        String actualJson = objectMapper.writeValueAsString(query);
        JSONAssert.assertEquals(expectedJsonAsString, actualJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testRequiredFields() throws JacksonException, JSONException {

        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidDimension dimension = new SimpleDimension("Demo");
        TopNMetric metric = new SimpleMetric("Let it work");

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);

        DruidTopNQuery query =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .dimension(dimension)
                        .threshold(7)
                        .topNMetric(metric)
                        .build();

        String actualJson = objectMapper.writeValueAsString(query);

        JSONObject dataSource = new JSONObject();
        dataSource.put("type", "table");
        dataSource.put("name", "sample_data");

        JSONObject expectedQuery = new JSONObject();
        expectedQuery.put("queryType", "topN");
        expectedQuery.put("dataSource", dataSource);

        JSONArray array =
                new JSONArray(
                        Collections.singletonList(
                                "2013-08-31T00:00:00.000Z/2013-09-03T00:00:00.000Z"));
        expectedQuery.put("intervals", array);
        expectedQuery.put("granularity", "day");
        expectedQuery.put("dimension", "Demo");
        expectedQuery.put("threshold", 7);
        expectedQuery.put("metric", "Let it work");

        JSONAssert.assertEquals(actualJson, expectedQuery, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testAllFields() throws JSONException, JacksonException {
        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidDimension dimension = new SimpleDimension("Demo");
        TopNMetric metric = new SimpleMetric("Let it work");

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);

        DruidFilter filter = new SelectorFilter("Spread", "Peace");
        DruidAggregator aggregator = new CountAggregator("Chill");
        DruidPostAggregator postAggregator = new ConstantPostAggregator("Keep", 16.11);
        Context context = Context.builder().populateCache(true).build();

        DruidTopNQuery query =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(7)
                        .topNMetric(metric)
                        .context(context)
                        .build();

        String actualJson = objectMapper.writeValueAsString(query);

        JSONObject dataSource = new JSONObject();
        dataSource.put("type", "table");
        dataSource.put("name", "sample_data");

        JSONObject expectedQuery = new JSONObject();
        expectedQuery.put("queryType", "topN");
        expectedQuery.put("dataSource", dataSource);

        JSONObject expectedFilter = new JSONObject();
        expectedFilter.put("type", "selector");
        expectedFilter.put("dimension", "Spread");
        expectedFilter.put("value", "Peace");

        JSONObject expectedAggregator = new JSONObject();
        expectedAggregator.put("type", "count");
        expectedAggregator.put("name", "Chill");

        JSONObject expectedPostAggregator = new JSONObject();
        expectedPostAggregator.put("type", "constant");
        expectedPostAggregator.put("name", "Keep");
        expectedPostAggregator.put("value", 16.11);

        JSONObject expectedContext = new JSONObject();
        expectedContext.put("populateCache", true);

        JSONArray array =
                new JSONArray(
                        Collections.singletonList(
                                "2013-08-31T00:00:00.000Z/2013-09-03T00:00:00.000Z"));
        expectedQuery.put("intervals", array);
        expectedQuery.put("granularity", "day");
        expectedQuery.put("filter", expectedFilter);
        expectedQuery.put(
                "aggregations", new JSONArray(Collections.singletonList(expectedAggregator)));
        expectedQuery.put(
                "postAggregations",
                new JSONArray(Collections.singletonList(expectedPostAggregator)));
        expectedQuery.put("dimension", "Demo");
        expectedQuery.put("threshold", 7);
        expectedQuery.put("metric", "Let it work");
        expectedQuery.put("context", expectedContext);

        JSONAssert.assertEquals(actualJson, expectedQuery, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testEquals() {
        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidDimension dimension = new SimpleDimension("Demo");
        TopNMetric metric = new SimpleMetric("Let it work");

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);

        DruidFilter filter = new SelectorFilter("Spread", "Peace");
        DruidAggregator aggregator = new CountAggregator("Chill");
        DruidPostAggregator postAggregator = new ConstantPostAggregator("Keep", 16.11);
        Context context = Context.builder().populateCache(true).build();

        DruidTopNQuery query1 =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(7)
                        .topNMetric(metric)
                        .context(context)
                        .build();

        DruidTopNQuery query2 =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(7)
                        .topNMetric(metric)
                        .context(context)
                        .build();

        Assertions.assertThat(query1).isEqualTo(query2);
    }

    @Test
    public void testUnequals() {
        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidDimension dimension = new SimpleDimension("Demo");
        TopNMetric metric = new SimpleMetric("Let it work");

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);

        DruidFilter filter = new SelectorFilter("Spread", "Peace");
        DruidAggregator aggregator = new CountAggregator("Chill");
        DruidPostAggregator postAggregator = new ConstantPostAggregator("Keep", 16.11);
        Context context = Context.builder().populateCache(true).build();

        DruidTopNQuery query1 =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(7)
                        .topNMetric(metric)
                        .context(context)
                        .build();

        DruidTopNQuery query2 =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(314)
                        .topNMetric(metric)
                        .context(context)
                        .build();

        Assertions.assertThat(query1).isNotEqualTo(query2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void preconditionCheck() {
        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidDimension dimension = new SimpleDimension("Demo");
        TopNMetric metric = new SimpleMetric("Let it work");

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);

        DruidFilter filter = new SelectorFilter("Spread", "Peace");
        DruidAggregator aggregator = new CountAggregator("Chill");
        DruidPostAggregator postAggregator = new ConstantPostAggregator("Keep", 16.11);
        Context context = Context.builder().populateCache(true).build();

        DruidTopNQuery query1 =
                DruidTopNQuery.builder()
                        .dataSource(new TableDataSource("sample_data"))
                        .intervals(Collections.singletonList(interval))
                        .granularity(granularity)
                        .filter(filter)
                        .aggregators(Collections.singletonList(aggregator))
                        .postAggregators(Collections.singletonList(postAggregator))
                        .dimension(dimension)
                        .threshold(-5)
                        .topNMetric(metric)
                        .context(context)
                        .build();
    }
}
