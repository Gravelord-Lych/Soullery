package lych.soullery.world;

import net.minecraft.util.Unit;
import net.minecraft.world.server.TicketType;

public final class ModTickets {
    public static final TicketType<Unit> SOUL_DRAGON = TicketType.create("dragon", ModTickets::alwaysEqual);

    private ModTickets() {}

    private static <T> int alwaysEqual(T t1, T t2) {
        return 0;
    }
}
