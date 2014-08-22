package org.pitest.mutationtest.engine.gregor.mutators;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.util.PitError;

/**
 * TODO:
 * - support mutable & immutable
 * - add runtime check and mutate Set, guarded by runtime check for HashSet (i.e. don't do it for TreeSet)
 */
public enum HashSetMutator implements MethodMutatorFactory {

    HASH_SET_ORDERING_MUTATOR;

    public MethodVisitor create(MutationContext context, MethodInfo methodInfo, MethodVisitor methodVisitor) {
        return new ReorderHashSetVisitor(this, context, methodVisitor);
    }

    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }

    @Override
    public String toString() {
        return "HASH_SET_ORDERING_MUTATOR";
    }

    public String getName() {
        return toString();
    }

    private final class ReorderHashSetVisitor extends MethodVisitor {
        private final MethodMutatorFactory factory;
        private final MutationContext context;

        public ReorderHashSetVisitor(final MethodMutatorFactory factory,
                                     final MutationContext context,
                                     final MethodVisitor delegateVisitor) {
            super(Opcodes.ASM5, delegateVisitor);
            this.factory = factory;
            this.context = context;
        }

        @Override
        public void visitMethodInsn(int opc, String owner, String name, String desc, boolean b) {

            if (owner.equals("scala/collection/immutable/Set")) {
                if (name.equals("headOption")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped headOption in " + owner + "::" + name);
                    if (this.context.shouldMutate(newId)) {
                        String updatedName = name.replace("headOption", "lastOption");
                        super.visitMethodInsn(opc, owner, updatedName, desc, b);
                    } else {
                        super.visitMethodInsn(opc, owner, name, desc, b);
                    }
                } else if (name.equals("toSeq")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped toSeq in " + owner + "::" + name);
                    if (this.context.shouldMutate(newId)) {
                        super.visitMethodInsn(opc, owner, name, desc, b);
                        super.visitMethodInsn(opc, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", b);
                    } else {
                        super.visitMethodInsn(opc, owner, name, desc, b);
                    }
                } else {
                    super.visitMethodInsn(opc, owner, name, desc, b);
                }
            } else {
                super.visitMethodInsn(opc, owner, name, desc, b);
            }
        }
    }
}
