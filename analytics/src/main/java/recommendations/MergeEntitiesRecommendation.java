package recommendations;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import typhonml.Entity;
import typhonml.Relation;

public class MergeEntitiesRecommendation extends Recommendation {
	private Entity absorbingEntity;
	private Entity absorbedEntity;
	private Relation relation;

	public MergeEntitiesRecommendation(Entity absorbingEntity, Entity absorbedEntity, Relation relation) {
		super();
		this.absorbedEntity = absorbedEntity;
		this.absorbingEntity = absorbingEntity;
		this.relation = relation;
	}

	@Override
	public String getEvolutionOperator() {
		return "MERGE ENTITIES " + absorbingEntity.getName() + " " + absorbedEntity.getName();
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	@Override
	public String getJSONContent() {
		Entity srcEntity = (relation.getType() == absorbingEntity) ? absorbedEntity : absorbingEntity;

		return "\"mergeEntities\": { \"changeOperator\": \"merge entities " + absorbingEntity.getName() + " "
				+ absorbedEntity.getName() + " '" + srcEntity.getName() + "." + relation.getName() + "'\"}";
	}

	@Override
	public JSONObject getJSON() {
		Entity srcEntity = (relation.getType() == absorbingEntity) ? absorbedEntity : absorbingEntity;
		JSONObject res = new JSONObject();
		JSONObject o = new JSONObject();
		o.put("changeOperator", "merge entities " + absorbingEntity.getName() + " " + absorbedEntity.getName() + " '"
				+ srcEntity.getName() + "." + relation.getName());
		res.put("mergeEntities", o);
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

//	@Override
//	public Element getHTMLElement(Document document, String radioName, boolean andRecommendation) {
//		Entity srcEntity = (relation.getType() == absorbingEntity) ? absorbedEntity : absorbingEntity;
//
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
//			label.appendChild(document.createTextNode(getHumanReadableDescription()));
//			res.appendChild(input);
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
//				input.appendChild(document.createTextNode(getHumanReadableDescription()));
//
//				Element hiddenInput = document.createElement("input");
//				hiddenInput.setAttribute("class", "changeOperator");
//				hiddenInput.setAttribute("type", "hidden");
//				hiddenInput.setAttribute("value", getChangeOperator());
//
//				label.appendChild(input);
//				label.appendChild(hiddenInput);
//			}
//		}
//
//		return res;
//	}

	private String getExplanation() {
		return "Performing joins between entities can be time-consuming. Merging these entities will make joins obsolete since entities data will be stored in a single one.";
	}

	private String getChangeOperator() {
		Entity srcEntity = (relation.getType() == absorbingEntity) ? absorbedEntity : absorbingEntity;
		return "merge entities " + absorbingEntity.getName() + " " + absorbedEntity.getName() + " as '"
				+ srcEntity.getName() + "." + relation.getName() + "'";
	}

	private String getHumanReadableDescription() {
		Entity srcEntity = (relation.getType() == absorbingEntity) ? absorbedEntity : absorbingEntity;
		return "Merging entity " + absorbedEntity.getName() + " into " + absorbingEntity.getName() + " via relation '"
				+ srcEntity.getName() + "." + relation.getName() + "'";
	}

}
