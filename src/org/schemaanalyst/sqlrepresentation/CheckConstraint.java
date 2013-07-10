package org.schemaanalyst.sqlrepresentation;

import java.util.ArrayList;
import java.util.List;

import org.schemaanalyst.data.Value;
import org.schemaanalyst.sqlrepresentation.checkcondition.CheckCondition;
import org.schemaanalyst.sqlrepresentation.expression.AndExpression;
import org.schemaanalyst.sqlrepresentation.expression.BetweenExpression;
import org.schemaanalyst.sqlrepresentation.expression.Expression;
import org.schemaanalyst.sqlrepresentation.expression.ExpressionTree;
import org.schemaanalyst.sqlrepresentation.expression.ExpressionVisitor;
import org.schemaanalyst.sqlrepresentation.expression.InExpression;
import org.schemaanalyst.sqlrepresentation.expression.ListExpression;
import org.schemaanalyst.sqlrepresentation.expression.NullExpression;
import org.schemaanalyst.sqlrepresentation.expression.OrExpression;
import org.schemaanalyst.sqlrepresentation.expression.ParenthesisedExpression;
import org.schemaanalyst.sqlrepresentation.expression.RelationalExpression;

/**
 * Represents a check constraint applied to a table in a database schema.
 * @author Phil McMinn
 *
 */
public class CheckConstraint extends Constraint {

	private static final long serialVersionUID = 1112035994865637833L;

	/** 
	 * @deprecated 
	 * The condition of the check constraint -- use expression instead
	 * */
	protected CheckCondition checkCondition;
	
	/** The expression of the check constraint */
	protected ExpressionTree expressionTree;
	
	/**
	 * Constructor.
	 * @deprecated
	 * @param name An identifying name for the constraint (can be null).
	 * @param table The table on which the check constraint should hold.
	 * @param checkCondition The condition associated with the check constraint.
	 */
	protected CheckConstraint(String name, Table table, CheckCondition checkCondition) {
		super(name, table);
		this.checkCondition = checkCondition;
	}
	
	/**
	 * Constructor.
	 * @param name An identifying name for the constraint (can be null).
	 * @param table The table on which the check constraint should hold.
	 * @param checkCondition The condition associated with the check constraint.
	 */
	protected CheckConstraint(String name, Table table, ExpressionTree expressionTree) {
		super(name, table);
		this.expressionTree = expressionTree;
	}	
	
	/**
	 * @deprecated
	 * Returns the condition associated with this check constraint.
	 * @return The condition associated with this check constraint.
	 */
	public CheckCondition getCheckCondition() {
		return checkCondition;
	}
	
	/**
	 * Returns the expression denoting this check constraint.
	 * @return The expression denoting this check constraint.
	 */
	public ExpressionTree getExpressionTree() {
		return expressionTree;
	}	
	
	/**
	 * Allows instances of IntegrityConstraintVisitor to visit this constraint.
	 * @param visitor The visitor that wishes to visit the constraint.
	 */
	
	public void accept(ConstraintVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * Copies the check predicate to a different table, deep copying expressions
	 * but mapping columns to the target table.  
	 * @param targetTable The table to copy the constraint to/
	 * @return The copied Check object.
	 */
	public CheckConstraint copyTo(Table targetTable) {

		// Allow for backwards compatibility with old check constraint format.
		// NB: columns appearing in conditions are not remapped. 
		if (checkCondition != null) {
			CheckConstraint copy = new CheckConstraint(this.name, targetTable, this.checkCondition);
			targetTable.addCheckConstraint(copy);
			return copy;
		} 
		
		class ExpressionRemapper implements ExpressionVisitor {

			Expression expression, remappedExpression;
			Table targetTable;
			
			ExpressionRemapper(Expression expression, Table targetTable) {
				this.expression = expression;
				this.targetTable = targetTable;
			}
			
			Expression remap() {
				remappedExpression = null;
				expression.accept(this);
				return remappedExpression;
			}

			private Expression remap(Expression expression) {
				return (new ExpressionRemapper(expression, targetTable)).remap();
			}
			
			private List<Expression> remapList(List<Expression> expressions) {
				List<Expression> remappedList = new ArrayList<>();
				for (Expression expression : expressions) {
					remappedList.add(remap(expression));
				}
				return remappedList;
			}
			
			public void visit(AndExpression expression) {
				remappedExpression = new AndExpression(
											remapList(expression.getSubexpressions()));				
			}

			public void visit(BetweenExpression expression) {
				remappedExpression = new BetweenExpression(
											remap(expression.getSubject()),
											remap(expression.getLHS()),
											remap(expression.getRHS()),
											expression.isNotBetween());
			}

			public void visit(Column expression) {
				remappedExpression = targetTable.getColumn(expression.getName());
			}

			public void visit(InExpression expression) {
				remappedExpression = new InExpression(
						remap(expression.getLHS()),
						remap(expression.getRHS()),
						expression.isNotIn());
			}

			public void visit(ListExpression expression) {
				remappedExpression = new ListExpression(remapList(expression.getSubexpressions()));				
			}

			public void visit(NullExpression expression) {
				remappedExpression = new NullExpression(
						remap(expression.getSubexpression()),
						expression.isNotNull());
			}

			public void visit(OrExpression expression) {
				remappedExpression = new OrExpression(remapList(expression.getSubexpressions()));				
			}

			public void visit(ParenthesisedExpression expression) {
				remappedExpression = new ParenthesisedExpression(
						remap(expression.getSubexpression()));				
			}

			public void visit(RelationalExpression expression) {
				remappedExpression = new RelationalExpression(
						remap(expression.getLHS()),
						expression.getRelationalOperator(),
						remap(expression.getRHS()));							
			}

			public void visit(Value expression) {
				remappedExpression = expression.duplicate();
			}		
		}
		
		ExpressionTree remappedExpressionTree = 
				(ExpressionTree) (new ExpressionRemapper(expressionTree, targetTable)).remap();
		
		CheckConstraint copy = new CheckConstraint(this.name, targetTable, remappedExpressionTree);
		targetTable.addCheckConstraint(copy);
		return copy;
	}
	
	/**
	 *  Checks whether this Check instance is equal to another object.
	 *  The comparison compares predicates only and ignores the constraint's name.
	 *  @param obj The object to compare this instance with.
	 *  @return True if the other object is a Check object with the same predicate, else false.
	 */	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		CheckConstraint other = (CheckConstraint) obj;
		
		// allow for backwards compatibility with old check constraint format
		if (checkCondition == null) {
			if (other.checkCondition != null)
				return false;
		} else if (!checkCondition.equals(other.checkCondition))
			return false;
		
		if (expressionTree == null) {
			if (other.expressionTree != null)
				return false;
		} else if (!expressionTree.equals(other.expressionTree))
			return false;		
		
		return true;
	}
	
	/**
	 * Returns an informative string regarding the constraint.
	 * @return An informative string regarding the constraint.
	 */
	public String toString() {
		String str = "CHECK[";
		
		// allow for backwards compatibility with old check constraint format
		if (checkCondition != null) {
			str += checkCondition;
		} else {
			str += expressionTree;
		}
		
		str += "]";
		return str;
	}	
}
