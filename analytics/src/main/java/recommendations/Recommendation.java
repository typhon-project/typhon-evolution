package recommendations;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Recommendation {
	
	private static int idCounter = 0;
	private int id;
	
	protected synchronized static int getNextId() {
		int res = idCounter;
		idCounter++;
		return res;
	}
	
	protected Recommendation() {
		this.id = getNextId();
	}
	
	
	public abstract String getEvolutionOperator();

	public abstract String getJSONContent();
	
	public abstract JSONObject getJSON();
	
	public abstract Element getHTMLElement(Document document, String radioName, boolean andRecommendation);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
