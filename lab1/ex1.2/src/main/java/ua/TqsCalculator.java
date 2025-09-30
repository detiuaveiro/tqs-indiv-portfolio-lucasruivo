package ua;

/**
 * Basic calculator class with methods for addition, subtraction, multiplication, and division.
 */
public class TqsCalculator {

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return a / b;
    }

    public double exp(double a, int b) {
        double c = a;
        if( b == 0){
            return 1;
        }
        if ( b == 1){
            return a;
        }
        for(int i = 0; i < b-1; i++){
            a = a * c;
        }
        return a;
    }

    public double sqroot(double a){
        if (a < 0) {
            throw new IllegalArgumentException("Square of a negative number is impossible");
        }
        return Math.sqrt(a);
    }

}
