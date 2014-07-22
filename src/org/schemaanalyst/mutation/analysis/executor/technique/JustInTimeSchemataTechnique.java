package org.schemaanalyst.mutation.analysis.executor.technique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.schemaanalyst.dbms.DBMS;
import org.schemaanalyst.dbms.DatabaseInteractor;
import org.schemaanalyst.mutation.Mutant;
import org.schemaanalyst.mutation.analysis.executor.testcase.FullSchemataDeletingTestCaseExecutor;
import org.schemaanalyst.mutation.analysis.executor.testcase.TestCaseExecutor;
import org.schemaanalyst.mutation.analysis.executor.testsuite.DeletingTestSuiteExecutor;
import org.schemaanalyst.mutation.analysis.executor.testsuite.TestSuiteExecutor;
import org.schemaanalyst.mutation.analysis.executor.testsuite.TestSuiteResult;
import org.schemaanalyst.sqlrepresentation.Schema;
import org.schemaanalyst.testgeneration.TestSuite;

/**
 * <p>
 * The 'Just In Time Schemata' mutation analysis approach.</p>
 *
 * @author Chris J. Wright
 */
public class JustInTimeSchemataTechnique extends AbstractSchemataTechnique {
    
    protected Map<String,DatabaseInteractor> threadInteractors;

    public JustInTimeSchemataTechnique(Schema schema, List<Mutant<Schema>> mutants, TestSuite testSuite, DBMS dbms, DatabaseInteractor databaseInteractor, boolean useTransactions) {
        super(schema, mutants, testSuite, dbms, databaseInteractor, useTransactions);
        threadInteractors = new HashMap<>();
    }

    @Override
    public AnalysisResult analyse(TestSuiteResult originalResults) {
        // Get mutant results with schemata changes
        doSchemataSteps();

        // Execute mutation analysis using thread pool
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Map<Mutant<Schema>, Future<MutantStatus>> callResults = startExecution(originalResults, executor);
        AnalysisResult result = collateResults(callResults);
        executor.shutdown();
        
        // Return result
        return result;
    }
    
    @Override
    protected void doSchemataSteps() {
        // Overrides because it does not need combined drop and create statements
        renameMutants(mutants);
    }

    /**
     * Starts the parallel mutation analysis process, returning once all work 
     * is submitted to the executor. Note that return from this method therefore
     * does not indicate success or completion of mutation analysis.
     * 
     * @param originalResults The results for the non-mutant schema
     * @param executor The executor to use for parallel execution
     * @return The collection of mutants and their futures
     */
    private Map<Mutant<Schema>, Future<MutantStatus>> startExecution(TestSuiteResult originalResults, ExecutorService executor) {
        Map<Mutant<Schema>, Future<MutantStatus>> callResults = new HashMap<>();
        for (int mutantId = 0; mutantId < mutants.size(); mutantId++) {
            Mutant<Schema> mutant = mutants.get(mutantId);
            String schemataPrefix = "mutant_" + (mutantId + 1) + "_";
            MutationAnalysisCallable callable = new MutationAnalysisCallable(mutant, schemataPrefix, originalResults);
            Future<MutantStatus> callResult = executor.submit(callable);
            callResults.put(mutant, callResult);
        }
        return callResults;
    }
    
    /**
     * Gathers the results from the multiple threads into a single result. Note 
     * this blocks until results are computed.
     * 
     * @param callResults The futures that will return the results
     * @return The combined result
     * @throws RuntimeException 
     */
    private AnalysisResult collateResults(Map<Mutant<Schema>, Future<MutantStatus>> callResults) throws RuntimeException {
        AnalysisResult result = new AnalysisResult();
        for (Map.Entry<Mutant<Schema>, Future<MutantStatus>> entry : callResults.entrySet()) {
            Mutant<Schema> mutant = entry.getKey();
            Future<MutantStatus> future = entry.getValue();
            try {
                switch (future.get()) {
                    case ALIVE:
                        result.addLive(mutant);
                        break;
                    case KILLED:
                        result.addKilled(mutant);
                        break;
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    /**
     * Class implementing the code for parallel execution of mutation analysis.
     */
    private class MutationAnalysisCallable implements Callable<MutantStatus> {

        private final Mutant<Schema> mutant;
        private final String schemataPrefix;
        private final TestSuiteResult originalResults;

        public MutationAnalysisCallable(Mutant<Schema> mutant, String schemataPrefix, TestSuiteResult originalResults) {
            this.mutant = mutant;
            this.schemataPrefix = schemataPrefix;
            this.originalResults = originalResults;
        }

        @Override
        public MutantStatus call() throws Exception {
            TestSuiteResult mutantResults = executeTestSuiteSchemata(mutant.getMutatedArtefact(), testSuite, schemataPrefix, originalResults);
            return originalResults.equals(mutantResults) ? MutantStatus.ALIVE : MutantStatus.KILLED;
        }
    }

    @Override
    protected TestSuiteResult executeTestSuiteSchemata(Schema schema, TestSuite suite, String schemataPrefix, TestSuiteResult originalResults) {
        DatabaseInteractor threadInteractor = getInteractorForThread(Thread.currentThread());
        TestCaseExecutor caseExecutor = new FullSchemataDeletingTestCaseExecutor(schema, dbms, threadInteractor, schemataPrefix);
        TestSuiteExecutor suiteExecutor = new DeletingTestSuiteExecutor();
        TestSuiteResult result = suiteExecutor.executeTestSuite(caseExecutor, suite);
        databaseInteractor.addInteractions(threadInteractor);
        return result;
    }
    
    protected synchronized DatabaseInteractor getInteractorForThread(Thread thread) {
        String threadName = thread.getName();
        if (!threadInteractors.containsKey(threadName)) {
            threadInteractors.put(threadName, databaseInteractor.duplicate());
        }
        return threadInteractors.get(threadName);
    }

    /**
     * Class representing the status of a mutant after mutation analysis.
     */
    private enum MutantStatus {

        /**
         * Mutant was killed
         */
        KILLED,
        /**
         * Mutant was not killed
         */
        ALIVE
    };

}
