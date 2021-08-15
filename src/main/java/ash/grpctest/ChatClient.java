package ash.grpctest;

import com.google.gson.Gson;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2021/8/16 3:36 AM
 */
@Slf4j
public class ChatClient {

    private ManagedChannel channel;
    private ChatServiceGrpc.ChatServiceBlockingStub blockingStub;

    public ChatClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        blockingStub = ChatServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private void chatMyself(String msg) {
        ChatRequest chatRequest = ChatRequest.newBuilder()
                .setChatMessage(Message.newBuilder()
                        .setUser(User.newBuilder()
                                .setName("test-user")
                                .setAge(999)
                                .setSex(1)
                                .build())
                        .setSendTime(System.currentTimeMillis())
                        .setMsg(msg)
                        .build())
                .build();

        log.info("chatRequest : {}", new Gson().toJson(chatRequest));

        ChatResponse chatResponse = blockingStub.chatMyself(chatRequest);

        log.info("chatResponse : {}", new Gson().toJson(chatResponse));
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 5914);
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String msg = scanner.nextLine();
            chatClient.chatMyself(msg);
        }
    }
}
