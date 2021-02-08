package be.unamur.typhonevo.sqlextractor.qlextractor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UUIDGenerator {

	
	public static String get(String tableName, List<String> params) throws UnsupportedEncodingException {
		if(params == null || params.size() == 0) {
			return UUID.randomUUID().toString();
		}
		
		
		String source = tableName + "//";
		for (String param : params) {
			source += ":" + param;
		}
		byte[] bytes = source.getBytes();
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		return uuid.toString();
	}

//	public static void main(String[] args) throws UnsupportedEncodingException {
//		List<String> list = new ArrayList<String>();
//		list.add("test");
//		list.add("test2");
//		get("Order", list);
//	}

}
