package capture.mains;

import java.util.ArrayList;
import java.util.List;

import model.TyphonModel;

public class Query {
	private TyphonModel model = null;

	private String originalQuery;
	private String normalizedQuery;
	private String displayableQuery;
	private String queryType;

	private List<String> mainEntities = new ArrayList<String>();

	private List<Join> joins = new ArrayList<Join>();
	private List<AttributeSelector> attributeSelectors = new ArrayList<AttributeSelector>();
	private List<Insert> inserts = new ArrayList<Insert>();

	public TyphonModel getModel() {
		if (model == null) {
			model = TyphonModel.getCurrentModel();
		}

		return model;
	}

	public void print() {
		QueryParsing.logger.debug("Original query: " + originalQuery);
		QueryParsing.logger.debug("Normalized query: " + normalizedQuery);
		QueryParsing.logger.debug("Displayable query: " + displayableQuery);
		QueryParsing.logger.debug("Query type: " + queryType);
		QueryParsing.logger.debug("*****************************************");
		QueryParsing.logger.debug("Main entities: " + mainEntities);
		QueryParsing.logger.debug("*****************************************");
		if (joins.size() > 0) {
			QueryParsing.logger.debug("Joins between entities: ");
			for (Join j : joins) {
				QueryParsing.logger.debug("   " + j.getEntityName1() + j.getAttributes1() + " AND " + j.getEntityName2()
						+ j.getAttributes2());
				if (j.containsImplicitJoins1()) {
					QueryParsing.logger.debug("Implicit joins in first part: ");
					for (Join j2 : j.getImplicitJoins1())
						QueryParsing.logger.debug("   -> " + j2.getEntityName1() + j2.getAttributes1() + " AND "
								+ j2.getEntityName2() + j2.getAttributes2());

					if (j.getImplicitAttributeSelector1() != null)
						QueryParsing.logger.debug("   -> " + j.getImplicitAttributeSelector1().getEntityName()
								+ j.getImplicitAttributeSelector1().getAttributes());

				}

				if (j.containsImplicitJoins2()) {
					QueryParsing.logger.debug("Implicit joins in snd part: ");
					for (Join j2 : j.getImplicitJoins2())
						QueryParsing.logger.debug("   -> " + j2.getEntityName1() + j2.getAttributes1() + " AND "
								+ j2.getEntityName2() + j2.getAttributes2());

					if (j.getImplicitAttributeSelector2() != null)
						QueryParsing.logger.debug("   -> " + j.getImplicitAttributeSelector2().getEntityName()
								+ j.getImplicitAttributeSelector2().getAttributes());
				}
			}

			QueryParsing.logger.debug("*****************************************");
		}

		if (attributeSelectors.size() > 0) {
			QueryParsing.logger.debug("Attribute selectors:");
			for (AttributeSelector c : attributeSelectors) {
				QueryParsing.logger.debug("   " + c.getEntityName() + c.getAttributes()
						+ (c.containsImplicitJoins() ? " contains implicit joins:" : ""));
				if (c.containsImplicitJoins()) {
					for (Join j : c.getImplicitJoins())
						QueryParsing.logger.debug("   -> " + j.getEntityName1() + j.getAttributes1() + " AND "
								+ j.getEntityName2() + j.getAttributes2());
					QueryParsing.logger
							.debug("   -> " + c.getImplicitSel().getEntityName() + c.getImplicitSel().getAttributes());
				}
			}
			QueryParsing.logger.debug("*****************************************");
		}

		if (inserts.size() > 0) {
			QueryParsing.logger.debug("Inserts: " + inserts);
		}

	}

	public String getOriginalQuery() {
		return originalQuery;
	}

	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}

	public String getNormalizedQuery() {
		return normalizedQuery;
	}

	public void setNormalizedQuery(String normalizedQuery) {
		this.normalizedQuery = normalizedQuery;
	}

	public String getDisplayableQuery() {
		return displayableQuery;
	}

	public void setDisplayableQuery(String displayableQuery) {
		this.displayableQuery = displayableQuery;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public void addJoin(Join join) {
		QueryParsing.analyzeJoin(join, getModel());
		joins.add(join);
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}

	public void addAttributeSelector(AttributeSelector sel) {
		QueryParsing.analyzeAtttributeSelector(sel, getModel());
		attributeSelectors.add(sel);
	}

	public List<AttributeSelector> getAttributeSelectors() {
		return attributeSelectors;
	}

	public void setAttributeSelectors(List<AttributeSelector> attributeSelectors) {
		this.attributeSelectors = attributeSelectors;
	}

	public List<Insert> getInserts() {
		return inserts;
	}

	public void setInserts(List<Insert> inserts) {
		this.inserts = inserts;
	}

	public List<String> getMainEntities() {
		return mainEntities;
	}

	public void setMainEntities(List<String> mainEntities) {
		this.mainEntities = mainEntities;
	}
}
