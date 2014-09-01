package org.pitest.mutationtest.engine.gregor.mutators;

import org.junit.Before;
import org.junit.Test;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.gregor.MutatorTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class SetMutatorTest extends MutatorTestBase {

    // between 4 and 5 elements the implementation switches from Set4 to HashSet
    List<Object> ints1 = new ArrayList<Object>(Arrays.asList(1, 2, 3, 4));
    List<Object> ints2 = new ArrayList<Object>(Arrays.asList(1, 2, 3, 4, 5));
    List<List<Object>> intTests = Arrays.asList(ints1, ints2);

    List<Object> doubles1 = new ArrayList<Object>(Arrays.asList(1.0, 2.0, 3.0, 4.0));
    List<Object> doubles2 = new ArrayList<Object>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
    List<List<Object>> doubleTests = Arrays.asList(doubles1, doubles2);

    List<List<String>> stringTests = Arrays.asList(Arrays.asList("one", "two", "three", "four"),
            Arrays.asList("1one", "2two", "3three", "4four", "5five"));

    @Before
    public void setupEngine() {
        createTesteeWith(HashSetMutator.HASH_SET_ORDERING_MUTATOR);
    }

    @Test
    public void shouldReplaceHeadOptionWithLastOptionForSet() throws Exception {
        // will fail with only one element since head == tail
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasHeadOption.class);

        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasHeadOption unmutated = new SetMutatorTestClasses.HasHeadOption(test);
            // since head != tail, the mutant should give a different result
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateTakeForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasTake.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasTake unmutated = new SetMutatorTestClasses.HasTake(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateTakeRightForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasTakeRight.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasTakeRight unmutated = new SetMutatorTestClasses.HasTakeRight(test);
            // since head != tail, the mutant should give a different result
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldReplaceHeadOptionWithLastOptionForHashSet() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasHeadOption.class);
        for (List<Object> test : intTests) {
            // will fail with only one element since head == tail
            HashSetMutatorTestClasses.HasHeadOption unmutated = new HashSetMutatorTestClasses.HasHeadOption(test);
            // since head != tail, the mutant should give a different result
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToListForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToList.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasToList unmutated = new SetMutatorTestClasses.HasToList(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToSeqForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToSeq.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasToSeq unmutated = new SetMutatorTestClasses.HasToSeq(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToIndexedSeqForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToIndexedSeq.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasToIndexedSeq unmutated = new SetMutatorTestClasses.HasToIndexedSeq(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToIntArrayForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToIntArray.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasToIntArray unmutated = new SetMutatorTestClasses.HasToIntArray(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToDoubleArrayForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToDoubleArray.class);
        for (List<Object> test : doubleTests) {
            SetMutatorTestClasses.HasToDoubleArray unmutated = new SetMutatorTestClasses.HasToDoubleArray(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToStringArrayForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasToStringArray.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasToStringArray unmutated = new SetMutatorTestClasses.HasToStringArray(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInForeachForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasForeach.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasForeach unmutated = new SetMutatorTestClasses.HasForeach(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInMapForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasMap.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasMap unmutated = new SetMutatorTestClasses.HasMap(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInMapWithBreakoutForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasMapWithBreakout.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasMapWithBreakout unmutated = new SetMutatorTestClasses.HasMapWithBreakout(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInFlatMapForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasFlatMap.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasFlatMap unmutated = new SetMutatorTestClasses.HasFlatMap(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInFoldLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasFoldLeft.class);
        for (List<Object> test : intTests) {
            SetMutatorTestClasses.HasFoldLeft unmutated = new SetMutatorTestClasses.HasFoldLeft(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInReduceLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasReduceLeft.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasReduceLeft unmutated = new SetMutatorTestClasses.HasReduceLeft(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInScanLeftForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasScanLeft.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasScanLeft unmutated = new SetMutatorTestClasses.HasScanLeft(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInScanLeftWithBreakoutForSet() throws Exception {
        final Mutant mutant = getFirstMutant(SetMutatorTestClasses.HasScanLeftWithBreakout.class);
        for (List<String> strings : stringTests) {
            SetMutatorTestClasses.HasScanLeftWithBreakout unmutated = new SetMutatorTestClasses.HasScanLeftWithBreakout(strings);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldMutateOrderInToSeqForHashSet() throws Exception {
        final Mutant mutant = getFirstMutant(HashSetMutatorTestClasses.HasToSeq.class);
        for (List<Object> test : intTests) {
            HashSetMutatorTestClasses.HasToSeq unmutated = new HashSetMutatorTestClasses.HasToSeq(test);
            assertThat(unmutated.call(), not(equalTo(mutateAndCall(unmutated, mutant))));
        }
    }

    @Test
    public void shouldNotMutateOrderInHeadOptionForTreeSet() throws Exception {
        final Mutant mutant = getFirstMutant(TreeSetMutatorTestClasses.HasHeadOption.class);
        for (List<Object> test : intTests) {
            TreeSetMutatorTestClasses.HasHeadOption unmutated = new TreeSetMutatorTestClasses.HasHeadOption(test);
            assertThat(unmutated.call(), equalTo(mutateAndCall(unmutated, mutant)));
        }
    }

    @Test
    public void shouldNotMutateOrderInToSeqForTreeSet() throws Exception {
        final Mutant mutant = getFirstMutant(TreeSetMutatorTestClasses.HasToSeq.class);
        for (List<Object> test : intTests) {
            TreeSetMutatorTestClasses.HasToSeq unmutated = new TreeSetMutatorTestClasses.HasToSeq(test);
            assertThat(unmutated.call(), equalTo(mutateAndCall(unmutated, mutant)));
        }
    }
}
