package example.com;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketModule {
    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;

        // 소켓 이벤트 리스너 등록
        server.addConnectListener(listenConnected());
        server.addDisconnectListener(listenDisconnected());

        server.addEventListener("send_message", Message.class, onChatReceived());

    }


    private DataListener<Message> onChatReceived() {
//        return (senderClient, data, ackSender) -> {
//            log.info(data.toString());
//            log.info("client namespace: ", senderClient.getNamespace().getName());
//
//            senderClient.getNamespace().getBroadcastOperations().sendEvent("get_message", data.getMessage());
//            //senderClient.sendEvent("get_message", data.getMessage());
//        };

        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendMessage(data.getRoom(), "get_message", senderClient, data.getMessage());
        };
    }


    /**
     * 클라이언트 연결 리스너
     */
    public ConnectListener listenConnected() {
//        return (client) -> {
//            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
//            log.info("connect: " + params.toString());
//        };

        // client의 요청 url에 담긴 room 값으로 room에 join시키기
        return (client) -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            client.joinRoom(room);
            log.info("Socket ID[{}]  Connected to socket, room name: {}", client.getSessionId().toString(), room);
        };
    }

    /**
     * 클라이언트 연결 해제 리스너
     */
    public DisconnectListener listenDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info("disconnect: " + sessionId);
            client.disconnect();
        };
    }

}
