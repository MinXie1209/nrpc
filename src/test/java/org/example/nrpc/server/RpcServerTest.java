package org.example.nrpc.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
class RpcServerTest {
    private RpcServer rpcServer;

    @BeforeEach
    void setUp() {
        rpcServer = new RpcServer(8080);
        rpcServer.init();
    }


    @Test
    void run() {
        Thread thread = new Thread(rpcServer);
        thread.setDaemon(true);
        thread.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        rpcServer.destroy();
    }
}