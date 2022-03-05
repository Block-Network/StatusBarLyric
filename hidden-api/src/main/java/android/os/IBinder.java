package android.os;

import android.annotation.NonNull;

import java.io.FileDescriptor;

public interface IBinder {

    void linkToDeath(@NonNull DeathRecipient recipient, int flags);

    boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags);

    boolean transact(int code, @NonNull Parcel data, Parcel reply, int flags);

    String getInterfaceDescriptor();

    IInterface queryLocalInterface(@NonNull String descriptor);

    void dump(@NonNull FileDescriptor fd, String[] args);

    void dumpAsync(@NonNull FileDescriptor fd, String[] args);

    boolean isBinderAlive();

    interface DeathRecipient {
        void binderDied();
    }
}
