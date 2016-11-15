package objectGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import main.Driver;
import utilities.TextDisplay;
import utilities.General;

public class ObjGenerator {

	private List<String> ObjectMenu;
	private List<String> ObjectGenerationMenu;
	private Scanner userInput = new Scanner(System.in);
	private List<Object> objList;

	public ObjGenerator() {

		ObjectMenu = General.readToList("src/objectGenerator/assets/ObjectMenu.txt");
		ObjectGenerationMenu = General.readToList("src/objectGenerator/assets/ObjectGenerationMenu.txt");
		objList = new ArrayList<Object>();
	}

	public List<Object> getObjList() {
		return objList;
	}

	public void objGeneratorMenu() {
		switch (Driver.menuSelect(userInput, ObjectGenerationMenu)) {
		case (1):
			Ints ints = new Ints();
			objList.add(ints);
			setFields(ints);
			break;
		case (2):
			SimpleObjs simpleObjs = new SimpleObjs();
			objList.add(simpleObjs);
			setFields(simpleObjs);
			break;
		case (3):
			PrimitiveArrays primitiveArrays = new PrimitiveArrays();
			objList.add(primitiveArrays);
			setFields(primitiveArrays);
			break;

		case (4):
			ObjRefsArray objRefsArray = new ObjRefsArray();
			objList.add(objRefsArray);
			setFields(objRefsArray);
			break;

		case (5):
			ObjCollection objCollection = new ObjCollection();
			objList.add(objCollection);
			break;

		case (6):
			return;
		}
	}

	public void setFields(Object obj) {
		TextDisplay.display("Field names");
		List<String> fieldList = getFieldList(obj);
		fieldList.add((fieldList.size() + 1) + ") Exit");
		int selection = -1;
		while (selection != (fieldList.size() + 1)) {
			selection = Driver.menuSelect(userInput, fieldList);
			if (selection > 0 && selection <= fieldList.size() - 1)
				setField(obj, selection - 1);
			else
				break;
		}
		objGeneratorMenu();
	}

	public void setField(Object obj, int fieldIndex) {
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];
		Class<?> fieldType = field.getType();
		TextDisplay.display("You have selected field: " + field.getName());
		TextDisplay.display("The type is: " + fieldType.getName());
		if (fieldType.isArray()) {
			TextDisplay.display("Give index of array element to update: ");
			int arrayIndex = Driver.getNextInt(userInput);
			setArrayElements(obj, fieldIndex, arrayIndex);
		} else if (fieldType.equals(int.class)) {
			setIntField(obj, fieldIndex);
		} else if (fieldType.equals(Object.class)) {
			TextDisplay.display("Select what object to choose:");
			switch (Driver.menuSelect(userInput, ObjectMenu)) {
			case (1):
				objGeneratorMenu();
				break;
			case (2):
				setIntField(obj, fieldIndex);
				break;
			case (3):
				Object value = objList.get(Driver.menuSelect(userInput, createdObjects()) - 1);
				setObjField(obj, fieldIndex, value);
				break;
			}
		}
	}

	public void setArrayElements(Object obj, int fieldIndex, int arrayIndex) {
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];
		Class<?> fieldType = field.getType();
		if (fieldType.isArray()) {
			Method setArrayValue = null;

			Class<?> componentType = fieldType.getComponentType();
			if (componentType.isPrimitive()) {
				try {
					setArrayValue = obj.getClass().getMethod("setArrayValue", int.class, int.class);
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}

				int input = 0;
				TextDisplay.display("This is a primitive array, please enter an int value for the array");
				input = Driver.getNextInt(userInput);

				try {
					setArrayValue.invoke(obj, input, arrayIndex);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} else {
				try {
					setArrayValue = obj.getClass().getMethod("setArrayValue", Object.class, int.class);
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}
				TextDisplay.display("Select one of objects from list to assign to array index");
				Object value = objList.get(Driver.menuSelect(userInput, createdObjects()) - 1);

				try {
					setArrayValue.invoke(obj, value, arrayIndex);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		} else
			throw new RuntimeException("Field is not array type!");
	}

	public void setObjField(Object obj, int fieldIndex, Object value) {
		TextDisplay.display("Enter an int to set the field:");
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];

		field.setAccessible(true);

		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		TextDisplay.display("Set field: " + field.getName() + " to " + value);
	}

	public void setIntField(Object obj, int fieldIndex) {
		TextDisplay.display("Enter an int to set the field:");
		int input = 0;
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];
		Class<?> fieldType = field.getType();
		if (!fieldType.equals(int.class) && !fieldType.equals(Object.class))
			throw new RuntimeException("Can't set non int field to int");
		try {
			input = userInput.nextInt();
		} catch (InputMismatchException e) {
			TextDisplay.display("Invalid selection");
			userInput.next();
			setField(obj, fieldIndex);
		}
		field.setAccessible(true);

		try {
			field.set(obj, input);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		TextDisplay.display("Set field: " + field.getName() + " to " + input);
	}

	public List<String> createdObjects() {
		List<String> createdObjectClassNames = new ArrayList<String>();
		int i = 1;
		for (Object obj : objList)
			createdObjectClassNames.add(i++ + ") " + obj.toString());
		return createdObjectClassNames;
	}

	public List<String> getFieldList(Object obj) {
		List<String> fieldList = new ArrayList<String>();
		int i = 0;
		for (Field field : obj.getClass().getDeclaredFields())
			fieldList.add((++i) + ") " + field.getName());
		return fieldList;
	}

}