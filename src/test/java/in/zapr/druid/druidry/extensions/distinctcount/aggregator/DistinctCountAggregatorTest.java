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

package in.zapr.druid.druidry.extensions.distinctcount.aggregator;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class DistinctCountAggregatorTest {

    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAllFields() throws JacksonException, JSONException {

        DistinctCountAggregator distinctCountAggregator =
                new DistinctCountAggregator("uniqueStars", "allStars");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "distinctCount");
        jsonObject.put("name", "uniqueStars");
        jsonObject.put("fieldName", "allStars");

        String actualJSON = objectMapper.writeValueAsString(distinctCountAggregator);
        String expectedJSON = jsonObject.toString();
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullFields() {

        DistinctCountAggregator distinctCountAggregator = new DistinctCountAggregator(null, null);
    }

    @Test
    public void testEqualsPositive() {

        DistinctCountAggregator aggregator1 =
                new DistinctCountAggregator("uniqueStars", "allStars");
        DistinctCountAggregator aggregator2 =
                new DistinctCountAggregator("uniqueStars", "allStars");

        Assertions.assertThat(aggregator1).isEqualTo(aggregator2);
    }

    @Test
    public void testEqualsNegative() {

        DistinctCountAggregator aggregator1 =
                new DistinctCountAggregator("uniqueStars1", "allStars1");
        DistinctCountAggregator aggregator2 =
                new DistinctCountAggregator("uniqueStars2", "allStars2");

        Assertions.assertThat(aggregator1).isNotEqualTo(aggregator2);
    }
}
