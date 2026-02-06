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

package in.zapr.druid.druidry.postAggregator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class HyperUniqueCardinalityPostAggregatorTest {

    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testArithmeticPostAggregatorAllFields() throws JacksonException, JSONException {

        HyperUniqueCardinalityPostAggregator hyperUniqueCardinalityPostAggregator =
                new HyperUniqueCardinalityPostAggregator("Hello", "World");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "hyperUniqueCardinality");
        jsonObject.put("name", "Hello");
        jsonObject.put("fieldName", "World");

        String actualJSON = objectMapper.writeValueAsString(hyperUniqueCardinalityPostAggregator);
        String expectedJSON = jsonObject.toString();
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullName() {

        HyperUniqueCardinalityPostAggregator hyperUniqueCardinalityPostAggregator =
                new HyperUniqueCardinalityPostAggregator(null, "Haha");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullFieldName() {

        HyperUniqueCardinalityPostAggregator hyperUniqueCardinalityPostAggregator =
                new HyperUniqueCardinalityPostAggregator("Name", null);
    }

    @Test
    public void testEqualsPositive() {
        HyperUniqueCardinalityPostAggregator aggregator1 =
                new HyperUniqueCardinalityPostAggregator("Hello", "World");
        HyperUniqueCardinalityPostAggregator aggregator2 =
                new HyperUniqueCardinalityPostAggregator("Hello", "World");

        Assertions.assertThat(aggregator1).isEqualTo(aggregator2);
    }

    @Test
    public void testEqualsNegative() {
        HyperUniqueCardinalityPostAggregator aggregator1 =
                new HyperUniqueCardinalityPostAggregator("Hello", "World");
        HyperUniqueCardinalityPostAggregator aggregator2 =
                new HyperUniqueCardinalityPostAggregator("Hola", "Duniya");

        Assertions.assertThat(aggregator1).isNotEqualTo(aggregator2);
    }

    @Test
    public void testEqualsWithAnotherSubClass() {
        HyperUniqueCardinalityPostAggregator aggregator1 =
                new HyperUniqueCardinalityPostAggregator("Hello", "Yaha");
        FieldAccessPostAggregator aggregator2 = new FieldAccessPostAggregator("Hello", "Yaha");

        Assertions.assertThat(aggregator1).isNotEqualTo(aggregator2);
    }
}
