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

import in.zapr.druid.druidry.dimension.enums.OutputType;
import in.zapr.druid.druidry.extractionFunctions.ExtractionFunction;
import in.zapr.druid.druidry.extractionFunctions.PartialExtractionFunction;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class ExtractionDimensionTest {
    private static ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSampleExtractionFunction() throws JSONException, JacksonException {

        ExtractionFunction partialExtractionFunction =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension extractionDimension =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.FLOAT)
                        .extractionFunction(partialExtractionFunction)
                        .build();

        String actualJSON = objectMapper.writeValueAsString(extractionDimension);

        String expectedJSONString =
                """

                {
                  "type" : "extraction",
                  \
                "dimension" : "name",
                  "outputName" :  "nombre",
                 \
                 "outputType": "FLOAT",
                  \
                "extractionFn" : { "type" : "partial", "expr" : "abcd" }
                }

                """;

        JSONAssert.assertEquals(expectedJSONString, actualJSON, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testRequiredFields() throws JacksonException, JSONException {
        ExtractionFunction partialExtractionFunction =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension extractionDimension =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .extractionFunction(partialExtractionFunction)
                        .build();

        String actualJSON = objectMapper.writeValueAsString(extractionDimension);

        String expectedJSONString =
                """

                {
                  "type" : "extraction",
                  \
                "dimension" : "name",
                  "outputName" :  "nombre",
                  \
                "extractionFn" : { "type" : "partial", "expr" : "abcd" }
                }

                """;

        JSONAssert.assertEquals(expectedJSONString, actualJSON, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDimensionMissingFields() {
        ExtractionFunction partialExtractionFunction =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension extractionDimension =
                ExtractionDimension.builder()
                        .outputName("nombre")
                        .extractionFunction(partialExtractionFunction)
                        .build();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOutputNameMissingFields() {
        ExtractionFunction partialExtractionFunction =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension extractionDimension =
                ExtractionDimension.builder()
                        .dimension("name")
                        .extractionFunction(partialExtractionFunction)
                        .build();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractionFunctionMissingFields() {

        ExtractionDimension extractionDimension =
                ExtractionDimension.builder().dimension("name").outputName("nombre").build();
    }

    @Test
    public void testEqualsPositive() {
        ExtractionFunction partialExtractionFunction1 =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension dimension1 =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.FLOAT)
                        .extractionFunction(partialExtractionFunction1)
                        .build();

        ExtractionFunction partialExtractionFunction2 =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension dimension2 =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.FLOAT)
                        .extractionFunction(partialExtractionFunction2)
                        .build();

        Assertions.assertThat(dimension1).isEqualTo(dimension2);
    }

    @Test
    public void testEqualsNegative() {
        ExtractionFunction partialExtractionFunction1 =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension dimension1 =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.LONG)
                        .extractionFunction(partialExtractionFunction1)
                        .build();

        ExtractionFunction partialExtractionFunction2 =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension dimension2 =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.FLOAT)
                        .extractionFunction(partialExtractionFunction2)
                        .build();

        Assertions.assertThat(dimension1).isNotEqualTo(dimension2);
    }

    @Test
    public void testEqualsWithAnotherSubClass() {
        SimpleDimension dimension1 = new SimpleDimension("name");
        ExtractionFunction partialExtractionFunction1 =
                PartialExtractionFunction.builder().expr("abcd").build();

        ExtractionDimension dimension2 =
                ExtractionDimension.builder()
                        .dimension("name")
                        .outputName("nombre")
                        .outputType(OutputType.LONG)
                        .extractionFunction(partialExtractionFunction1)
                        .build();

        Assertions.assertThat(dimension1).isNotEqualTo(dimension2);
    }
}
