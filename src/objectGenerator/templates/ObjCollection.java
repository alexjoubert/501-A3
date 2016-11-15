package objectGenerator.templates;

import java.util.ArrayList;
import java.util.List;

public class ObjCollection {
	private List<Object> listObjs;
	
	public ObjCollection(){
		listObjs = new ArrayList<Object>();
		listObjs.add(new Ints());
		listObjs.add(new PrimitiveArrays());
		listObjs.add(new ObjRefsArray());
		
	}
	
	public List<?> getListObjs() {
		return listObjs;
	}

	public void setListObjs(List<Object> listObjs) {
		this.listObjs = listObjs;
	}
	
}