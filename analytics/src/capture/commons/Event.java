package capture.commons;

public abstract class Event {

	protected String eventId;
	protected String query;

	public Event() {

	}

	public Event(String eventId, String query) {

		this.eventId = eventId;
		this.query = query;
	}

	public String getId() {
		return eventId;
	}

	public String getQuery() {
		return query;
	}

	public void setId(String id) {
		this.eventId = id;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return "Event [id=" + eventId + ", query=" + query + "]";
	}

}
