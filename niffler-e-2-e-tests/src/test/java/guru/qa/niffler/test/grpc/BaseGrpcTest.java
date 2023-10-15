package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.GrpsTest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

@GrpsTest
public abstract class BaseGrpcTest {
    protected static final Empty EMPTY = Empty.getDefaultInstance();
    protected static final Config CFG = Config.getInstance();
    private static Channel channel;

    static {
        channel = ManagedChannelBuilder
                .forAddress(CFG.getCurrencyGrpcAddress(), CFG.getCurrencyGrpcPort())
                .intercept(new AllureGrpc())
                .usePlaintext() //позволяет обмениваться сообщениями без шифрования
                .build();
    }

    protected final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub currencyStub =
            NifflerCurrencyServiceGrpc.newBlockingStub(channel);
}
