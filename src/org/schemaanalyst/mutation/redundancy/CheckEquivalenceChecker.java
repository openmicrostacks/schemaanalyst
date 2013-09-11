/*
 */
package org.schemaanalyst.mutation.redundancy;

import org.schemaanalyst.sqlrepresentation.constraint.CheckConstraint;

/**
 * <p>
 * An {@link EquivalenceChecker} that compares two {@link CheckConstraint} 
 * objects to determine if they are equivalent.
 * </p>
 * 
 * <p>
 * Two {@link CheckConstraint} objects are equivalent if they are the same 
 * object, or:
 * <ol>
 * <li>
 * They have matching identifiers;
 * </li>
 * <li>
 * They belong to tables with matching identifiers; and
 * </li>
 * <li>
 * They have equal expressions.
 * </li>
 * </ol>
 * </p>
 * 
 * @author Chris J. Wright
 */
public class CheckEquivalenceChecker extends EquivalenceChecker<CheckConstraint>{

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean areEquivalent(CheckConstraint a, CheckConstraint b) {
        if (super.areEquivalent(a, b)) {
            return true;
        } else if (!a.getIdentifier().equals(b.getIdentifier())) {
            return false;
        } else if (!a.getTable().getIdentifier().equals(b.getTable().getIdentifier())) {
            return false;
        } else {
            return a.getExpression().equals(b.getExpression());
        }
    }
    
}
