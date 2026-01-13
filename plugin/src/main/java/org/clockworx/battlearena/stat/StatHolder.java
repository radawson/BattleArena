package org.clockworx.battlearena.stat;

import org.clockworx.battlearena.util.Describable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public interface StatHolder extends Describable {

    <T> Optional<T> stat(ArenaStat<T> stat);

    @Nullable
    <T> T getStat(ArenaStat<T> stat);

    <T> void setStat(ArenaStat<T> stat, T value);

    <T> void computeStat(ArenaStat<T> stat, Function<? super T, ? extends T> computeFunction);
}
