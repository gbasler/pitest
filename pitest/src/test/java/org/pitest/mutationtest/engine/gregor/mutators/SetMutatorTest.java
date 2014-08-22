package org.pitest.mutationtest.engine.gregor.mutators;

import org.junit.Before;
import org.junit.Test;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.gregor.MutatorTestBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class SetMutatorTest extends MutatorTestBase {

    @Before
    public void setupEngine() {
        createTesteeWith(HashSetMutator.HASH_SET_ORDERING_MUTATOR);
    }

    @Test
    public void shouldReplaceHeadOptionWithLastOption() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasHeadOption.class);
        // will fail with only one element since head == tail
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        HashSetMutatorTestClasses.HasHeadOption unmutated = new HashSetMutatorTestClasses.HasHeadOption(integers);
        // since head != tail, the mutant should give a different result
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToSeq() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasToSeq.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        HashSetMutatorTestClasses.HasToSeq unmutated = new HashSetMutatorTestClasses.HasToSeq(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }
}
