package survivornet;

import org.junit.jupiter.api.Test;
import survivornet.utils.DataGenerator;

public class TestDataGenerator {


    @Test
    void test() {
        DataGenerator.generateData(500, 500, 1000);
    }
}
