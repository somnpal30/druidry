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
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class SimpleDimensionTest {

    private ObjectMapper objectMapper;

    @BeforeClass
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAllFields() throws JacksonException, JSONException {
        SimpleDimension simpleDimension = new SimpleDimension("name");

        String actualString = objectMapper.writeValueAsString(simpleDimension);
        String expectedString = "\"name\"";

        Assertions.assertThat(expectedString).isEqualTo(actualString);
    }

    @Test
    public void testEqualsPositive() {
        SimpleDimension dimension1 = new SimpleDimension("name");
        SimpleDimension dimension2 = new SimpleDimension("name");

        Assertions.assertThat(dimension1).isEqualTo(dimension2);
    }

    @Test
    public void testEqualsNegative() {
        SimpleDimension dimension1 = new SimpleDimension("name");
        SimpleDimension dimension2 = new SimpleDimension("name1");

        Assertions.assertThat(dimension1).isNotEqualTo(dimension2);
    }

    @Test
    public void testEqualsWithAnotherSubClass() {
        SimpleDimension dimension1 = new SimpleDimension("name");
        DefaultDimension dimension2 = new DefaultDimension("name", "output", OutputType.LONG);

        Assertions.assertThat(dimension1).isNotEqualTo(dimension2);
    }
}
