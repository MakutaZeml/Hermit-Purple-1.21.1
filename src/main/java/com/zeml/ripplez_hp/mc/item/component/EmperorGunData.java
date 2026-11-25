package com.zeml.ripplez_hp.mc.item.component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;


public class EmperorGunData {
    protected Optional<UUID> owner;
    protected Optional<UUID> UUIDTarget;
    protected int mode;

    public EmperorGunData() {
        this(Optional.empty(), Optional.empty(), 0);
    }

    public EmperorGunData(Optional<UUID> owner, Optional<UUID> uuidTarget, int mode) {
        this.owner = owner;
        this.UUIDTarget = uuidTarget;
        this.mode = mode;
    }

    public Optional<UUID> getUuidOwner() {
        return this.owner;
    }

    public Optional<UUID> getUuidTarget() {
        return this.UUIDTarget;
    }

    public int getMode() {
        return mode;
    }

    public void setOwner(Optional<UUID> owner) {
        this.owner = owner;
    }

    public void setUUIDTarget(Optional<UUID> uuidTarget) {
        this.UUIDTarget = uuidTarget;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    public static final Codec<EmperorGunData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("owner").xmap(
                            strOpt -> strOpt.flatMap(s -> {
                                try {
                                    return Optional.of(UUID.fromString(s));
                                } catch (IllegalArgumentException e) {
                                    return Optional.empty();
                                }
                            }),
                            uuidOpt -> uuidOpt.map(UUID::toString)
                    ).forGetter(EmperorGunData::getUuidOwner),

                    Codec.STRING.optionalFieldOf("uuidTarget").xmap(
                            strOpt -> strOpt.flatMap(s -> {
                                try {
                                    return Optional.of(UUID.fromString(s));
                                } catch (IllegalArgumentException e) {
                                    return Optional.empty();
                                }
                            }),
                            uuidOpt -> uuidOpt.map(UUID::toString)
                    ).forGetter(EmperorGunData::getUuidTarget),

                    Codec.INT.optionalFieldOf("mode", 0).forGetter(EmperorGunData::getMode)
            ).apply(instance, EmperorGunData::new)
    );

    public static final StreamCodec<FriendlyByteBuf, EmperorGunData> STREAM_CODEC = StreamCodec.of(
            EmperorGunData::encode,
            EmperorGunData::decode
    );

    @Override
    public String toString() {
        return "EmperorGunData{owner=" + owner + ", UUIDTarget=" + UUIDTarget + ", mode=" + mode + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmperorGunData that = (EmperorGunData) obj;
        return mode == that.mode &&
                owner.equals(that.owner) &&
                UUIDTarget.equals(that.UUIDTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, UUIDTarget, mode);
    }


    private static void encode(FriendlyByteBuf buf, EmperorGunData data) {
        if (data.owner.isPresent()) {
            buf.writeBoolean(true);
            buf.writeUUID(data.owner.get());
        } else {
            buf.writeBoolean(false);
        }

        if (data.UUIDTarget.isPresent()) {
            buf.writeBoolean(true);
            buf.writeUUID(data.UUIDTarget.get());
        } else {
            buf.writeBoolean(false);
        }

        buf.writeVarInt(data.mode);
    }

    private static EmperorGunData decode(FriendlyByteBuf buf) {
        Optional<UUID> owner = Optional.empty();
        if (buf.readBoolean()) {
            owner = Optional.of(buf.readUUID());
        }

        Optional<UUID> target = Optional.empty();
        if (buf.readBoolean()) {
            target = Optional.of(buf.readUUID());
        }

        int mode = buf.readVarInt();

        return new EmperorGunData(owner, target, mode);
    }
}
