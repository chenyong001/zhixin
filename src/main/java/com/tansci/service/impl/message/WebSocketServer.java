package com.tansci.service.impl.message;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName： WebSocketServer.java
 * @ClassPath： com.tansci.service.impl.WebSocketServer.java
 * @Description： WebSocket
 * @Author： tanyp
 * @Date： 2022/3/3 10:35
 **/
@Slf4j
@ServerEndpoint("/ws/{id}")
@Component
public class WebSocketServer {

    public static CopyOnWriteArraySet<WebSocketServer> sessionSet = new CopyOnWriteArraySet<>();

    private Session session;

    @Getter
    private String id;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.info("=======WebSocket  建立连接 id:{}==============", id);
        this.session = session;
        this.id = id;
        sessionSet.add(this);
    }

    @OnClose
    public void onClose() {
        sessionSet.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("id") String id) {
        log.error("========websocket错误================");
        error.printStackTrace();
        if (StringUtils.isNotBlank(id)) {
            for (WebSocketServer ws : sessionSet) {
                if (ws.id.equals(id)) {
                    sessionSet.remove(ws);
                }
            }
        }
    }


    @OnMessage
    public void onMessage(Session session,@PathParam("id")String id, String message) throws IOException {
        System.out.println(message);
        // 收到客户端消息时调用
        if("ping".equalsIgnoreCase(message)){
            if (StringUtils.isNotBlank(id)) {
                for (WebSocketServer ws : sessionSet) {
                    if (ws.id.equals(id)) {
                        System.out.println("wsId:"+id+"  "+message);
                        ws.sendMessage("pong");
                    }
                }
            }
        }

    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendMessage(@PathParam("id") String id, String message) throws IOException {
        if (StringUtils.isNotBlank(id)) {
            for (WebSocketServer ws : sessionSet) {
                if (ws.id.equals(id)) {
                    ws.sendMessage(message);
                }
            }
        }
    }

}
