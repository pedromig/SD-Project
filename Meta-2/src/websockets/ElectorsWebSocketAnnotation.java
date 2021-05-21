package websockets;

import core.models.RmiConnector;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/electorsWS")
public class ElectorsWebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private Session session;
    private Thread thread;
    private boolean running;
    private String electionName;

    public ElectorsWebSocketAnnotation() {
        sequence.getAndIncrement();
        this.running = true;
        this.electionName = null;
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        thread = new Thread(
                () -> {
                    RmiConnector connector = new RmiConnector();
                    while (running) {
                        try {
                            Thread.sleep(2000);
                            if (electionName != null) {
                                if (connector.getServer() == null) {
                                    connector = new RmiConnector();
                                }
                                this.sendMessage(electionName);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        thread.start();
    }

    @OnClose
    public void end() {
        this.running = false;
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void receiveMessage(String message) {
        this.electionName = message;
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    private void sendMessage(String text) {
        try {
            this.session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
