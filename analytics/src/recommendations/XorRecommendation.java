package recommendations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XorRecommendation extends Recommendation {
	private List<Recommendation> recommendations = new ArrayList<Recommendation>();

	public XorRecommendation(Recommendation... list) {
		super();
		for (Recommendation r : list)
			recommendations.add(r);
	}

	@Override
	public String getEvolutionOperator() {
		String res = "XOR {\n";
		for (Recommendation r : recommendations)
			res += "   " + r.getEvolutionOperator() + "\n";
		res += "}";
		return res;
	}

	@Override
	public String getJSONContent() {
		String res = "\"Xor\": [";
		int i = 0;
		for (Recommendation r : recommendations) {
			res += (i > 0) ? ",\n" : "\n";
			res += r.getJSONContent();
			i++;
		}
		res += "]";
		return res;
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		JSONArray array = new JSONArray();
		for (Recommendation r : recommendations)
			array.put(r.getJSON());
		res.put("Xor", array);
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
////			  <div>recommendations</div>
////			</div>
//
//			res = document.createElement("div");
////			res.setAttribute("class", "recommendationDiv");
//			
//			Element input = document.createElement("input");
//			input.setAttribute("type", "radio");
//			input.setAttribute("id", getId() + "");
//			input.setAttribute("name", radioName);
//			input.setAttribute("value", getId() + "");
//			
//			Element label = document.createElement("label");
//			label.appendChild(document.createTextNode("Exclusive options"));
//
//			Element subDiv = document.createElement("div");
//			subDiv.setAttribute("class", "recommendationDiv");
//
//			String radioName2 = getId() + "";
//
//			for (Recommendation r : recommendations)
//				subDiv.appendChild(r.getHTMLElement(document, radioName2, false));
//
//			res.appendChild(input);
//			res.appendChild(label);
//			res.appendChild(subDiv);
//
//		} else {
////			<div><input type="checkbox"><div>recommendations</div></div>
//			res = document.createElement("div");
////			res.setAttribute("class", "recommendationDiv");
//
//			if (!andRecommendation) {
//				Element input = document.createElement("input");
//				input.setAttribute("type", "checkbox");
//				res.appendChild(input);
//			} 
//			
//			Element label = document.createElement("label");
//			label.appendChild(document.createTextNode("Exclusive options"));
//			res.appendChild(label);
//
//			Element subDiv = document.createElement("div");
//			subDiv.setAttribute("class", "recommendationDiv");
//
//			String radioName2 = getId() + "";
//
//			for (Recommendation r : recommendations)
//				subDiv.appendChild(r.getHTMLElement(document, radioName2, false));
//			res.appendChild(subDiv);
//
//		}
//
//		return res;
//	}
	
	
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
		
		if(radioName != null) {
			input.setAttribute("type", "radio");
			input.setAttribute("id", getId() + "");
			input.setAttribute("name", radioName);
			label.setAttribute("for", getId() + "");
		} else
			if(andRecommendation) {
				input.setAttribute("type", "hidden");
			} else
				input.setAttribute("type", "checkbox");
		
		
		Element subDiv = document.createElement("div");
		subDiv.setAttribute("class", "recommendationDiv");

		String radioName2 = getId() + "";

		for (Recommendation r : recommendations)
			subDiv.appendChild(r.getHTMLElement(document, radioName2, false));
		res.appendChild(subDiv);
		
		
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
		return "These options proposed below are exclusive. That means you can only select one at most.";
	}

	private String getChangeOperator() {
		return "";
	}

	private String getHumanReadableDescription() {
		return "Exclusive options";
	}
	

}
