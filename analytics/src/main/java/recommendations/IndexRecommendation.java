package recommendations;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IndexRecommendation extends Recommendation {

	private String dbName;
	private String tableName;
	private String entityName;
	private String attribute;

	public IndexRecommendation(String dbName, String tableName, String entityName, String attribute) {
		super();
		this.dbName = dbName;
		this.tableName = tableName;
		this.entityName = entityName;
		this.attribute = attribute;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String getEvolutionOperator() {
		return "Adding index to " + tableName + "[" + attribute + "]";
	}

	@Override
	public String getJSONContent() {
		return "\"addIndex\": { \"changeOperator\": \"AddIndex {table '" + tableName + "' attributes ('" + attribute
				+ "')}\"}";
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		JSONObject o = new JSONObject();
		o.put("changeOperator", "AddIndex {table '" + tableName + "' attributes ('" + attribute + "'}");
		res.put("addIndex", o);
		return res;
	}

	@Override
	public Element getHTMLElement(Document document, String radioName, boolean andRecommendation) {
		Element res = document.createElement("div");

		Element input = document.createElement("input");
		input.setAttribute("value", getChangeOperator());
		res.appendChild(input);
		Element label = document.createElement("label");
		res.appendChild(label);

		label.appendChild(document.createTextNode(getHumanReadableDescription()));
		label.appendChild(getInformationLabel(document));

		if (radioName != null) {
			input.setAttribute("type", "radio");
			input.setAttribute("id", getId() + "");
			input.setAttribute("name", radioName);
			label.setAttribute("for", getId() + "");
		} else if (andRecommendation) {
			input.setAttribute("type", "hidden");
		} else
			input.setAttribute("type", "checkbox");

		return res;
	}

//	@Override
//	public Element getHTMLElement(Document document, String radioName, boolean andRecommendation) {
//		Element res = null;
//		if (radioName != null) {
//			// XOR recommendation
////			<div>
////			  <input type="radio" id="huey" name="radioName" value="huey"
////			         checked>
////			  <label for="huey">Huey</label>
////			</div>
//
//			res = document.createElement("div");
////			res.setAttribute("class", "recommendationDiv");
//			Element input = document.createElement("input");
//			input.setAttribute("type", "radio");
//			input.setAttribute("id", getId() + "");
//			input.setAttribute("name", radioName);
//			input.setAttribute("value", getId() + "");
//			Element label = document.createElement("label");
//			label.setAttribute("for", getId() + "");
//			label.appendChild(
//					document.createTextNode(getHumanReadableDescription()));
//			res.appendChild(input);
//			
//			
//			Element hiddenInput = document.createElement("input");
//			hiddenInput.setAttribute("class", "changeOperator");
//			hiddenInput.setAttribute("type", "hidden");
//			hiddenInput.setAttribute("value", getChangeOperator());
//			res.appendChild(hiddenInput);
//			
//			res.appendChild(label);
//
//		} else {
////			<div><label><input type="checkbox"><span>some text</span></label></div>
//			res = document.createElement("div");
////			res.setAttribute("class", "recommendationDiv");
//
//			Element label = document.createElement("label");
//			res.appendChild(label);
//
//			if (!andRecommendation) {
//				Element input = document.createElement("input");
//				input.setAttribute("type", "checkbox");
//				input.appendChild(document
//						.createTextNode(getHumanReadableDescription()));
//				
//				Element hiddenInput = document.createElement("input");
//				hiddenInput.setAttribute("class", "changeOperator");
//				hiddenInput.setAttribute("type", "hidden");
//				hiddenInput.setAttribute("value", getChangeOperator());
//				
//				label.appendChild(input);
//				label.appendChild(hiddenInput);
//			}
//
//		}
//
//		return res;
//	}

	private Element getInformationLabel(Document document) {
		Element infoLabel = document.createElement("label");
		infoLabel.setAttribute("class", "info");
		infoLabel.setAttribute("title", getExplanation());
		infoLabel.appendChild(document.createTextNode(" "));
		return infoLabel;
	}

	private String getExplanation() {
		return "Adding an index will improve the search performance on this attribute";
	}

	private String getHumanReadableDescription() {
		return "Adding an index to " + tableName + "[" + attribute + "]";
	}

	private String getChangeOperator() {
		return "AddIndex {table '" + dbName + "." + tableName + "' attributes ('" + entityName + "." + attribute + "') }";
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}
