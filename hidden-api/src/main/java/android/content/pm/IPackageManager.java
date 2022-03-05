package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;

public interface IPackageManager extends IInterface {

    PackageInfo getPackageInfo(String packageName, int flags, int userId)
            throws RemoteException;

    @RequiresApi(33)
    PackageInfo getPackageInfo(String packageName, long flags, int userId)
            throws RemoteException;

    abstract class Stub extends Binder implements IPackageManager {

        public static IPackageManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}
