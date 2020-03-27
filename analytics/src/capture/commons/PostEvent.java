package capture.commons;

import java.util.Date;

public class PostEvent extends Event {
	private Boolean success;
	private Date startTime;
	private Date endTime;
	private String resultSet;

	public String getResultSet() {
		return resultSet;
	}

	public void setResultSet(String resultSet) {
		this.resultSet = resultSet;
	}

	public PostEvent() {
		super();
	}

	public PostEvent(String id, String query, Boolean success, Date startTime,
			Date endTime) {
		super(id, query);
		this.success = success;
		this.startTime = startTime;
		this.endTime = endTime;

	}
	
	public PostEvent(String id, String query, Boolean success, Date startTime,
			Date endTime, String resultSet) {
		super(id, query);
		this.success = success;
		this.startTime = startTime;
		this.endTime = endTime;
		this.resultSet = resultSet;
	}

//	public PostEvent(String id, String query, Boolean success, Date startTime,
//			Date endTime, PreEvent preEvent, DMLCommand dmlCommand) {
//		super(id, query);
//		this.success = success;
//		this.startTime = startTime;
//		this.endTime = endTime;
//		this.preEvent = preEvent;
//		this.dmlCommand = dmlCommand;
//	}

	public Boolean getSuccess() {
		return success;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	@Override
	public String toString() {
		return "PostEvent [success=" + success + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", id="
				+ eventId + ", query=" + query + ", resultSet=" + resultSet +"]";
	}

	// @Override
	// public String toString() {
	// return "PostEvent [success=" + success + ", startTime=" + startTime
	// + ", endTime=" + endTime + ", preEvent=" + preEvent
	// + ", dmlCommand=" + dmlCommand + ", id=" + id + ", query="
	// + query + "]";
	// }

}
