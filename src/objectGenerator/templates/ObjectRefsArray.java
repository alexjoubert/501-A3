package objectGenerator.templates;

public class ObjectRefsArray {

	public Object[] arrayObjectRefs;

	public ObjectRefsArray()
	{
		arrayObjectRefs = new Object[100];
	}
	
	public Object[] getArrayObjectRefs() {
		return arrayObjectRefs;
	}

	public void setArrayObjectRefs(Object[] arrayObjectRefs) {
		this.arrayObjectRefs = arrayObjectRefs;
	}
	
	public void setArrayValue(Object value, int index) {
		this.arrayObjectRefs[index] = value;
	}
}