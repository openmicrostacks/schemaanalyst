package org.schemaanalyst.datageneration.search.objective.value;

import org.schemaanalyst.data.BooleanValue;
import org.schemaanalyst.data.CompoundValue;
import org.schemaanalyst.data.DateTimeValue;
import org.schemaanalyst.data.DateValue;
import org.schemaanalyst.data.NumericValue;
import org.schemaanalyst.data.StringValue;
import org.schemaanalyst.data.TimeValue;
import org.schemaanalyst.data.TimestampValue;
import org.schemaanalyst.data.Value;
import org.schemaanalyst.data.ValueVisitor;
import org.schemaanalyst.datageneration.search.objective.ObjectiveFunction;
import org.schemaanalyst.datageneration.search.objective.ObjectiveValue;
import org.schemaanalyst.logic.RelationalOperator;
import org.schemaanalyst.util.Pair;

public abstract class ValueRelationalObjectiveFunction<T> extends ObjectiveFunction<Pair<T>> {

    protected RelationalOperator op;
    protected boolean nullIsSatisfy;
    protected String description;
    
    public ValueRelationalObjectiveFunction(RelationalOperator op, boolean nullIsSatisfy) {
        this.op = op;
        this.nullIsSatisfy = nullIsSatisfy;
    }
        
    @Override
    public ObjectiveValue evaluate(Pair<T> candidateSolution) {
        T lhs = candidateSolution.getFirst();
        T rhs = candidateSolution.getSecond();

        description = lhs + " " + op + " " + rhs;
        
        if (lhs == null || rhs == null) {
            if (nullIsSatisfy) {
                return ObjectiveValue.optimalObjectiveValue(description);
            } else {
                return ObjectiveValue.worstObjectiveValue(description);
            }            
        }
        
        return computeObjectiveValue(lhs, rhs);
    }

    protected abstract ObjectiveValue computeObjectiveValue(T lhs, T rhs);
    
    public static ObjectiveValue compute(Value lhs, RelationalOperator op, Value rhs, boolean nullIsSatisfy) {

        class RelationalObjectiveFunctionDispatcher implements ValueVisitor {

            Value lhs, rhs;
            RelationalOperator op;
            boolean nullIsSatisfy;
            ObjectiveValue objVal;
            
            ObjectiveValue dispatch(Value lhs, RelationalOperator op, Value rhs, boolean nullIsSatisfy) {
                this.lhs = lhs;
                this.op = op;
                this.rhs = rhs;
                this.nullIsSatisfy = nullIsSatisfy;
                
                if (lhs != null) {
                    lhs.accept(this);
                } else if (rhs != null) {
                    rhs.accept(this);
                } else {
                    booleanValue();
                }
                return objVal;
            }

            @Override
            public void visit(BooleanValue value) {
                booleanValue();
            }

            @Override
            public void visit(DateValue value) {
                compoundValue();
            }

            @Override
            public void visit(DateTimeValue value) {
                compoundValue();
            }

            @Override
            public void visit(NumericValue value) {
                numericValue();
            }

            @Override
            public void visit(StringValue value) {
                compoundValue();
            }

            @Override
            public void visit(TimeValue value) {
                compoundValue();
            }

            @Override
            public void visit(TimestampValue value) {
                numericValue();
            }
            
            protected void booleanValue() {
                BooleanValueRelationalObjectiveFunction objFun = new BooleanValueRelationalObjectiveFunction(op, nullIsSatisfy);
                objVal = objFun.evaluate(new Pair<BooleanValue>((BooleanValue) lhs, (BooleanValue) rhs));
            }
            
            protected void numericValue() {
                NumericValueRelationalObjectiveFunction objFun = new NumericValueRelationalObjectiveFunction(op, nullIsSatisfy);
                objVal = objFun.evaluate(new Pair<NumericValue>((NumericValue) lhs, (NumericValue) rhs));  
            }
            
            protected void compoundValue() {
                CompoundValueRelationalObjectiveFunction objFun = new CompoundValueRelationalObjectiveFunction(op, nullIsSatisfy);
                objVal = objFun.evaluate(new Pair<CompoundValue>((CompoundValue) lhs, (CompoundValue) rhs));
            }
        }      

        return (new RelationalObjectiveFunctionDispatcher()).dispatch(lhs, op, rhs, nullIsSatisfy);
    }
}
