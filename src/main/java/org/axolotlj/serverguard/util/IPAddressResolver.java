package org.axolotlj.serverguard.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * Utility to resolve real or logical IP address of a connection.
 */
public class IPAddressResolver {

    public static String resolve(SocketAddress address) {
        if (address instanceof InetSocketAddress inet) {
            InetAddress inetAddress = inet.getAddress();
            if (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) {
                return "localhost";
            }
            return inetAddress.getHostAddress();
        }
        return "unknown";
    }

    public static boolean isLocal(String ip) {
        return Objects.equals(ip, "localhost") || ip.startsWith("127.") || ip.equals("::1");
    }
}
