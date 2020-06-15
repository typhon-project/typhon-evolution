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
	
	private Element getInformationLabel(Document document) {
		Element infoLabel = document.createElement("label");
		infoLabel.setAttribute("class", "info");
		infoLabel.setAttribute("title", getExplanation());
		infoLabel.appendChild(document.createTextNode(" "));
		return infoLabel;
	}

	private String getExplanation() {
		return "Performing joins between two entities stored in a different database can be time-consuming. Migrating one to the same database as the other one will speed up these joins.";
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
//			input.setAttribute("value", getChangeOperator());
//			Element label = document.createElement("label");
//			label.setAttribute("for", getId() + "");
//			label.appendChild(document.createTextNode(getHumanReadableDescription()));
//			res.appendChild(input);
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
//			label.appendChild(
//					document.createTextNode(getHumanReadableDescription()));
//
//			if (!andRecommendation) {
//				Element input = document.createElement("input");
//				input.setAttribute("type", "checkbox");
//				input.appendChild(
//						document.createTextNode(getHumanReadableDescription()));
//				label.appendChild(input);
//				
//				Element hiddenInput = document.createElement("input");
//				hiddenInput.setAttribute("class", "changeOperator");
//				hiddenInput.setAttribute("type", "hidden");
//				hiddenInput.setAttribute("value", getChangeOperator());
//				label.appendChild(hiddenInput);
//				
//			}
//
//		}
//
//		return res;
//	}

	private String getChangeOperator() {
		return "migrate " + entity.getName() + " to " + destDatabase.getName();
	}

	private String getHumanReadableDescription() {
		return "Migrating entity " + entity.getName() + " to database '" + destDatabase.getName() + "'";
	}

}
