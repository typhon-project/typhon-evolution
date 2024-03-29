package query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Insert implements Serializable {
	private String entityName;
	private List<Insert> children = new ArrayList<Insert>();

	public Insert(String entityName, List<Insert> children) {
		setEntityName(entityName);
		setChildren(children);
	}

	public Insert clone() {
		List<Insert> children = new ArrayList<Insert>();
		for(Insert i : this.children)
			children.add(i.clone());
		Insert res = new Insert(entityName, children);
		return res;
	}
	
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<Insert> getChildren() {
		return children;
	}

	public void setChildren(List<Insert> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		String res = entityName + "{";
		for (int i = 0; i < children.size(); i++) {
			if (i > 0)
				res += ", ";

			Insert child = children.get(i);
			res += child;
		}
		res += "}";
		return res;
	}
}
