package miui.statusbar.lyric.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public final class ShellUtils {

    public static void voidShell(String command, boolean isSu) {

        try {
            if (isSu) {
                Process p = Runtime.getRuntime().exec("su");
                OutputStream outputStream = p.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeBytes(command);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
            } else {
                Runtime.getRuntime().exec(command);
            }
        } catch (Throwable ignored) {
        }
    }

    public static String returnShell(String command) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (Runtime.getRuntime().exec(command).getInputStream()), 1024);
            String buffer = bufferedReader.readLine();
            bufferedReader.close();
            return buffer;
        } catch (IOException ignored) {
        }
        return "";
    }

}
