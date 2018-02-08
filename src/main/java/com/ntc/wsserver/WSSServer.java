/*
 * Copyright 2017 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.wsserver;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author nghiatc
 * @since Nov 14, 2017
 * 
 * https://github.com/TooTallNate/Java-WebSocket
 * https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLServerExample.java
 * 
 * keytool -genkey -validity 3650 -keystore "server-keystore.jks" -storepass "storetest123" \
 * -keypass "keytest123" -alias "default" -dname "CN=127.0.0.1, OU=UTS, O=UTS, L=NewYork, S=WEST, C=USA"
 */
public class WSSServer extends WebSocketServer {
    public static ConcurrentHashMap<Integer, WebSocket> mapWSClient = new ConcurrentHashMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
		int port = 15051;

		WSSServer server = new WSSServer(new InetSocketAddress(host, port));
        server.initSSL(server);
		server.start();
    }
    
    public WSSServer(InetSocketAddress address) {
		super(address);
	}
    
    public WSSServer(InetSocketAddress address, Draft d) {
		super(address, Collections.singletonList(d));
	}
    
    public void initSSL(WSSServer server){
        try {
            System.out.println(">>>>> initSSL");
            // load up the key store
            String STORETYPE = "JKS";
            String KEYSTORE = "/home/nghiatc/uts/uts-wsserver/cert/server-keystore.jks";
            String STOREPASSWORD = "storetest123";
            String KEYPASSWORD = "keytest123";

            KeyStore ks = KeyStore.getInstance(STORETYPE);
            File kf = new File(KEYSTORE);
            ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); // SunX509 | PKIX
            kmf.init(ks, KEYPASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        System.out.println("WSSServer id onOpen: " + ws.hashCode());
        System.out.println("WSSServer has a new connection from " + ws.getRemoteSocketAddress());
        int userWsId = ws.hashCode();
        mapWSClient.put(userWsId, ws);
        String userAddress = ch.getFieldValue("address");
        System.out.println("++++++++ Add UserWsId=" + userWsId + ", UserAddress=" + userAddress);
        String msg = "Connected sucessfully...";
        ws.send(msg);
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        System.out.println("WSSServer closed " + ws.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        int userWsId = ws.hashCode();
        mapWSClient.remove(userWsId);
        System.out.println("-------- Remove UserWsId=" + userWsId);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        int userWsId = ws.hashCode();
        String msg = "WSSServer received message from "	+ ws.getRemoteSocketAddress() + " - userWsId=" + userWsId + ": " + message;
        System.out.println(msg);
        ws.send(msg);
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        int userWsId = ws.hashCode();
        System.err.println("WSSServer has an error occured on connection " + ws.getRemoteSocketAddress() + " - userWsId=" + userWsId + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("WSSServer is running on port: " + getPort());
    }

}
