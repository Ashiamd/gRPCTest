package ash.grpctest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2021/8/16 3:05 AM
 */
@Slf4j
public class ChatServer {

    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
        chatServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        int port = 5914;

        server = ServerBuilder.forPort(port)
                .addService(new ChatServiceGrpcImpl())
                .build()
                .start();

        log.info("Server started, listening on : {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ChatServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private class ChatServiceGrpcImpl extends ChatServiceGrpc.ChatServiceImplBase {

        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void chatMyself(ChatRequest request, StreamObserver<ChatResponse> responseObserver) {
            Message chatMessage = request.getChatMessage();
            User user = chatMessage.getUser();
            long sendTime = chatMessage.getSendTime();
            String msg = chatMessage.getMsg();

            log.info("User : {}", new Gson().toJson(user));
            log.info("sendTime : {}", simpleDateFormat.format(sendTime));
            log.info("msg : {}", msg);

            responseObserver.onNext(ChatResponse.newBuilder()
                    .setChatMessage(chatMessage)
                    .build());

            responseObserver.onCompleted();
        }
    }

}
