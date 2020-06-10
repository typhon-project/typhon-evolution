package query;

import java.util.ArrayList;
import java.util.List;

import capture.mains.AttributeSelector;
import model.TyphonModel;
import typhonml.Entity;
import typhonml.Model;
import typhonml.Relation;

public class WellFormedJoin {

	private Entity entity1;
	// attribute belonging to entity1
	private Relation attributeRel;

	private Entity entity2;

	public static List<WellFormedJoin> extractWellFormedJoins(Query q) {
		List<WellFormedJoin> res = new ArrayList<WellFormedJoin>();
		if (q != null && q.getModel() != null) {
			for (Join j : q.getJoins())
				res.addAll(extractWellFormedJoins(q.getModel(), j));

			for (AttributeSelector sel : q.getAttributeSelectors())
				res.addAll(extractWellFormedJoins(q.getModel(), sel));
		}

		return res;
	}

	private static List<WellFormedJoin> extractWellFormedJoins(TyphonModel model, Join join) {
		List<WellFormedJoin> res = new ArrayList<WellFormedJoin>();

		if (join != null) {
			if (join.containsImplicitJoins1()) {
				for (Join j : join.getImplicitJoins1()) {
					Entity e1 = model.getEntityTypeFromName(j.getEntityName1());
					Relation rel = model.getRelationFromNameInEntity(j.getAttributes1().get(0), e1);
					Entity e2 = model.getEntityTypeFromName(j.getEntityName2());
					if (e1 != null && rel != null && e2 != null)
						res.add(new WellFormedJoin(e1, rel, e2));
				}
			}

			if (join.containsImplicitJoins2()) {
				for (Join j : join.getImplicitJoins2()) {
					Entity e1 = model.getEntityTypeFromName(j.getEntityName1());
					Relation rel = model.getRelationFromNameInEntity(j.getAttributes1().get(0), e1);
					Entity e2 = model.getEntityTypeFromName(j.getEntityName2());
					if (e1 != null && rel != null && e2 != null)
						res.add(new WellFormedJoin(e1, rel, e2));
				}
			}
		}

		return res;
	}

	private static List<WellFormedJoin> extractWellFormedJoins(TyphonModel model, AttributeSelector sel) {
		List<WellFormedJoin> res = new ArrayList<WellFormedJoin>();

		if (sel != null) {
			if (sel.containsImplicitJoins()) {
				for (Join j : sel.getImplicitJoins()) {
					Entity e1 = model.getEntityTypeFromName(j.getEntityName1());
					Relation rel = model.getRelationFromNameInEntity(j.getAttributes1().get(0), e1);
					Entity e2 = model.getEntityTypeFromName(j.getEntityName2());
					if (e1 != null && rel != null && e2 != null)
						res.add(new WellFormedJoin(e1, rel, e2));
				}
			}
		}

		return res;
	}

	public WellFormedJoin(Entity entity1, Relation attributeRel, Entity entity2) {
		this.entity1 = entity1;
		this.attributeRel = attributeRel;
		this.entity2 = entity2;
	}

	public Entity getEntity1() {
		return entity1;
	}

	public void setEntity1(Entity entity1) {
		this.entity1 = entity1;
	}

	public Relation getAttributeRel() {
		return attributeRel;
	}

	public void setAttributeRel(Relation attributeRel) {
		this.attributeRel = attributeRel;
	}

	public Entity getEntity2() {
		return entity2;
	}

	public void setEntity2(Entity entity2) {
		this.entity2 = entity2;
	}

}
