package capture.mains;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Join {
	private String entityName1;
	private List<String> attributes1;
	private String entityName2;
	private List<String> attributes2;

	private List<Join> implicitJoins1;
	private AttributeSelector implicitAttributeSelector1;
	private List<Join> implicitJoins2;
	private AttributeSelector implicitAttributeSelector2;

	public List<Join> getAllJoins() {
		List<Join> res = new ArrayList<Join>();

		if (!containsImplicitJoins1() && !containsImplicitJoins2())
			res.add(this);

		if (containsImplicitJoins1() && !containsImplicitJoins2()) {
			res.addAll(this.getImplicitJoins1());
			String lastEntityName = this.getImplicitJoins1().get(this.getImplicitJoins1().size() - 1).getEntityName2();
			if (!lastEntityName.equals(entityName2)) {
				// ex: from user u, product p select u, p where u.orders == p
				// there is a join between order and product
				Join j = new Join(lastEntityName, new ArrayList<String>(), entityName2, new ArrayList<String>());
				res.add(j);
			}

		}

		if (!containsImplicitJoins1() && containsImplicitJoins2()) {
			res.addAll(getImplicitJoins2());
			String lastEntityName = this.getImplicitJoins2().get(this.getImplicitJoins2().size() - 1).getEntityName2();
			if (!lastEntityName.equals(entityName1)) {
				// ex: from user u, product p select u, p where p == u.orders
				// there is a join between order and product
				Join j = new Join(lastEntityName, new ArrayList<String>(), entityName1, new ArrayList<String>());
				res.add(j);
			}
		}

		if (containsImplicitJoins1() && containsImplicitJoins2()) {
			// ex: where p.orders == u.orders
			res.addAll(implicitJoins1);
			res.addAll(implicitJoins2);
			String lastEntityName1 = this.getImplicitJoins1().get(this.getImplicitJoins1().size() - 1).getEntityName2();
			String lastEntityName2 = this.getImplicitJoins2().get(this.getImplicitJoins2().size() - 1).getEntityName2();
			Join j = new Join(lastEntityName1, new ArrayList<String>(), lastEntityName2, new ArrayList<String>());
			res.add(j);
		}

		return res;
	}

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
