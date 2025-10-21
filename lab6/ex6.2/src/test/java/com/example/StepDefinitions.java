package com.example;

import io.cucumber.java.en.*;

import org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    @Given("a calculator I just turned on")
    public void a_calculator_i_just_turned_on() {
        System.out.println("Calculator is turned on");
    }

    @When("I add {int} and {int}")
    public void i_add_and(Integer int1, Integer int2) {
        System.out.println("Adding " + int1 + " and " + int2);
    }

    @When("I subtract {int} to {int}")
    public void i_subtract_to(Integer int1, Integer int2) {
        System.out.println("Subtracting " + int2 + " from " + int1);
    }

    @When("I multiply {int} by {int}")
    public void i_multiply_by(Integer int1, Integer int2) {
        System.out.println("Multiplying " + int1 + " by " + int2);
    }

    @When("I divide {int} by {int}")
    public void i_divide_by(Integer int1, Integer int2) {
        if (int2 == 0) {
            System.out.println("Division by zero is not allowed");
        } else {
            System.out.println("Dividing " + int1 + " by " + int2);
        }
    }

    @Then("the result is undefined")
    public void the_result_is_undefined() {
        System.out.println("The result is undefined due to division by zero.");
    }

    @Then("the result is {int}")
    public void the_result_is(Integer int1) {
        System.out.println("The result is " + int1);
    }

}
