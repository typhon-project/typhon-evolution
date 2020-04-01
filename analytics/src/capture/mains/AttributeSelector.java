package capture.mains;

import java.util.List;

public class AttributeSelector {
	private String entityName;
	private List<String> attributes;

	private List<Join> implicitJoins;
	private AttributeSelector implicitSel;

	public AttributeSelector(String entityName, List<String> attributes) {
		this.entityName = entityName;
		this.attributes = attributes;
	}

	public boolean containsImplicitJoins() {
		return implicitJoins != null && implicitJoins.size() > 0;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public List<Join> getImplicitJoins() {
		return implicitJoins;
	}

	public void setImplicitJoins(List<Join> implicitJoins) {
		this.implicitJoins = implicitJoins;
	}

	public AttributeSelector getImplicitSel() {
		return implicitSel;
	}

	public void setImplicitSel(AttributeSelector implicitSel) {
		this.implicitSel = implicitSel;
	}

}
