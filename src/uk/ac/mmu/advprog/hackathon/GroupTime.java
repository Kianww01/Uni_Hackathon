package uk.ac.mmu.advprog.hackathon;

/**
 * GroupTime POJO used for storing a signalID, dateTime and signalValue
 * @author Kian Wilson 19069770
 */
public class GroupTime
{
	private String signalID;
	private String dateTime;
	private String signalValue;
	
	/**
	 * Constructor for a GroupTime POJO
	 * @param signalID
	 * @param dateTime
	 * @param signalValue
	 */
	public GroupTime(String signalID, String dateTime, String signalValue) {
		this.signalID = signalID;
		this.dateTime = dateTime;
		this.signalValue = signalValue;
	}
	
	public String getSignalID()
	{
		return signalID;
	}

	public void setSignalID(String signalID)
	{
		this.signalID = signalID;
	}

	public String getDateTime()
	{
		return dateTime;
	}

	public void setDateTime(String dateTime)
	{
		this.dateTime = dateTime;
	}

	public String getSignalValue()
	{
		return signalValue;
	}

	public void setSignalValue(String signalValue)
	{
		this.signalValue = signalValue;
	}
}
