package org.example.nrpc.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * protostuff 序列化 反序列化
 *
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class ProtostuffUtil {
    private static class SerializeData {
        private Object target;
    }

    @SuppressWarnings("unchecked")
    public static byte[] serialize(Object object) {
        SerializeData serializeData = new SerializeData();
        serializeData.target = object;
        Class<SerializeData> clazz = (Class<SerializeData>) serializeData.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024 * 4);
        Schema<SerializeData> schema = RuntimeSchema.getSchema(clazz);
        return ProtostuffIOUtil.toByteArray(serializeData, schema, linkedBuffer);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        Schema<SerializeData> schema = RuntimeSchema.getSchema(SerializeData.class);
        SerializeData serializeData = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, serializeData, schema);
        return (T) serializeData.target;
    }
}
