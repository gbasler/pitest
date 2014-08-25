package org.pitest.mutationtest.engine.gregor.mutators;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.util.PitError;
import org.objectweb.asm.Label;

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

    private static interface Mutator {
        void visitOriginal();

        void visitReplacement();
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

        public void mutateWith(final MutationIdentifier newId, Mutator mutator) {
            if (this.context.shouldMutate(newId)) {
                // do not mutate SortedSet
                mv.visitInsn(Opcodes.DUP);
                mv.visitTypeInsn(Opcodes.INSTANCEOF, "scala/collection/immutable/SortedSet");
                Label l0 = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, l0);
                mutator.visitOriginal();
                Label l1 = new Label();
                mv.visitJumpInsn(Opcodes.GOTO, l1);
                mv.visitLabel(l0);

                mutator.visitReplacement();

                mv.visitLabel(l1);
            } else {
                mutator.visitOriginal();
            }
        }

        public void visitMethodInsnOriginal(final int opc, final String owner, final String name, final String desc, final boolean b) {
            super.visitMethodInsn(opc, owner, name, desc, b);
        }

        @Override
        public void visitMethodInsn(final int opc, final String owner, final String name, final String desc, final boolean b) {

            final boolean isSet = owner.equals("scala/collection/immutable/Set");
            boolean isHashSet = owner.equals("scala/collection/immutable/HashSet");
            if (isSet || isHashSet) {
                if (name.equals("headOption")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped headOption in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            mv.visitMethodInsn(opc, owner, "lastOption", desc, b);
                        }
                    };

                    mutateWith(newId, mutator);
                } else if (name.equals("toSeq")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped toSeq in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            visitOriginal();
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                                    "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/Seq");
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("toIndexedSeq")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped toIndexedSeq in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            visitOriginal();
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                                    "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/IndexedSeq");
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("toArray")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "swapped toArray in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            visitOriginal();
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "[I");

                            mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/Predef$", "MODULE$", "Lscala/Predef$;");
                            mv.visitInsn(Opcodes.SWAP);

                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/Predef$", "intArrayOps", "([I)Lscala/collection/mutable/ArrayOps;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/mutable/ArrayOps", "reverse", "()Ljava/lang/Object;", true);
                        }
                    };
                    mutateWith(newId, mutator);
                } else {
                    super.visitMethodInsn(opc, owner, name, desc, b);
                }
            } else {
                super.visitMethodInsn(opc, owner, name, desc, b);
            }
        }
    }
}
