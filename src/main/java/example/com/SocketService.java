package example.com;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SocketService {

    public void sendMessage(String room, String eventName, SocketIOClient senderClient, String message) {
        for (
                SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) { // 해당 room에 들어와있는 모든 client에 대해
            if (!client.getSessionId().equals(senderClient.getSessionId())) { // 메시지 전송자가 아닌 모든 client에게 이벤트 발행
                client.sendEvent(eventName,
                        new Message(MessageType.SERVER, message));
            }
        }
    }
}
