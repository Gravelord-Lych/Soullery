package lych.soullery.test;

import lych.soullery.Soullery;
import lych.soullery.api.SoulleryAPI;
import lych.soullery.util.impl.SoulleryAPIImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SoulleryTest {
    @Test
    public void testModid() {
        Assertions.assertEquals(SoulleryAPI.MOD_ID, Soullery.MOD_ID);
    }

    @Test
    public void testAPI() {
        Assertions.assertSame(SoulleryAPI.getInstance(), SoulleryAPIImpl.INSTANCE);
    }
}
