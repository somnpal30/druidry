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

package in.zapr.druid.druidry.dimension;

import tools.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;

public class RegexFilteredDimensionTest {
    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSampleRegexFilteredDimension() throws JacksonException, JSONException {
        DimensionSpec dimensionSpec = DefaultDimension.builder()
                .dimension("system_label_values")
                .outputName("system_label_values")
                .build();

        RegexFilteredDimension regexFilteredDimension = RegexFilteredDimension.builder()
                .dimensionSpec(dimensionSpec)
                .pattern("compute.googleapis.com/cores`.*")
                .build();

        String jsonOutput = objectMapper.writeValueAsString(regexFilteredDimension);
        String expectedJSONString = """
                {
                      "type": "regexFiltered",
                      "delegate": {
                        "type": "default",
                        "dimension": "system_label_values",
                        "outputName": "system_label_values"
                      },
                      "pattern": "compute.googleapis.com/cores`.*"
                    }\
                """;
        JSONAssert.assertEquals(expectedJSONString, jsonOutput, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSampleRegexFilteredDimensionWithNullDimensionSpec() throws JacksonException {
        RegexFilteredDimension regexFilteredDimension = RegexFilteredDimension.builder()
                .pattern("compute.googleapis.com/cores`.*")
                .build();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSampleRegexFilteredDimensionWithNullRegex() throws JacksonException {
        DimensionSpec dimensionSpec = DefaultDimension.builder()
                .dimension("system_label_values")
                .outputName("system_label_values")
                .build();
        RegexFilteredDimension regexFilteredDimension = RegexFilteredDimension.builder()
                .dimensionSpec(dimensionSpec)
                .build();
    }


}
