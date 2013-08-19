package org.schemaanalyst.sqlrepresentation.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ExpressionFilterWalker} walks an {@link Expression} tree. If a
 * subexpression passes the supplied {@link ExpressionFilter} test, its path is
 * added to a list returned by the {@link #filter(ExpressionFilter)} method.
 * 
 * @author Phil McMinn
 * 
 */

public class ExpressionFilterWalker {

	private Expression expression;

	/**
	 * Constructor
	 * 
	 * @param expression
	 *            the expression to be exhaustively walked through.
	 */
	public ExpressionFilterWalker(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Filters expressions in the expression tree and returns their
	 * {@link ExpressionPath} instances.
	 * 
	 * @param filter
	 *            the filter used to test whether the {@link ExpressionPath} of
	 *            the instance should be saved.
	 * @return A list of {@link ExpressionPath} instances corresponding to
	 *         filtered expressions.
	 */
	public List<ExpressionPath> filter(ExpressionFilter filter) {
		return filter(expression, filter, new ExpressionPath(),
				new ArrayList<ExpressionPath>());
	}

	private List<ExpressionPath> filter(Expression expression,
			ExpressionFilter filter, ExpressionPath path,
			List<ExpressionPath> paths) {

		if (filter.accept(expression)) {
			paths.add(path);
		}

		for (int index = 0; index < expression.getNumSubexpressions(); index++) {
			ExpressionPath currentPath = path.duplicate();
			path.add(index);
			filter(expression.getSubexpression(index), filter, currentPath,
					paths);
		}

		return paths;
	}
}
