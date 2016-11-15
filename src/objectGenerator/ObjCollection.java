package objectGenerator;

import java.util.Collection;
import java.util.HashSet;

public class ObjCollection {
	private Collection<Object> colObjs;
	
	public ObjCollection(){
		colObjs = new HashSet<Object>();
		colObjs.add(new Ints());
		colObjs.add(new PrimitiveArrays());
		colObjs.add(new ObjRefsArray());
		
	}
	
	public Collection<?> getColObjs() {
		return colObjs;
	}

	public void setListObjs(Collection<Object> colObjs) {
		this.colObjs = colObjs;
	}
	
}