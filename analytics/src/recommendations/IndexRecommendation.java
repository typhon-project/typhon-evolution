package recommendations;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IndexRecommendation extends Recommendation {

	private String tableName;
	private String attribute;

	public IndexRecommendation(String tableName, String attribute) {
		super();
		this.tableName = tableName;
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
		Element res = null;
		if (radioName != null) {
			// XOR recommendation
//			<div>
//			  <input type="radio" id="huey" name="radioName" value="huey"
//			         checked>
//			  <label for="huey">Huey</label>
//			</div>

			res = document.createElement("div");
//			res.setAttribute("class", "recommendationDiv");
			Element input = document.createElement("input");
			input.setAttribute("type", "radio");
			input.setAttribute("id", getId() + "");
			input.setAttribute("name", radioName);
			input.setAttribute("value", getId() + "");
			Element label = document.createElement("label");
			label.setAttribute("for", getId() + "");
			label.appendChild(
					document.createTextNode("AddIndex {table '" + tableName + "' attributes ('" + attribute + "'}"));
			res.appendChild(input);
			res.appendChild(label);

		} else {
//			<div><label><input type="checkbox"><span>some text</span></label></div>
			res = document.createElement("div");
//			res.setAttribute("class", "recommendationDiv");

			Element label = document.createElement("label");
			res.appendChild(label);

			if (!andRecommendation) {
				Element input = document.createElement("input");
				input.setAttribute("type", "checkbox");
				input.appendChild(document
						.createTextNode("AddIndex {table '" + tableName + "' attributes ('" + attribute + "'}"));
				label.appendChild(input);
			}

		}

		return res;
	}

}
