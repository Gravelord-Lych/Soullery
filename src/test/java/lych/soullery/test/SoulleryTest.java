package lych.soullery.test;

import lych.soullery.Soullery;
import lych.soullery.api.SoulleryAPI;
import lych.soullery.util.Vectors;
import lych.soullery.util.impl.SoulleryAPIImpl;
import net.minecraft.util.math.vector.Vector3d;
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

    @Test
    public void testVectors() {
        Assertions.assertEquals(Vectors.getAngle(new Vector3d(0, 0, 1), new Vector3d(1, 0, 0)), Math.PI / 2);
        Assertions.assertEquals(Vectors.getAngle(new Vector3d(0, 1, 1), new Vector3d(0, -1, -1)), Math.PI);
        Assertions.assertEquals(Vectors.getAngle(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0)), Math.PI / 4);
    }
}
