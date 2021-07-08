package org.example.nrpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 江南小俊
 * @since 2021/7/8
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RpcAddress {
    private String host;
    private Integer port;
}
