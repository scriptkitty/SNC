package unikl.disco.misc;

import org.apache.commons.math3.fraction.Fraction;
import java.lang.Number;

//TODO What to return in the default case?
//TODO What to return if the type check fails?
//TODO Throw proper Exceptions such as FractionConversionException
public class NumberObj {
	//FIXME Implement a global flag in Analysis.java (?) and read that out
//	private static NumberObjType type = NumberObjType.DOUBLE;
	private static NumberObjType type = NumberObjType.RATIONAL;
	
	private static boolean performTypeChecks = false;
	
	private Number value;

	public static final NumberObj POSITIVE_INFINITY = getPosInfinity();
	public static final NumberObj NEGATIVE_INFINITY = getNegInfinity();
	public static final NumberObj NaN = getNaN();
	
	public NumberObj(){
		switch ( type ) {
        case DOUBLE:
        	this.value = new Double( 0.0 );
        	break;
        case RATIONAL:
        	this.value = new Fraction( 0.0 );
        	break;
		default:
		}
	}

	public NumberObj( double value ) {
		switch ( type ) {
        case DOUBLE:
        	this.value = new Double( value );
        	break;
        case RATIONAL:
        	this.value = new Fraction( value );
        	break;
		default:
        	System.exit( 0 );
		}
	}
	
	private NumberObj( Number value ) {
		this.value = value;
	}
	
	public static NumberObj DoubleToNumberObj( Double doubleObj ){
		return new NumberObj( doubleObj );
	}
	
	private static NumberObj getNaN() {
		switch ( type ) {
        case DOUBLE:
        	return new NumberObj( Double.NaN );
        case RATIONAL:
        	return new NumberObj( new Fraction( Double.NaN ) );
		default:
			return null;
		}
	}
	
	private static NumberObj getPosInfinity() {
		switch ( type ) {
        case DOUBLE:
        	return new NumberObj( Double.POSITIVE_INFINITY );
        case RATIONAL:
        	// Fraction is based in Integer and thus there's no infinity (and it is prone to overflows)
        	return new NumberObj( new Fraction( Integer.MAX_VALUE ) );
		default:
			return null;
		}
	}
	
	private static NumberObj getNegInfinity() {
		switch ( type ) {
        case DOUBLE:
        	return new NumberObj( Double.NEGATIVE_INFINITY );
        case RATIONAL:
        	// Fraction is based in Integer and thus there's no infinity (and it is prone to overflows)
        	return new NumberObj( new Fraction( Integer.MIN_VALUE ) );
		default:
			return null;
		}
	}
	
	public static NumberObj getEpsilon() {
		switch ( type ) {
        case DOUBLE:
        	return new NumberObj( new Double( 1e-6 ) );
        case RATIONAL:
        	// unfortunately you cannot give the constructor the double value 0.0000001
        	return new NumberObj( new Fraction( 1, 1000000 ) );
		default:
			return null;
		}
	}
	
	public NumberObjType getType() {
		return type;
	}
	
	private static boolean TypeCheck( NumberObj num1, NumberObj num2 ) {
		if ( num1.getType() != num2.getType() ) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public Number getValue() {
		return value;
	}

	// In order to simplify the transition from the primitive data type double to
	// a Double object wrapped around it or a rational number object
	// these functions emulate copy by value for objects that
	// typically inhibit copy by reference in Java
	public static NumberObj add( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}

		if( num1.equals( NaN ) || num2.equals( NaN ) ) {
			return NaN;
		}
		// prevent overflow exception when adding integer based number representations like Fraction
		if( num1.equals( POSITIVE_INFINITY ) || num2.equals(  POSITIVE_INFINITY ) ) {
			return POSITIVE_INFINITY;
		}
		if( num1.equals( NEGATIVE_INFINITY ) || num2.equals(  NEGATIVE_INFINITY ) ) {
			return NEGATIVE_INFINITY;
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( double1.doubleValue() + double2.doubleValue() );
        case RATIONAL:
        	Fraction frac1 = (Fraction) num1.getValue();
        	Fraction frac2 = (Fraction) num2.getValue();
        	// may throw MathArithmeticException due to integer overflow
        	return new NumberObj( frac1.add( frac2 ) );
		default:
        	return null;
		}
	}
	
	public static NumberObj sub( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}

		if( num1.equals( NaN ) || num2.equals( NaN ) ) {
			return NaN;
		}
		// prevent overflow exception when adding integer based number representations like Fraction
		if( num1.equals( POSITIVE_INFINITY ) || num2.equals(  POSITIVE_INFINITY ) ) {
			return POSITIVE_INFINITY;
		}
		if( num1.equals( NEGATIVE_INFINITY ) || num2.equals(  NEGATIVE_INFINITY ) ) {
			return NEGATIVE_INFINITY;
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( double1.doubleValue() - double2.doubleValue() );
        case RATIONAL:
        	Fraction frac1 = (Fraction) num1.getValue();
        	Fraction frac2 = (Fraction) num2.getValue();
        	// may throw MathArithmeticException due to integer overflow
        	return new NumberObj( frac1.subtract( frac2 ) );
		default:
        	return null;
		}
	}
	
	public static NumberObj mult( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}

		if( num1.equals( NaN ) || num2.equals( NaN ) ) {
			return NaN;
		}
		// prevent overflow exception when adding integer based number representations like Fraction
		if( num1.equals( POSITIVE_INFINITY ) || num2.equals(  POSITIVE_INFINITY ) ) {
			return POSITIVE_INFINITY;
		}
		if( num1.equals( NEGATIVE_INFINITY ) || num2.equals(  NEGATIVE_INFINITY ) ) {
			return NEGATIVE_INFINITY;
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( double1.doubleValue() * double2.doubleValue() );
        case RATIONAL:
        	Fraction frac1 = (Fraction) num1.getValue();
        	Fraction frac2 = (Fraction) num2.getValue();
        	// may throw MathArithmeticException due to integer overflow
        	return new NumberObj( frac1.multiply( frac2 ) );
		default:
        	return null;
		}
	}

	public static NumberObj div( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}

		if( num1.equals( NaN ) || num2.equals( NaN ) ) {
			return NaN;
		}
		// Integer based number representations use Integer.MAX_VALUE to signal infinity so special treatment is necessary when dividing
		if( num1.equals( POSITIVE_INFINITY ) ) {
			return POSITIVE_INFINITY;
		}
		if( num2.equals( POSITIVE_INFINITY ) ) {
			return new NumberObj( 0.0 );
		}
		if( num1.equals( NEGATIVE_INFINITY ) ) {
			return NEGATIVE_INFINITY;
		}
		if( num2.equals( NEGATIVE_INFINITY ) ) {
			return new NumberObj( 0.0 );
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( double1.doubleValue() / double2.doubleValue() );
        case RATIONAL:
        	Fraction frac2 = (Fraction) num2.getValue();
        	if ( frac2.getNumerator() == 0 ) {
        		return getPosInfinity();
//        		return getNaN();
        	} else {
            	Fraction frac1 = (Fraction) num1.getValue();
            	return new NumberObj( frac1.divide( frac2 ) );        		
        	}
		default:
        	return null;
		}
	}	
	
	public NumberObj copy() {
		switch ( type ) {
        case DOUBLE:
        	return new NumberObj( value );
        case RATIONAL:
        	return new NumberObj( value );
		default:
			return new NumberObj( 0.0 );
		}
	}

	/*
	 * java.lang.Math's max(double, double) description:
	 * 
	 * Returns the greater of two double values. That is, the result is the argument closer to positive infinity.
	 * If the arguments have the same value, the result is that same value. If either value is NaN, then the result is NaN.
	 * Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero.
	 * If one argument is positive zero and the other negative zero, the result is positive zero.
	 * 
	 * SB's thoughts because there's no max() in Fraction:
	 * "result is that same value" implicitly uses copy by value semantics
	 * Can a fraction be NaN?
	 * The -/+ zero stuff is not needed
	 */
	public static NumberObj max( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( Math.max( double1.doubleValue(), double2.doubleValue() ) );
        case RATIONAL:    	
        	Fraction frac1 = (Fraction) num1.getValue();
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range        	
        	double frac1_numerator = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_numerator = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_numerator >= frac2_numerator ) {
        		return new NumberObj( frac1 );
        	} else {
        		return new NumberObj( frac2 );
        	}
		default:
        	return null;
		}
	}

	public static NumberObj min( NumberObj num1, NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( num1, num2 );
		}
		
		switch ( num1.getType() ) {
        case DOUBLE:
        	Double double1 = (Double) num1.getValue();
        	Double double2 = (Double) num2.getValue();
        	return new NumberObj( Math.min( double1.doubleValue(), double2.doubleValue() ) );
        case RATIONAL:    	
        	Fraction frac1 = (Fraction) num1.getValue();
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_numerator = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_numerator = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_numerator < frac2_numerator ) {
        		return new NumberObj( frac1 );
        	} else {
        		return new NumberObj( frac2 );
        	}
		default:
        	return null;
		}
	}
	
	public boolean equals( double num2 ) {
		if( num2 == Double.NaN ){
			return value.equals( NaN );
		}
		if( num2 == Double.POSITIVE_INFINITY ){
			return value.equals( POSITIVE_INFINITY );
		}
		if( num2 == Double.NEGATIVE_INFINITY ){
			return value.equals( NEGATIVE_INFINITY );
		}
		return equals( new NumberObj( num2 ) );
	}

	public boolean equals( NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( this, num2 );
		}

		if( this == NaN & num2 == NaN ){
			return true;
		}
		if( this == POSITIVE_INFINITY & num2 == POSITIVE_INFINITY ){
			return true;
		}
		if( this == NEGATIVE_INFINITY & num2 == NEGATIVE_INFINITY ){
			return true;
		}
		
		switch ( type ) {
        case DOUBLE:
        	Double double2 = (Double) num2.getValue();
        	return value.doubleValue() == double2.doubleValue();
        case RATIONAL:
        	//Fractions's equals() method is inherited from java.lang.Object
        	Fraction frac1 = (Fraction) value;
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_num = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_num = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_num == frac2_num ) {
        		return true;
        	} else {
        		return false;
        	}
		default:
        	return false;
		}
	}

	public boolean greater( NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( this, num2 );
		}

		if( this == NaN || num2 == NaN ){
			return false;
		}
		if( this == POSITIVE_INFINITY & num2 == POSITIVE_INFINITY ){
			return false;
		}
		if( this == NEGATIVE_INFINITY & num2 == NEGATIVE_INFINITY ){
			return false;
		}
		
		switch ( type ) {
        case DOUBLE:
        	Double double2 = (Double) num2.getValue();
        	return value.doubleValue() > double2.doubleValue();
        case RATIONAL:
        	Fraction frac1 = (Fraction) value;
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_num = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_num = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_num > frac2_num ) {
        		return true;
        	} else {
        		return false;
        	}
		default:
        	return false;
		}
	}

	public boolean ge( NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( this, num2 );
		}

		if( this == NaN || num2 == NaN ){
			return false;
		}
		if( this == POSITIVE_INFINITY & num2 == POSITIVE_INFINITY ){
			return true;
		}
		if( this == NEGATIVE_INFINITY & num2 == NEGATIVE_INFINITY ){
			return true;
		}
				
		switch ( type ) {
        case DOUBLE:
        	Double double2 = (Double) num2.getValue();
        	return value.doubleValue() >= double2.doubleValue();
        case RATIONAL:
        	Fraction frac1 = (Fraction) value;
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_num = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_num = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_num >= frac2_num ) {
        		return true;
        	} else {
        		return false;
        	}
		default:
        	return false;
		}
	}

	public boolean less( NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( this, num2 );
		}

		if( this == NaN || num2 == NaN ){
			return false;
		}
		if( this == POSITIVE_INFINITY & num2 == POSITIVE_INFINITY ){
			return false;
		}
		if( this == NEGATIVE_INFINITY & num2 == NEGATIVE_INFINITY ){
			return false;
		}
			
		
		switch ( type ) {
        case DOUBLE:
        	Double double2 = (Double) num2.getValue();
        	return value.doubleValue() < double2.doubleValue();
        case RATIONAL:
        	Fraction frac1 = (Fraction) value;
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_num = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_num = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_num < frac2_num ) {
        		return true;
        	} else {
        		return false;
        	}
		default:
        	return false;
		}
	}

	public boolean le( NumberObj num2 ) {
		if ( performTypeChecks ) {
			TypeCheck( this, num2 );
		}

		if( this == NaN || num2 == NaN ){
			return false;
		}
		if( this == POSITIVE_INFINITY & num2 == POSITIVE_INFINITY ){
			return true;
		}
		if( this == NEGATIVE_INFINITY & num2 == NEGATIVE_INFINITY ){
			return true;
		}
		
		switch ( type ) {
        case DOUBLE:
        	Double double2 = (Double) num2.getValue();
        	return value.doubleValue() <= double2.doubleValue();
        case RATIONAL:
        	Fraction frac1 = (Fraction) value;
        	Fraction frac2 = (Fraction) num2.getValue();

        	// operate on Doubles to prevent getting out of Integer's range 
        	double frac1_num = new Double( frac1.getNumerator() ) * new Double( frac2.getDenominator() );
        	double frac2_num = new Double( frac2.getNumerator() ) * new Double( frac1.getDenominator() );
        	
        	if ( frac1_num <= frac2_num ) {
        		return true;
        	} else {
        		return false;
        	}
		default:
        	return false;
		}
	}
	
	public static NumberObj abs( NumberObj num ) {
		if ( num.equals( NaN ) ) {
    		return NaN;
    	}
    	if ( num.equals( POSITIVE_INFINITY ) ) {
    		return POSITIVE_INFINITY;
    	}
    	if ( num.equals( NEGATIVE_INFINITY ) ) {
			return NEGATIVE_INFINITY;
		}

		switch ( num.getType() ) {
	    case DOUBLE:
	    	Double double_value = (Double) num.getValue();
	    	return new NumberObj(  Math.abs( double_value.doubleValue() ) );
	    case RATIONAL:
	    	Fraction frac = (Fraction) num.getValue();
	    	return new NumberObj( frac.abs() );
		default:
			return new NumberObj( 0.0 );
		}
	}
	
	public static NumberObj negate( NumberObj num ) {
		if ( num.equals( NaN ) ) {
    		return NaN;
    	}
    	if ( num.equals( POSITIVE_INFINITY ) ) {
    		return NEGATIVE_INFINITY;
    	}
    	if ( num.equals( NEGATIVE_INFINITY ) ) {
			return POSITIVE_INFINITY;
		}
    	
		switch ( num.getType() ) {
	    case DOUBLE:
	    	Double double_value = (Double) num.getValue();
	    	return new NumberObj(  -(double_value.doubleValue()) );
	    case RATIONAL:
	    	Fraction frac = (Fraction) num.getValue();
	    	return new NumberObj( frac.negate() );
		default:
			return new NumberObj( 0.0 );
		}
	}

	@Override
	public String toString(){
		if ( this.equals( NaN ) ) {
    		return "NaN";
    	}
    	if ( this.equals( POSITIVE_INFINITY ) ) {
    		return "Infinity";
    	}
    	if ( this.equals( NEGATIVE_INFINITY ) ) {
			return "-Infinity";
		}
    	
		switch ( this.getType() ) {
	    case DOUBLE:
	    	return Double.toString( (Double)value );
	    case RATIONAL:
	    	return ( (Fraction)value ).toString();
		default:
			return "invalied number";
		}
	}
}
