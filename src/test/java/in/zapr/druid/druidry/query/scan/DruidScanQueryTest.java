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

package in.zapr.druid.druidry.query.scan;

import in.zapr.druid.druidry.dataSource.TableDataSource;
import in.zapr.druid.druidry.dimension.enums.OutputType;
import in.zapr.druid.druidry.filter.DruidFilter;
import in.zapr.druid.druidry.filter.SelectorFilter;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.virtualColumn.ExpressionVirtualColumn;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class DruidScanQueryTest {
    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSampleQuery() throws JacksonException, JSONException {

        List<String> searchDimensions = Arrays.asList("dim1", "dim2");

        DateTime startTime = new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 1, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidFilter filter = new SelectorFilter("dim1", "value1");

        DruidScanQuery query =
                DruidScanQuery.builder()
                        .dataSource(new TableDataSource("sample_datasource"))
                        .columns(searchDimensions)
                        .virtualColumns(
                                Collections.singletonList(
                                        new ExpressionVirtualColumn(
                                                "dim3", "dim1 + dim2", OutputType.FLOAT)))
                        .filter(filter)
                        .resultFormat(ResultFormat.LIST)
                        .intervals(Collections.singletonList(interval))
                        .batchSize(10000)
                        .limit(1000L)
                        .legacy(true)
                        .build();

        String expectedJsonAsString =
                """
                {
                  "queryType": "scan",
                  "dataSource": {
                    "type": "table",
                    "name": "sample_datasource"
                  },
                  "columns": [
                    "dim1",
                    "dim2"
                  ],
                  "virtualColumns": [{
                    "type": "expression",
                    "name": "dim3",
                    "outputType": "FLOAT",
                    "expression": "dim1 + dim2"
                  }],
                  "filter": {
                    "type": "selector",
                    "dimension": "dim1",
                    "value": "value1"
                  },
                  "resultFormat": "list",
                  "batchSize": 10000,
                  "limit": 1000,
                  "legacy": true,
                  "intervals": [\
                    "2013-01-01T00:00:00.000Z/2013-01-03T00:00:00.000Z"\
                  ]\
                }\
                """;

        String actualJson = objectMapper.writeValueAsString(query);
        JSONAssert.assertEquals(expectedJsonAsString, actualJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testRequiredFields() throws JacksonException, JSONException {

        DateTime startTime = new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 1, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidScanQuery query =
                DruidScanQuery.builder()
                        .dataSource(new TableDataSource("sample_datasource"))
                        .intervals(Collections.singletonList(interval))
                        .build();

        String expectedJsonAsString =
                """
                {
                  "queryType": "scan",
                  "dataSource": {
                    "type": "table",
                    "name": "sample_datasource"
                  },
                  "intervals": [\
                    "2013-01-01T00:00:00.000Z/2013-01-03T00:00:00.000Z"\
                  ]\
                }\
                """;

        String actualJson = objectMapper.writeValueAsString(query);
        JSONAssert.assertEquals(actualJson, expectedJsonAsString, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void preconditionLimitCheck() {

        DateTime startTime = new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 1, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidScanQuery query =
                DruidScanQuery.builder()
                        .dataSource(new TableDataSource("sample_datasource"))
                        .intervals(Collections.singletonList(interval))
                        .limit(-1L)
                        .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void preconditionBatchSizeCheck() {

        DateTime startTime = new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 1, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidScanQuery query =
                DruidScanQuery.builder()
                        .dataSource(new TableDataSource("sample_datasource"))
                        .intervals(Collections.singletonList(interval))
                        .batchSize(-1)
                        .build();
    }

    @Test
    public void testSampleQueryWithEmptyLines() throws JacksonException, JSONException {

        List<String> searchDimensions = Arrays.asList();

        DateTime startTime = new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 1, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        DruidFilter filter = new SelectorFilter("dim1", "value1");

        DruidScanQuery query =
                DruidScanQuery.builder()
                        .dataSource(new TableDataSource("sample_datasource"))
                        .columns(searchDimensions)
                        .filter(filter)
                        .resultFormat(ResultFormat.LIST)
                        .intervals(Collections.singletonList(interval))
                        .batchSize(10000)
                        .limit(1000L)
                        .legacy(true)
                        .build();

        String expectedJsonAsString =
                """
                {
                  "queryType": "scan",
                  "dataSource": {
                    "type": "table",
                    "name": "sample_datasource"
                  },
                  "columns": [
                ],
                  "filter": {
                    "type": "selector",
                    "dimension": "dim1",
                    "value": "value1"
                  },
                  "resultFormat": "list",
                  "batchSize": 10000,
                  "limit": 1000,
                  "legacy": true,
                  "intervals": [\
                    "2013-01-01T00:00:00.000Z/2013-01-03T00:00:00.000Z"\
                  ]\
                }\
                """;

        String actualJson = objectMapper.writeValueAsString(query);
        JSONAssert.assertEquals(actualJson, expectedJsonAsString, JSONCompareMode.NON_EXTENSIBLE);
    }
}
