package org.example.nrpc.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author 江南小俊
 * @since 2021/7/7
 **/
class RpcClientTest {
    private RpcClient rpcClient;

    @BeforeEach
    void setUp() {
        rpcClient = new RpcClient("127.0.0.1", 8000);
        rpcClient.init();
    }

    @AfterEach
    void tearDown() {
        rpcClient.destroy();
    }

    @Test
    void run() {
        Thread thread = new Thread(rpcClient);
        thread.setDaemon(true);
        thread.start();
    }
}