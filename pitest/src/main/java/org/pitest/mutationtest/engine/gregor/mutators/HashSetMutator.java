package org.pitest.mutationtest.engine.gregor.mutators;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.util.PitError;
import org.objectweb.asm.Label;

import java.util.HashMap;
import java.util.Map;

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

    private enum State {SEEN_NOTHING, SEEN_FOLDLEFT}

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

            final Map<String, String> swappableOps = new HashMap<String, String>();
            swappableOps.put("headOption", "lastOption");
            swappableOps.put("lastOption", "headOption");

            if (isSet || isHashSet) {
                if (swappableOps.containsKey(name)) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            mv.visitMethodInsn(opc, owner, swappableOps.get(name), desc, b);
                        }
                    };

                    mutateWith(newId, mutator);
                } else if (name.equals("toSeq")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

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
                } else if (name.equals("toList")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            visitOriginal();
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "scala/collection/immutable/List", "reverse", "()Lscala/collection/immutable/List;", false);
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("toIndexedSeq")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

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
                            this.factory, "ordering matters for toArray in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            // TODO: fix types!!! this works only for integer arrays!
                            visitOriginal();
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "[I");

                            mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/Predef$", "MODULE$", "Lscala/Predef$;");
                            mv.visitInsn(Opcodes.SWAP);

                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/Predef$", "intArrayOps", "([I)Lscala/collection/mutable/ArrayOps;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/mutable/ArrayOps", "reverse", "()Ljava/lang/Object;", true);
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("foldLeft") || name.equals("foldRight")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        // TODO: check collection.breakOut!!!
                        public void visitReplacement() {
                            // trying to avoid to have to allocate more variables in the local frame
                            // I think it's safer like this...

                            // v1, v2, v2 -> v2, v3, v1, v2, v3
                            mv.visitInsn(Opcodes.DUP2_X1);
                            // v2, v3, v1, v2, v3 -> v2, v3, v1
                            mv.visitInsn(Opcodes.POP2);

                            // v2, v3, v1*
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "toSeq", "()Lscala/collection/Seq;", true);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");

                            // v2, v3, v1* -> v1*, v2, v3, v1*
                            mv.visitInsn(Opcodes.DUP_X2);
                            // v1*, v2, v3, v1* -> v1*, v2, v3
                            mv.visitInsn(Opcodes.POP);

                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableOnce", name, desc, b);
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("reduceLeft") || name.equals("reduceRight")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        // TODO: check collection.breakOut!!!
                        public void visitReplacement() {
                            // trying to avoid to have to allocate more variables in the local frame
                            // I think it's safer like this...

                            mv.visitInsn(Opcodes.SWAP);

                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "toSeq", "()Lscala/collection/Seq;", true);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");

                            mv.visitInsn(Opcodes.SWAP);

                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableOnce", name, desc, b);
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("scanLeft") || name.equals("scanRight")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        // TODO: check collection.breakOut!!!
                        public void visitReplacement() {
                            // trying to avoid to have to allocate more variables in the local frame
                            // I think it's safer like this...

                            // v1, v2, v2 -> v2, v3, v1, v2, v3
                            mv.visitInsn(Opcodes.DUP2_X1);
                            // v2, v3, v1, v2, v3 -> v2, v3, v1
                            mv.visitInsn(Opcodes.POP2);

                            // v2, v3, v1*
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "toSeq", "()Lscala/collection/Seq;", true);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");

                            // v2, v3, v1* -> v1*, v2, v3, v1*
                            mv.visitInsn(Opcodes.DUP_X2);
                            // v1*, v2, v3, v1* -> v1*, v2, v3
                            mv.visitInsn(Opcodes.POP);

                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableOnce", name, desc, b);
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
