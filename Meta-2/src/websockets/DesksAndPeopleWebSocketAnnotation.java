package websockets;

import core.models.RmiConnector;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint(value = "/desksAndPeopleWS")
public class DesksAndPeopleWebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private Session session;
    private Thread thread;
    private boolean running;

    public DesksAndPeopleWebSocketAnnotation() {
        sequence.getAndIncrement();
        this.running = true;
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
                            if (connector.getServer() == null) {
                                connector = new RmiConnector();
                            }
//                            this.sendMessage(connector.getServer().ping());
                            this.sendMessage(connector.getServer().pingDesks(null));
                        } catch (Exception e){
                            e.printStackTrace();
                        };
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
