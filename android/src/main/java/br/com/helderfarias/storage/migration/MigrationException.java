package br.com.helderfarias.storage.migration;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
@Keep
public final class MigrationException extends Exception {

    MigrationException(@NonNull final String message, @NonNull final Exception inner) {
        super(message, inner);
    }
}
