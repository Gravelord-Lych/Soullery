package lych.soullery.api;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

final class APIConstants {
    static final Logger LOGGER = LogManager.getLogger(SoulleryAPI.MOD_ID + "-api");
    static final Supplier<SoulleryAPI> INSTANCES = Suppliers.memoize(() -> {
        try {
            Class<?> clazz = Class.forName("lych.soullery.util.impl.SoulleryAPIImpl");
            return (SoulleryAPI) clazz.getField("INSTANCE").get(null);
        } catch (ReflectiveOperationException e) {
            return new SoulleryAPIDummyImpl();
        } catch (RuntimeException e) {
            LOGGER.error("No valid SoulCraftAPIImpl found, use a dummy instead");
            return new SoulleryAPIDummyImpl();
        }
    });

    private APIConstants() {}
}
