package objectGenerator.templates;

public class ArraysPrimitive {
	public int[] arrayInt;
	
	public ArraysPrimitive()
	{
		arrayInt = new int[100];
	}
	
	public int[] getArrayInt() {
		return arrayInt;
	}

	public void setArrayInt(int[] arrayInt) {
		this.arrayInt = arrayInt;
	}
	
	public void setArrayValue(int value, int index) {
		this.arrayInt[index] = value;
	}
}