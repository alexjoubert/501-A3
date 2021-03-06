package main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

//Code adapted from Java Reflection: In Action textbook
public class Deserializer {

	public static Object deserialize(Document source) throws Exception {
		List<Element> objList = source.getRootElement().getChildren();
		Map<Object, Object> table = new HashMap<Object, Object>();

		createInstances(table, objList);
		assignFieldValues(table, objList);

		return table.get("0");
	}

	public static Document fileToDocument(File file) {
		SAXBuilder builder = new SAXBuilder();
		Document document = null;

		try {
			document = (Document) builder.build(file);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return document;
	}

	public static List<Object> deserialize(List<Document> docs) {
		List<Object> objs = new ArrayList<Object>();

		try {
			for (Document doc : docs)
				objs.add(deserialize(doc));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objs;
	}

	public static List<Document> fileToDocument(List<File> files) {
		List<Document> docs = new ArrayList<Document>();

		for (File file : files)
			docs.add(fileToDocument(file));

		return docs;
	}

	private static void createInstances(Map<Object, Object> map, List<Element> objList) throws Exception {
		for (int i = 0; i < objList.size(); i++) {
			Element objElem = objList.get(i);
			Class<?> cls = Class.forName(objElem.getAttributeValue("class"));
			Object instance = null;
			if (!cls.isArray()) {
				Constructor<?> c = cls.getDeclaredConstructor(null);
				if (!Modifier.isPublic(c.getModifiers()))
					c.setAccessible(true);
				instance = c.newInstance(null);
			} else {
				instance = Array.newInstance(cls.getComponentType(),
						Integer.parseInt(objElem.getAttributeValue("length")));
			}
			map.put(objElem.getAttributeValue("id"), instance);
		}
	}

	private static void assignFieldValues(Map<Object, Object> map, List<Element> objList) throws Exception {
		for (int i = 0; i < objList.size(); i++) {
			Element objElem = objList.get(i);
			Object instance = map.get(objElem.getAttributeValue("id"));
			List<Element> fieldElems = objElem.getChildren();
			if (!instance.getClass().isArray()) {
				for (int j = 0; j < fieldElems.size(); j++) {
					Element fieldElem = fieldElems.get(j);
					String className = fieldElem.getAttributeValue("declaringclass");
					Class<?> fieldDC = Class.forName(className);
					String fieldName = fieldElem.getAttributeValue("name");
					Field f = fieldDC.getDeclaredField(fieldName);
					if (!Modifier.isPublic(f.getModifiers())) {
						f.setAccessible(true);
					}
					Element vElt = (Element) fieldElem.getChildren().get(0);
					f.set(instance, deserializeValue(vElt, f.getType(), map));
				}
			} else {
				Class<?> compType = instance.getClass().getComponentType();
				for (int j = 0; j < fieldElems.size(); j++) {
					Array.set(instance, j, deserializeValue(fieldElems.get(j), compType, map));
				}
			}
		}
	}

	private static Object deserializeValue(Element vElt, Class<?> fieldType, Map<Object, Object> table) throws ClassNotFoundException {
		String valtype = vElt.getName();

		if (valtype.equals("null")) {
			return null;
		} else if (valtype.equals("reference")) {
			return table.get(vElt.getText());
		} else if (fieldType.equals(boolean.class)) {
			if (vElt.getText().equals("true")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else if (fieldType.equals(byte.class)) {
			return Byte.valueOf(vElt.getText());
		} else if (fieldType.equals(short.class)) {
			return Short.valueOf(vElt.getText());
		} else if (fieldType.equals(int.class)) {
			return Integer.valueOf(vElt.getText());
		} else if (fieldType.equals(long.class)) {
			return Long.valueOf(vElt.getText());
		} else if (fieldType.equals(float.class)) {
			return Float.valueOf(vElt.getText());
		} else if (fieldType.equals(double.class)) {
			return Double.valueOf(vElt.getText());
		} else if (fieldType.equals(char.class)) {
			return new Character(vElt.getText().charAt(0));
		} else {
			return vElt.getText();
		}
	}
}
