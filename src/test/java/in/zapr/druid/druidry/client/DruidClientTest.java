package in.zapr.druid.druidry.client;

import in.zapr.druid.druidry.query.DruidQuery;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.List;

public class DruidClientTest {

    private boolean wasClientClosed;

    @Test
    public void testAutoClosing() {
        Assertions.assertThat(wasClientClosed).withFailMessage("Client closing indicator is not initialized properly").isFalse();
        try (TestDruidClient client = new TestDruidClient()) {
            client.connect();
        }
        Assertions.assertThat(wasClientClosed).withFailMessage("Close method has not been called").isTrue();
    }

    private class TestDruidClient implements DruidClient {

        @Override
        public void connect() {

        }

        @Override
        public void close() {
            wasClientClosed = true;
        }

        @Override
        public String query(DruidQuery druidQuery) {
            return null;
        }

        @Override
        public <T> List<T> query(DruidQuery druidQuery, Class<T> className) {
            return null;
        }

    }

}
