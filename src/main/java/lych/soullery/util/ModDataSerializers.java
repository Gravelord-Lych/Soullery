package lych.soullery.util;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.extension.ExtraAbility;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.*;

import static net.minecraft.network.datasync.DataSerializers.registerSerializer;

public class ModDataSerializers {
    public static final IDataSerializer<List<BlockPos>> BLOCK_POS_LIST = new IDataSerializer<List<BlockPos>>() {
        @Override
        public void write(PacketBuffer buffer, List<BlockPos> list) {
            buffer.writeVarInt(list.size());
            for (BlockPos pos : list) {
                buffer.writeVarInt(pos.getX());
                buffer.writeVarInt(pos.getY());
                buffer.writeVarInt(pos.getZ());
            }
        }

        @Override
        public List<BlockPos> read(PacketBuffer buffer) {
            int size = buffer.readVarInt();
            List<BlockPos> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                int x = buffer.readVarInt();
                int y = buffer.readVarInt();
                int z = buffer.readVarInt();
                list.add(new BlockPos(x, y, z));
            }
            return list;
        }

        @Override
        public List<BlockPos> copy(List<BlockPos> list) {
            return new ArrayList<>(list);
        }
    };
    public static final IDataSerializer<Set<IExtraAbility>> EXA = new IDataSerializer<Set<IExtraAbility>>() {
        @Override
        public void write(PacketBuffer buffer, Set<IExtraAbility> set) {
            buffer.writeVarInt(set.size());
            for (IExtraAbility exa : set) {
                buffer.writeResourceLocation(exa.getRegistryName());
            }
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        @Override
        public Set<IExtraAbility> read(PacketBuffer buffer) {
            int size = buffer.readVarInt();
            Set<IExtraAbility> set = new LinkedHashSet<>();
            for (int i = 0; i < size; i++) {
                ResourceLocation location = buffer.readResourceLocation();
                set.add(ExtraAbility.getOptional(location).get());
            }
            return set;
        }

        @Override
        public Set<IExtraAbility> copy(Set<IExtraAbility> set) {
            return new LinkedHashSet<>(set);
        }
    };
    public static final IDataSerializer<Long> LONG = new IDataSerializer<Long>() {
        @Override
        public void write(PacketBuffer buffer, Long l) {
            buffer.writeVarLong(l);
        }

        @Override
        public Long read(PacketBuffer buffer) {
            return buffer.readVarLong();
        }

        @Override
        public Long copy(Long l) {
            return l;
        }
    };
    public static final IDataSerializer<Optional<Color>> OPTIONAL_COLOR = new IDataSerializer<Optional<Color>>() {

        @Override
        public void write(PacketBuffer buffer, Optional<Color> optional) {
            if (optional.isPresent()) {
                Color color = optional.get();
                buffer.writeVarInt(color.getRed());
                buffer.writeVarInt(color.getGreen());
                buffer.writeVarInt(color.getBlue());
            } else {
                buffer.writeVarInt(-1);
            }
        }

        @Override
        public Optional<Color> read(PacketBuffer buffer) {
            int r = buffer.readVarInt();
            if (r == -1) {
                return Optional.empty();
            } else {
                int g = buffer.readVarInt();
                int b = buffer.readVarInt();
                return Optional.of(new Color(r, g, b));
            }
        }

        @Override
        public Optional<Color> copy(Optional<Color> optional) {
            return optional;
        }
    };

    static {
        registerSerializer(BLOCK_POS_LIST);
        registerSerializer(EXA);
        registerSerializer(LONG);
        registerSerializer(OPTIONAL_COLOR);
    }
}
