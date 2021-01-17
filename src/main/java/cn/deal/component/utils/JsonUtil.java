package cn.deal.component.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.deal.component.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;


public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public static String toJson(Object src) {
        try {
			return objectMapper.writeValueAsString(src);
		} catch (JsonProcessingException e) {
			log.error("toJson:",e);
			return null;
		}
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (StringUtils.isBlank(json)){
            log.error("the json parameter should not be blank");
            throw new RuntimeException("the parameter should not be blank");
        }

        try {
			return objectMapper.readValue(json, classOfT);
		} catch(IOException e) {
			log.error("error handle in fromJson", e.getMessage());
        	return null;
		}
    }

    /**
     * 将json反序列化为对象(可处理jsonarray）
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference){
        try {
            return new ObjectMapper().readValue(json, typeReference);

        } catch (IOException e) {
        	log.error("error handle in fromJson", e.getMessage());
        	return null;
        }
    }

	public static <T> T fromJsonV2(String json, TypeReference<T> typeReference){
		try {
			return new ObjectMapper().readValue(json, typeReference);

		} catch (IOException e) {
			log.error("error handle in fromJson", e.getMessage());
			throw new BusinessException("deserialize_error", e.getMessage());
		}
	}

    /**
	 * 将json转化为实体POJO
	 * 
	 * @param jsonStr
	 * @param obj
	 * @return
	 */
	public static <T> Object JSONToObj(String jsonStr, Class<T> obj) {
		T t = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			t = objectMapper.readValue(jsonStr, obj);
		} catch (Exception e) {
			log.error("JsonUtil parse json from file, parse error: ", e);
		}
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Object JSONReturnObj(JSONObject res, Class<T> obj) {
		T t = null;
		if (res.has("status")) {
			int status = res.getInt("status");

			if (status == 1 && res.has("data")) {
				Object obj1 = res.get("data");

				if (obj1 != null) {
					t= (T) parseJson(obj1.toString(), obj);
				}
			}
		}
		
		return t ;
	}

	public static String getReturnJson(JSONObject res){
		String json = null;
		if (res.has("status")) {
			int status = res.getInt("status");
			if (status == 1 && res.has("data")) {
				Object obj1 = res.get("data");

				if (obj1 != null) {
					json = obj1.toString();
				}
			}
		}
		return json;
	}
	
	public static Object parseJson(String json, Class clazz) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
	        	.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

	            @Override
	            public Date deserialize(final JsonElement json, final Type typeOfT,
	                    JsonDeserializationContext context) {

					return DateUtils.parseDate(json.getAsString());
				}
	        })
	        .create();
		return gson.fromJson(json, clazz);
	}

	public static Object parseJson(String json, Type typeOfT) {
		Gson gson = new GsonBuilder()
	        .setPrettyPrinting()
	        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

	            @Override
	            public Date deserialize(final JsonElement json, final Type typeOfT,
	                    JsonDeserializationContext context) {
					return DateUtils.parseDate(json.getAsString());
	            }
	        })
	        .create();
		return gson.fromJson(json, typeOfT);
	}
	
	public static Object getJsonObjFromFile(String path) {

		JSONParser jsonParser = new JSONParser();
		URL resource = JsonUtil.class.getClassLoader().getResource(path);

		if (resource != null) {
			Object obj;

			try {
				obj = jsonParser.parse(new FileReader(resource.getPath()));
				
				return obj;

			} catch (FileNotFoundException e) {
				log.error("JsonUtil get json obj from file, file not found error: ", e);

			} catch (IOException e) {
				log.error("JsonUtil get json obj from file, io error: ", e);

			} catch (org.json.simple.parser.ParseException e) {
				log.error("JsonUtil get json obj from file, ParseException error: ", e);

			}

			return null;
		}

		return null;
	}
	
	public static <T> T jsonToObject(String json, TypeReference<T> typeReference){
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            log.warn("jsonArrayToObject:",e);
        } catch (JsonMappingException e) {
            log.warn("jsonArrayToObject:",e);
        } catch (IOException e) {
            log.warn("jsonArrayToObject:",e);
        }
        return null;
    }

	/**
	 * 将JOSN转换为Map
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, String> jsonToMap(String jsonStr) {
		TypeReference<Map<String, String>> ref = new TypeReference<Map<String, String>>() { };
		Map<String, String> data = (Map<String, String>)jsonToObject(jsonStr, ref);
		return data;
	}
}
