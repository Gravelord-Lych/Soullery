package lych.soullery.util;

public interface IIdentifiableEnum {
    int ordinal();

    default int getId() {
        return ordinal();
    }

    static <E extends Enum<E> & IIdentifiableEnum> E byOrdinal(E[] values, int id) throws EnumConstantNotFoundException {
        if (id >= 0 && id < values.length) {
            return values[id];
        }
        throw new EnumConstantNotFoundException(id);
    }

    static <E extends Enum<E> & IIdentifiableEnum> E byId(E[] values, int id) throws EnumConstantNotFoundException {
        for (E e : values) {
            if (e.getId() == id) {
                return e;
            }
        }
        throw new EnumConstantNotFoundException(id);
    }
}
