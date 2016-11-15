package main;

import java.lang.reflect.*;
import java.util.*;

import static utilities.TextDisplay.*;

//Code adapted from assignment 2
public class Visualizer {
	public List<Class<?>> inspectedClasses;
	public Object topInstance;

	public Visualizer() {
		inspectedClasses = new ArrayList<Class<?>>();
	}

	public void visualize(Object origInstance, boolean recursive) {
		Class<?> objCls = origInstance.getClass();
		if (topInstance == null)
			topInstance = origInstance;
		int depth = 0;
		this.visualize(origInstance, objCls, recursive, depth);
	}

	public void visualize(Object origInstance, Class<?> objCls, boolean recursive, int depth) {
		if (objCls.isArray())
			this.visualizeArray(origInstance, recursive, depth);
		else if (objCls.isPrimitive())
			this.visualizePrimitive(origInstance, depth);
		else
			this.visualizeObject(origInstance, objCls, recursive, depth);
	}


	public void visualizeArray(Object origInstance, boolean recursive, int depth) {
		display("Array Object:", depth);
		display("Reference: " + origInstance, depth + 1);
		
		Class<?> componentType = origInstance.getClass().getComponentType();
		Class<?> objectType = componentType;
		display("Component Type: " + componentType.getName(), depth + 1);
		display();
		
		int arrayLength = Array.getLength(origInstance);
		display("Array size: " + arrayLength, depth + 1);
		display();
		
		for (int i = 0; i < arrayLength; i++) {
			display("Element Index: " + i, depth + 1);
			Object elemObj = Array.get(origInstance, i);
			System.out.println(componentType.isPrimitive());
			System.out.println((objectType.equals(Object.class) && elemObj != null));
			System.out.println(!objectType.equals(elemObj.getClass()));
			
			if (!objectType.isPrimitive() && ((objectType.equals(Object.class) && elemObj != null)
					|| !objectType.equals(elemObj.getClass())))
				objectType = elemObj.getClass(); 	
			if (elemObj != null) {
				if (elemObj.equals(topInstance)) {
					display("Circular reference to self. Halting recursion on this object", depth + 1);
					visualize(elemObj, objectType, false, depth + 1);
				} else
					visualize(elemObj, objectType, recursive, depth + 1);
			} else
				display("Element: Null", depth + 1);
			display();
		}
		display();
	}

	public void visualizeObject(Object origInstance, Class<?> objCls, boolean recursive, int depth) {
		List<Field> objsToInspect = new ArrayList<Field>();
		Class<?> superClass = objCls.getSuperclass();		
		inspectedClasses.add(objCls);
		display("Recursion: " + recursive, depth);
		display("Class Name: " + objCls.getName(), depth);
		display("Declaring Class: " + objCls.getDeclaringClass(), depth);
		display("Intermediate Super Class: " + superClass, depth);

		visualizeConstructors(objCls, depth);
		visualizeInterfaces(objCls, depth);
		visualizeFields(origInstance, objCls, objsToInspect, depth);
		visualizeMethods(objCls, depth);
		
		if (recursive) {
			visualizeFieldClasses(origInstance, objCls, objsToInspect, recursive, depth);

			if (superClass != null && !superClass.equals(Object.class)
					&& !inspectedClasses.contains(objCls.getSuperclass())) {
				display("SUPERCLASS OF " + objCls.getName(), depth);
				visualize(origInstance, superClass, recursive, depth + 1);
			}
		}
	}

	public void visualizePrimitive(Object obj, int depth) {
		try {
			Field valueField = obj.getClass().getDeclaredField("value");
			if (!Modifier.isPublic(valueField.getModifiers()))
				valueField.setAccessible(true);
			display("Primitive Value: " + valueField.get(obj).toString(), depth);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void visualizeConstructors(Class<?> objCls, int depth) {
		if (objCls.getDeclaredConstructors().length > 0)
			display("Constructors:", depth);
		else
			return;
		for (Constructor<?> c : objCls.getDeclaredConstructors()) {
			if(!Modifier.isPublic(c.getModifiers()))
				c.setAccessible(true);
			
			display("Name: " + c.getName(), depth + 1);
			display("Modifier: " + Modifier.toString(c.getModifiers()), depth + 1);
			
			for (Class<?> exception : c.getExceptionTypes())
				display("Exception thrown: " + exception.getName(), depth + 1);
			
			for (Class<?> paramTypes : c.getParameterTypes())
				display("Parameter types: " + paramTypes.getName(), depth + 1);
			
			display("Declaring Class: " + c.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
	}

	public void visualizeMethods(Class<?> objCls, int depth) {
		if (objCls.getDeclaredMethods().length > 0)
			display("Methods:", depth);
		else
			return;
		
		for (Method method : objCls.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers()))
				method.setAccessible(true);
			
			display("Name: " + method.getName(), depth + 1);
			display("Modifier: " + Modifier.toString(method.getModifiers()), depth + 1);
			display("Return Type: " + method.getReturnType(), depth + 1);
			
			for (Class<?> exception : method.getExceptionTypes())
				display("Exception thrown: " + exception.getName(), depth + 1);
			
			for (Class<?> paramTypes : method.getParameterTypes())
				display("Parameter types: " + paramTypes.getName(), depth + 1);
			
			display("Declaring Class: " + method.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
	}

	public void visualizeInterfaces(Class<?> objCls, int depth) {
		if (objCls.getInterfaces().length > 0)
			display("Interfaces:", depth);
		else
			return;
		
		for (Class<?> cls : objCls.getInterfaces()) {
			display(cls.getName(), depth + 1);
			display();
		}
		display();
	}

	public void visualizeFieldClasses(Object obj, Class<?> objCls, List<Field> objsToInspect, boolean recursive,
			int depth) {

		if (objsToInspect.size() > 0)
			display("FIELD CLASSES:", depth);
		else
			return;
		
		for (Field field : objsToInspect) {
			if (!Modifier.isPublic(field.getModifiers()))
				field.setAccessible(true);
			
			display("Field Class Name: " + field.getName(), depth + 1);
			try {
				Object fieldObj = field.get(obj);
				if (fieldObj == null)
					display("No value given", depth + 1);
				else {
					Visualizer newInspect = new Visualizer(); 
					newInspect.setTopInstance(topInstance);
					newInspect.visualize(fieldObj, fieldObj.getClass(), recursive, depth + 1);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			display();
		}
		display();
	}

	public void visualizeFields(Object obj, Class<?> objCls, List<Field> objsToInspect, int depth) {
		if (objCls.getDeclaredFields().length > 0)
			display("Fields:", depth);
		else
			return;
		for (Field field : objCls.getDeclaredFields()) {

			if (!field.getType().isPrimitive())
				objsToInspect.add(field);

			display("Name: " + field.getName(), depth + 1);
			if (!Modifier.isPublic(field.getModifiers()))
				field.setAccessible(true);

			try {
				display("Value: " + field.get(obj), depth + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			display("Modifier: " + Modifier.toString(field.getModifiers()), depth + 1);
			display("Type: " + field.getType().getName(), depth + 1);
			display("Declaring Class: " + field.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
	}

	/*public Object getTopInstance() {
		return topInstance;
	}*/

	public void setTopInstance(Object topInstance) {
		this.topInstance = topInstance;
	}
}