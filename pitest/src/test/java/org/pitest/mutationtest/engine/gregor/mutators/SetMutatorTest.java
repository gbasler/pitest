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
    public void shouldReplaceHeadOptionWithLastOptionForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasHeadOption.class);
        // will fail with only one element since head == tail
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasHeadOption unmutated = new SetMutatorTestClasses.HasHeadOption(integers);
        // since head != tail, the mutant should give a different result
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateTakeForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasTake.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasTake unmutated = new SetMutatorTestClasses.HasTake(integers);
        // since head != tail, the mutant should give a different result
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateTakeRightForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasTakeRight.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasTakeRight unmutated = new SetMutatorTestClasses.HasTakeRight(integers);
        // since head != tail, the mutant should give a different result
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldReplaceHeadOptionWithLastOptionForHashSet() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasHeadOption.class);
        // will fail with only one element since head == tail
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        HashSetMutatorTestClasses.HasHeadOption unmutated = new HashSetMutatorTestClasses.HasHeadOption(integers);
        // since head != tail, the mutant should give a different result
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToListForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToList.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasToList unmutated = new SetMutatorTestClasses.HasToList(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToSeqForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToSeq.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasToSeq unmutated = new SetMutatorTestClasses.HasToSeq(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToIndexedSeqForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToIndexedSeq.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasToIndexedSeq unmutated = new SetMutatorTestClasses.HasToIndexedSeq(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToArrayForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToArray.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasToArray unmutated = new SetMutatorTestClasses.HasToArray(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInForeachForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasForeach.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("1", "2"));
        SetMutatorTestClasses.HasForeach unmutated = new SetMutatorTestClasses.HasForeach(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInMapForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasMap.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("1", "2"));
        SetMutatorTestClasses.HasMap unmutated = new SetMutatorTestClasses.HasMap(strings);
        String actual = mutateAndCall(unmutated, mutant);
        String expected = unmutated.call();
        assertThat(expected, not(equalTo(actual)));
    }

    @Test
    public void shouldMutateOrderInMapWithBreakoutForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasMapWithBreakout.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("1", "2"));
        SetMutatorTestClasses.HasMapWithBreakout unmutated = new SetMutatorTestClasses.HasMapWithBreakout(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInFlatMapForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasFlatMap.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("1", "2"));
        SetMutatorTestClasses.HasFlatMap unmutated = new SetMutatorTestClasses.HasFlatMap(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInFoldLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasFoldLeft.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        SetMutatorTestClasses.HasFoldLeft unmutated = new SetMutatorTestClasses.HasFoldLeft(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInReduceLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasReduceLeft.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("one", "two"));
        SetMutatorTestClasses.HasReduceLeft unmutated = new SetMutatorTestClasses.HasReduceLeft(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInScanLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasScanLeft.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("one", "two"));
        SetMutatorTestClasses.HasScanLeft unmutated = new SetMutatorTestClasses.HasScanLeft(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInScanLeftWithBreakoutForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasScanLeftWithBreakout.class);
        List<String> strings = new LinkedList<String>(Arrays.asList("one", "two"));
        SetMutatorTestClasses.HasScanLeftWithBreakout unmutated = new SetMutatorTestClasses.HasScanLeftWithBreakout(strings);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldMutateOrderInToSeqForHashSet() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasToSeq.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        HashSetMutatorTestClasses.HasToSeq unmutated = new HashSetMutatorTestClasses.HasToSeq(integers);
        assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
    }

    @Test
    public void shouldNotMutateOrderInHeadOptionForTreeSet() throws Exception {
        final Mutant mutant = getFirstMutant(TreeSetMutatorTestClasses.HasHeadOption.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        TreeSetMutatorTestClasses.HasHeadOption unmutated = new TreeSetMutatorTestClasses.HasHeadOption(integers);
        assertThat(unmutated.call(), equalTo(mutateAndCall(unmutated, mutant)));
    }

    @Test
    public void shouldNotMutateOrderInToSeqForTreeSet() throws Exception {
        final Mutant mutant = getFirstMutant(TreeSetMutatorTestClasses.HasToSeq.class);
        List<Object> integers = new LinkedList<Object>(Arrays.asList(1, 2));
        TreeSetMutatorTestClasses.HasToSeq unmutated = new TreeSetMutatorTestClasses.HasToSeq(integers);
        assertThat(unmutated.call(), equalTo(mutateAndCall(unmutated, mutant)));
    }
}
