package objectGenerator.templates;

public class ObjRefsArray {

	public Object[] arrayObjectRefs;

	public ObjRefsArray()
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