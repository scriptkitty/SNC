package unikl.disco.calculator.symbolic_math;

import java.io.Serializable;

/**
 *
 * @author sebi
 */
public class Hoelder implements Serializable {

	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2169450349608663397L;
	private int hoelder_id;
	private double p_value;
	private double q_value;
	
	//Constructors
	
	/**
	 * Creates a dummy Hoelder
	 * @param hoelder_id
	 */
	public Hoelder(int hoelder_id){
		this.hoelder_id = hoelder_id;
	}
	
    /**
     *
     * @param hoelder_id
     * @param p_value
     */
    public Hoelder(int hoelder_id, double p_value){
		this.hoelder_id = hoelder_id;
		this.p_value = p_value;
		this.q_value = 1/(1-1/p_value);
	}
	
    /**
     *
     * @param hoelder_id
     * @param value
     * @param p_value
     */
    public Hoelder(int hoelder_id, double value, boolean p_value){
		this.hoelder_id = hoelder_id;
		if(p_value){
			this.p_value = value;
			this.q_value = 1/(1-1/value);
		}
		else{
			this.q_value = value;
			this.p_value = 1/(1-1/value);
		}
	}

	//Methods
	
    /**
     *
     * @return
     */
    	
	@Override
	public String toString(){
		return "p-value: "+p_value+"   q-value: "+q_value;
	}
	
	//Getter and Setter
	
    /**
     *
     * @return
     */
    	
	public int getHoelderID() {
		return hoelder_id;
	}

    /**
     *
     * @return
     */
    public double getPValue() {
		return p_value;
	}

    /**
     *
     * @param p_value
     */
    public void setPValue(double p_value) {
		this.p_value = p_value;
		this.q_value = 1/(1-1/p_value);
	}

    /**
     *
     * @return
     */
    public double getQValue() {
		return q_value;
	}

    /**
     *
     * @param q_value
     */
    public void setQValue(double q_value) {
		this.q_value = q_value;
		this.p_value = 1/(1-1/q_value);
	}
	
    /**
     *
     * @return
     */
    public Hoelder copy() {
            return new Hoelder(this.hoelder_id, this.p_value);
        }
}
