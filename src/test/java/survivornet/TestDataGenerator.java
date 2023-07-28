package survivornet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import survivornet.utils.DataGenerator;

public class TestDataGenerator {


    @Test
    @Disabled
    void test() {
        DataGenerator.generateData(500, 500, 1000);
    }
}
