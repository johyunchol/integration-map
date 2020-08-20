package kr.co.kkensu.integrationmap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Jackson 사용이 어려워 쉽세 만들기 위해 사용한 Wrapper
 */
public class JacksonWrapper {
    private ObjectMapper objectMapper;

    public JacksonWrapper() {
        objectMapper = JacksonFactory.createMapper();
    }


    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            if (json == null)
                return null;
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JSON -> List 등 generic을 사용하는 object로 변환할때 쓰임
     * 예 : JacksonFactory.create().fromJson(userInfo.user_social, TypeFactory.defaultInstance().constructCollectionType(List.class, UserSocial.class));
     *
     * @param json object로 변환할 string
     * @param type 변환할 타입 예: TypeFactory.defaultInstance().constructCollectionType(List.class, UserSocial.class)
     * @return 결과 Object
     */
    public <T> T fromJson(String json, JavaType type) {
        try {
            if (json == null)
                return null;
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
};
