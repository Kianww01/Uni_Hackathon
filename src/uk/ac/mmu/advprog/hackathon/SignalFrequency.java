package uk.ac.mmu.advprog.hackathon;

/**
 * SignalFrequency POJO used for storing a signal value and frequency
 * @author Kian Wilson 19069770
 */
public class SignalFrequency
{
	private String signal;
	
	/**
	 * Constructor for a SignalFrequency POJO
	 * @param signal 
	 * @param frequency 
	 */
	public SignalFrequency(String signal, String frequency) {
		this.signal = signal;
		this.frequency = frequency;
	}
	
	public String getSignal()
	{
		return signal;
	}

	public void setSignal(String signal)
	{
		this.signal = signal;
	}

	public String getFrequency()
	{
		return frequency;
	}

	public void setFrequency(String frequency)
	{
		this.frequency = frequency;
	}

	private String frequency;
}
