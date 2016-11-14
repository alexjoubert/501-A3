package objectGenerator.templates;

import java.util.ArrayList;
import java.util.List;

public class ObjectCollection {
	private List<Object> listObjs;
	
	public ObjectCollection(){
		listObjs = new ArrayList<Object>();
		listObjs.add(new Ints());
		listObjs.add(new PrimitiveArrays());
		listObjs.add(new ObjectRefsArray());
		
	}
	
	public List<?> getListObjs() {
		return listObjs;
	}

	public void setListObjs(List<Object> listObjs) {
		this.listObjs = listObjs;
	}
	
}