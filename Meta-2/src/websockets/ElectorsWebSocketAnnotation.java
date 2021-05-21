package websockets;

import core.models.RmiConnector;
import utils.Vote;
import utils.elections.Election;
import utils.people.Person;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
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
                                Election<?> election = connector.getElection(electionName);
                                this.sendMessage(connector.getServer().printVotingProcessedData(null, election));
                                CopyOnWriteArrayList<Vote> votes = election.getVotes();
                                for (Vote v : votes) {
                                    Person person = connector.getPerson(v.getPersonID());
                                    this.sendMessage("[ID -> "+ v.getPersonID() +"][" + person.getType() + "][" + person.getDepartment() + "][" + v.getVotingDeskID() + "]");
                                }
                                this.sendMessage("");
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
