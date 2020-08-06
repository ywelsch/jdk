/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

 /*
 * @test
 * @bug 8038145
 * @summary Add support for per Socket configuration of TCP_USER_TIMEOUT
 * @modules jdk.net
 * @run main TcpUserTimeoutTest
 */
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import jdk.net.ExtendedSocketOptions;

public class TcpUserTimeoutTest {

    private static final int DEFAULT_USER_TIMEOUT = 10000;
    
    public static void main(String args[]) throws IOException {
        var loopback = InetAddress.getLoopbackAddress();
        try (ServerSocket ss = boundServer(loopback);
                Socket s = new Socket(loopback, ss.getLocalPort());
                DatagramSocket ds = new DatagramSocket(0);
                MulticastSocket mc = new MulticastSocket(0)) {
            if (ss.supportedOptions().contains(ExtendedSocketOptions.TCP_USER_TIMEOUT)) {
                ss.setOption(ExtendedSocketOptions.TCP_USER_TIMEOUT, DEFAULT_USER_TIMEOUT);
                if (ss.getOption(ExtendedSocketOptions.TCP_USER_TIMEOUT) != DEFAULT_USER_TIMEOUT) {
                    throw new RuntimeException("Test failed, TCP_USER_TIMEOUT should have been " + DEFAULT_USER_TIMEOUT);
                }
            }
            if (s.supportedOptions().contains(ExtendedSocketOptions.TCP_USER_TIMEOUT)) {
                s.setOption(ExtendedSocketOptions.TCP_USER_TIMEOUT, DEFAULT_USER_TIMEOUT);
                if (s.getOption(ExtendedSocketOptions.TCP_USER_TIMEOUT) != DEFAULT_USER_TIMEOUT) {
                    throw new RuntimeException("Test failed, TCP_USER_TIMEOUT should have been " + DEFAULT_USER_TIMEOUT);
                }
            }
            if (ds.supportedOptions().contains(ExtendedSocketOptions.TCP_USER_TIMEOUT)) {
                throw new RuntimeException("Test failed, TCP_USER_TIMEOUT is applicable"
                        + " for TCP Sockets only.");
            }
            if (mc.supportedOptions().contains(ExtendedSocketOptions.TCP_USER_TIMEOUT)) {
                throw new RuntimeException("Test failed, TCP_USER_TIMEOUT is applicable"
                        + " for TCP Sockets only");
            }
        }
    }

    private static ServerSocket boundServer(InetAddress address) throws IOException {
        var socketAddress = new InetSocketAddress(address, 0);
        var server = new ServerSocket();
        server.bind(socketAddress);
        return server;
    }
}
