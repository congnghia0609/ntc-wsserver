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

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author nghiatc
 * @since Nov 14, 2017
 */
public class WSServer extends WebSocketServer {
    public static ConcurrentHashMap<Integer, WebSocket> mapWSClient = new ConcurrentHashMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String host = "localhost";
		int port = 8787;

		WSServer server = new WSServer(new InetSocketAddress(host, port));
        //server.setWebSocketFactory(wsf);
		server.run();
    }
    
    public WSServer(InetSocketAddress address) {
		super(address);
	}

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        System.out.println("WSServer id onOpen: " + ws.hashCode());
        System.out.println("WSServer has a new connection from " + ws.getRemoteSocketAddress());
        int userWsId = ws.hashCode();
        mapWSClient.put(userWsId, ws);
        String userAddress = ch.getFieldValue("address");
        System.out.println("++++++++ Add UserWsId=" + userWsId + ", UserAddress=" + userAddress);
        String msg = "Connected sucessfully...";
        ws.send(msg);
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        System.out.println("WSServer closed " + ws.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        int userWsId = ws.hashCode();
        mapWSClient.remove(userWsId);
        System.out.println("-------- Remove UserWsId=" + userWsId);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        int userWsId = ws.hashCode();
        String msg = "WSServer received message from "	+ ws.getRemoteSocketAddress() + " - userWsId=" + userWsId + ": " + message;
        System.out.println(msg);
        ws.send(msg);
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        int userWsId = ws.hashCode();
        System.err.println("WSServer has an error occured on connection " + ws.getRemoteSocketAddress() + " - userWsId=" + userWsId + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("WSServer is running on port: " + getPort());
    }

}
