package org.schemaanalyst.test.datageneration.search.objective.value;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import static junit.framework.Assert.*;
import static junitparams.JUnitParamsRunner.$;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.schemaanalyst.data.BooleanValue;
import org.schemaanalyst.datageneration.search.objective.ObjectiveFunctionException;
import org.schemaanalyst.datageneration.search.objective.value.BooleanValueRelationalObjectiveFunction;
import org.schemaanalyst.logic.RelationalOperator;
import org.schemaanalyst.util.Pair;

import static org.schemaanalyst.test.testutil.ObjectiveValueAssert.*;

@RunWith(JUnitParamsRunner.class)
public class TestBooleanValueRelationalObjectiveFunction {

    Object[] optimalValues() {
        return $(
                $(new BooleanValue(true), RelationalOperator.EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(false), RelationalOperator.EQUALS, new BooleanValue(false), false),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(true), RelationalOperator.NOT_EQUALS, new BooleanValue(false), false),
                
                // same tests above should pass regardless of nullIsSatisfy, as there are no nulls
                $(new BooleanValue(true), RelationalOperator.EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(false), RelationalOperator.EQUALS, new BooleanValue(false), false),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(true), RelationalOperator.NOT_EQUALS, new BooleanValue(false), false),                
                
                $(new BooleanValue(false), RelationalOperator.EQUALS, null, true),
                $(null, RelationalOperator.EQUALS, null, true),
                $(null, RelationalOperator.EQUALS, new BooleanValue(false), true),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, null, true),
                $(null, RelationalOperator.NOT_EQUALS, null, true),
                $(null, RelationalOperator.NOT_EQUALS, new BooleanValue(false), true)                
        );
    }    
    
    @Test
    @Parameters(method = "optimalValues")    
    public void testOptimal(BooleanValue lhs, RelationalOperator op, BooleanValue rhs, boolean nullIsSatisfy) {        
        BooleanValueRelationalObjectiveFunction objFun = 
                new BooleanValueRelationalObjectiveFunction(op, nullIsSatisfy);
        
        assertOptimal(objFun.evaluate(new Pair<BooleanValue>(lhs, rhs)));
    }
    
    // -----------------------------------------------------------------------------------------------------------------------        
    
    Object[] worstValues() {
        return $(
                $(new BooleanValue(true), RelationalOperator.EQUALS, new BooleanValue(false), true),
                $(new BooleanValue(false), RelationalOperator.EQUALS, new BooleanValue(true), true),
                $(new BooleanValue(true), RelationalOperator.NOT_EQUALS, new BooleanValue(true), true),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, new BooleanValue(false), true),

                // same tests above should pass regardless of nullIsSatisfy, as there are no nulls
                $(new BooleanValue(true), RelationalOperator.EQUALS, new BooleanValue(false), false),
                $(new BooleanValue(false), RelationalOperator.EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(true), RelationalOperator.NOT_EQUALS, new BooleanValue(true), false),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, new BooleanValue(false), false),                
                
                $(new BooleanValue(false), RelationalOperator.EQUALS, null, false),
                $(null, RelationalOperator.EQUALS, null, false),
                $(null, RelationalOperator.EQUALS, new BooleanValue(false), false),
                $(new BooleanValue(false), RelationalOperator.NOT_EQUALS, null, false),
                $(null, RelationalOperator.NOT_EQUALS, null, false),
                $(null, RelationalOperator.NOT_EQUALS, new BooleanValue(false), false)                
        );
    }     
            
    @Test
    @Parameters(method = "worstValues")    
    public void testWorst(BooleanValue lhs, RelationalOperator op, BooleanValue rhs, boolean nullIsSatisfy) {        
        BooleanValueRelationalObjectiveFunction objFun = 
                new BooleanValueRelationalObjectiveFunction(op, nullIsSatisfy);
        
        assertWorst(objFun.evaluate(new Pair<BooleanValue>(lhs, rhs)));
    } 
    
    // -----------------------------------------------------------------------------------------------------------------------
    
    Object[] throwsExceptionValues() {
        return $(
                $(new BooleanValue(true), RelationalOperator.GREATER, new BooleanValue(false), true),
                $(new BooleanValue(true), RelationalOperator.GREATER_OR_EQUALS, new BooleanValue(false), true),
                $(new BooleanValue(true), RelationalOperator.LESS, new BooleanValue(false), true),
                $(new BooleanValue(true), RelationalOperator.LESS_OR_EQUALS, new BooleanValue(false), true),                
                $(null, RelationalOperator.GREATER, null, true)                
        );
    }      
    
    @Test
    @Parameters(method = "throwsExceptionValues")    
    public void testThrowsException(BooleanValue lhs, RelationalOperator op, BooleanValue rhs, boolean nullIsSatisfy) {        
        boolean fail = true;
        try {        
            BooleanValueRelationalObjectiveFunction objFun = 
                    new BooleanValueRelationalObjectiveFunction(op, nullIsSatisfy);

            assertWorst(objFun.evaluate(new Pair<BooleanValue>(lhs, rhs)));
        } catch (ObjectiveFunctionException e) {
            fail = false;            
        }
        
        if (fail) {
            fail("Exception should be thrown, but wasn't");
        }
    }     
    
}
