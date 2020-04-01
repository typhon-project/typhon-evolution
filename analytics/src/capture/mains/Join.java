package capture.mains;

import java.util.List;

public class Join {
	private String entityName1;
	private List<String> attributes1;
	private String entityName2;
	private List<String> attributes2;

	private List<Join> implicitJoins1;
	private AttributeSelector implicitAttributeSelector1;
	private List<Join> implicitJoins2;
	private AttributeSelector implicitAttributeSelector2;

	public boolean containsImplicitJoins1() {
		return implicitJoins1 != null && implicitJoins1.size() > 0;
	}

	public boolean containsImplicitJoins2() {
		return implicitJoins2 != null && implicitJoins2.size() > 0;
	}

	public Join(String entityName1, List<String> attributes1, String entityName2, List<String> attributes2) {
		setEntityName1(entityName1);
		setEntityName2(entityName2);
		setAttributes1(attributes1);
		setAttributes2(attributes2);
	}

	public String getEntityName1() {
		return entityName1;
	}

	public void setEntityName1(String entityName1) {
		this.entityName1 = entityName1;
	}

	public List<String> getAttributes1() {
		return attributes1;
	}

	public void setAttributes1(List<String> attributes1) {
		this.attributes1 = attributes1;
	}

	public String getEntityName2() {
		return entityName2;
	}

	public void setEntityName2(String entityName2) {
		this.entityName2 = entityName2;
	}

	public List<String> getAttributes2() {
		return attributes2;
	}

	public void setAttributes2(List<String> attributes2) {
		this.attributes2 = attributes2;
	}

	public List<Join> getImplicitJoins1() {
		return implicitJoins1;
	}

	public void setImplicitJoins1(List<Join> implicitJoins1) {
		this.implicitJoins1 = implicitJoins1;
	}

	public List<Join> getImplicitJoins2() {
		return implicitJoins2;
	}

	public void setImplicitJoins2(List<Join> implicitJoins2) {
		this.implicitJoins2 = implicitJoins2;
	}

	public AttributeSelector getImplicitAttributeSelector1() {
		return implicitAttributeSelector1;
	}

	public void setImplicitAttributeSelector1(AttributeSelector implicitAttributeSelector1) {
		this.implicitAttributeSelector1 = implicitAttributeSelector1;
	}

	public AttributeSelector getImplicitAttributeSelector2() {
		return implicitAttributeSelector2;
	}

	public void setImplicitAttributeSelector2(AttributeSelector implicitAttributeSelector2) {
		this.implicitAttributeSelector2 = implicitAttributeSelector2;
	}
}
