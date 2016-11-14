package objectGenerator.templates;

public class PrimitiveArrays {
	public int[] arrayInt;
	
	public PrimitiveArrays()
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