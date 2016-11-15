package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

//Code adapted from Java Reflection: In Action textbook
public class Serializer {

	public List<File> toFile(List<Object> objs) {
		List<File> files = new ArrayList<File>();
		int i = 0;
		for (Document doc : serializeList(objs)) {
			XMLOutputter xmlOutput = new XMLOutputter();

			xmlOutput.setFormat(Format.getPrettyFormat());
			try {
				xmlOutput.output(doc, new FileWriter("Serialized" + ++i + ".xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			files.add(new File("Serialized" + i + ".xml"));
		}
		return files;
	}
	
	public List<Document> serializeList(List<Object> objs) {
		List<Document> docs = new ArrayList<Document>();
		for (Object obj : objs)
			docs.add(serialize(obj));
		return docs;
	}


	public Document serialize(Object obj) {
		return serialize(obj, new Document(new Element("serialized")), new HashMap<Object, String>());
	}

	public static Document serialize(Object obj, Document target, Map<Object, String> map) {
		String id = Integer.toString(map.size());
		map.put(obj, id);
		Class<?> objclass = obj.getClass();

		Element objElem = new Element("object");
		objElem.setAttribute("class", objclass.getName());
		objElem.setAttribute("id", id);
		target.getRootElement().addContent(objElem);

		if (objclass.isArray()) {
			Class<?> componentType = objclass.getComponentType();

			int length = Array.getLength(obj);
			objElem.setAttribute("length", Integer.toString(length));
			for (int i = 0; i < length; i++) {
				objElem.addContent(serializeVariable(componentType, Array.get(obj, i), target, map));
			}
		} else {
			for (Field field : getNonStaticFields(objclass)) {
				field.setAccessible(true);
				Element fieldElem = new Element("field");
				fieldElem.setAttribute("name", field.getName());
				Class<?> declaringClass = field.getDeclaringClass();
				fieldElem.setAttribute("declaringclass", declaringClass.getName());

				Class<?> fieldtype = field.getType();
				Object child = null;
				try {
					child = field.get(obj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				fieldElem.addContent(serializeVariable(fieldtype, child, target, map));

				objElem.addContent(fieldElem);
			}
		}

		return target;
	}

	private static List<Field> getNonStaticFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()))
				fields.add(field);
		}
		if (clazz.getSuperclass() != null)
			fields.addAll(getNonStaticFields(clazz.getSuperclass()));
		return fields;
	}
	
	private static Element serializeVariable(Class<?> fieldtype, Object child, Document target,
			Map<Object, String> map) {
		if (child == null) {
			return new Element("null");
		} else if (!fieldtype.isPrimitive()) {
			Element reference = new Element("reference");
			if (map.containsKey(child)) {
				reference.setText(map.get(child).toString());
			} else {
				reference.setText(Integer.toString(map.size()));
				serialize(child, target, map);
			}
			return reference;
		} else {
			Element value = new Element("value");
			value.setText(child.toString());
			return value;
		}
	}

}