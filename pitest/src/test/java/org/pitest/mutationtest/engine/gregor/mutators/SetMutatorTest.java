package org.pitest.mutationtest.engine.gregor.mutators;

import org.junit.Before;
import org.junit.Test;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.gregor.MutatorTestBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SetMutatorTest extends MutatorTestBase {

    @Before
    public void setupEngine() {
        createTesteeWith(HashSetMutator.HASH_SET_ORDERING_MUTATOR);
    }

    @Test
    public void shouldReplaceHeadOptionWithLastOption() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasHeadOption.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2, 3, 4, 5));
        SetMutatorTestClasses.HasHeadOption unmutated = new SetMutatorTestClasses.HasHeadOption(integers);
        assertMutantCallableReturns(unmutated, mutant, "4"); // not 5 since unordered!
    }
}
