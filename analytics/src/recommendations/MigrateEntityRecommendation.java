package recommendations;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import typhonml.Database;
import typhonml.Entity;

public class MigrateEntityRecommendation extends Recommendation {
	private Entity entity;
	private Database destDatabase;

	public MigrateEntityRecommendation(Entity ent, Database db) {
		super();
		this.entity = ent;
		this.destDatabase = db;
	}

	@Override
	public String getEvolutionOperator() {
		return "MIGRATE " + entity.getName() + " INTO DB " + destDatabase.getName();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Database getDestDatabase() {
		return destDatabase;
	}

	public void setDestDatabase(Database destDatabase) {
		this.destDatabase = destDatabase;
	}

	@Override
	public String getJSONContent() {

		return "\"migrateEntity\": { \"changeOperator\": \"migrate " + entity.getName() + " to "
				+ destDatabase.getName() + "\"}";
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		JSONObject o = new JSONObject();
		o.put("changeOperator", "migrate " + entity.getName() + " to " + destDatabase.getName());
		res.put("migrateEntity", o);
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
			label.appendChild(document.createTextNode("migrate " + entity.getName() + " to " + destDatabase.getName()));
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
				input.appendChild(
						document.createTextNode("migrate " + entity.getName() + " to " + destDatabase.getName()));
				label.appendChild(input);
			}

		}

		return res;
	}

}
