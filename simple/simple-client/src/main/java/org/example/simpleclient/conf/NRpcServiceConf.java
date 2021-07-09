package org.example.simpleclient.conf;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@NRpcServiceScan({"org.example.nrpc.simple.api"})
public class NRpcServiceConf {
}
