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

    private enum State {
        SEEN_BREAKOUT, SEEN_CAN_BUILD_FROM, SEEN_INT_CLASS_TAG, SEEN_DOUBLE_CLASS_TAG,
        SEEN_OBJECT_CLASS_TAG, SEEN_NOTHING
    }

    private final class ReorderHashSetVisitor extends MethodVisitor {
        private final MethodMutatorFactory factory;
        private final MutationContext context;
        private State state;

        public ReorderHashSetVisitor(final MethodMutatorFactory factory,
                                     final MutationContext context,
                                     final MethodVisitor delegateVisitor) {
            super(Opcodes.ASM5, delegateVisitor);
            this.factory = factory;
            this.context = context;
            this.state = State.SEEN_NOTHING;
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

            final Map<String, String> swappableOps2 = new HashMap<String, String>();
            swappableOps2.put("take", "takeRight");
            swappableOps2.put("takeRight", "take");

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
                } else if (swappableOps2.containsKey(name)) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    // TODO: for some reason the wrapping does not work here
                    if (this.context.shouldMutate(newId)) {
                        mv.visitMethodInsn(opc, owner, swappableOps2.get(name), desc, b);
                    } else {
                        visitMethodInsnOriginal(opc, owner, name, desc, b);
                    }
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
                            visitOriginal();

                            // TODO: support Long
                            if (state == State.SEEN_INT_CLASS_TAG) {
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "[I");

                                mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/Predef$", "MODULE$", "Lscala/Predef$;");
                                mv.visitInsn(Opcodes.SWAP);

                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/Predef$", "intArrayOps", "([I)Lscala/collection/mutable/ArrayOps;", false);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/mutable/ArrayOps", "reverse", "()Ljava/lang/Object;", true);
                            } else if (state == State.SEEN_DOUBLE_CLASS_TAG) {
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "[D");

                                mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/Predef$", "MODULE$", "Lscala/Predef$;");
                                mv.visitInsn(Opcodes.SWAP);

                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/Predef$", "doubleArrayOps", "([D)Lscala/collection/mutable/ArrayOps;", false);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/mutable/ArrayOps", "reverse", "()Ljava/lang/Object;", true);
                            } else if (state == State.SEEN_OBJECT_CLASS_TAG) {
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/Object;");

                                mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/Predef$", "MODULE$", "Lscala/Predef$;");
                                mv.visitInsn(Opcodes.SWAP);

                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/Predef$", "refArrayOps", "([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;", false);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/mutable/ArrayOps", "reverse", "()Ljava/lang/Object;", true);
                            }
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

                        // TODO: simplify this a bit...
                        public void visitReplacement() {
                            if (state == State.SEEN_BREAKOUT) {
                                // trying to avoid to have to allocate more variables in the local frame
                                // I think it's safer like this...

                                // v1, v2, v3, v4 -> v3, v4, v1, v2, v3, v4
                                mv.visitInsn(Opcodes.DUP2_X2);
                                // v3, v4, v1, v2, v3, v4 -> v3, v4, v1, v2
                                mv.visitInsn(Opcodes.POP2);

                                // v3, v4, v2, v1
                                mv.visitInsn(Opcodes.SWAP);

                                // replace Set with Seq
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "toSeq", "()Lscala/collection/Seq;", true);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableLike");

                                // v3, v4, v2, v1*
                                mv.visitInsn(Opcodes.SWAP);

                                // v3, v4, v1*, v2
                                mv.visitInsn(Opcodes.DUP2_X2);

                                // v1*, v2, v3, v4, v1*, v2
                                mv.visitInsn(Opcodes.POP2);

                                // v1*, v2, v3, v4

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableLike", name, desc, b);
                            } else {
                                // trying to avoid to have to allocate more variables in the local frame
                                // I think it's safer like this...

                                // v1, v2, v3, v4 -> v3, v4, v1, v2, v3, v4
                                mv.visitInsn(Opcodes.DUP2_X2);
                                // v3, v4, v1, v2, v3, v4 -> v3, v4, v1, v2
                                mv.visitInsn(Opcodes.POP2);

                                // v3, v4, v2, v1
                                mv.visitInsn(Opcodes.SWAP);

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "toSeq", "()Lscala/collection/Seq;", true);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");

                                // v3, v4, v2, v1*
                                mv.visitInsn(Opcodes.SWAP);

                                // v3, v4, v1*, v2
                                mv.visitInsn(Opcodes.DUP2_X2);

                                // v1*, v2, v3, v4, v1*, v2
                                mv.visitInsn(Opcodes.POP2);

                                // v1*, v2, v3, v4

                                // kill canBuildFrom for set on the stack
                                mv.visitInsn(Opcodes.POP);

                                // put canBuildFrom for Seq instead
                                mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/collection/Seq$", "MODULE$", "Lscala/collection/Seq$;");
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/collection/Seq$", "canBuildFrom", "()Lscala/collection/generic/CanBuildFrom;", false);

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableLike", name, desc, b);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableOnce", "toSet", "()Lscala/collection/immutable/Set;", true);
                            }
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("foreach")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            mv.visitInsn(Opcodes.SWAP);

                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/immutable/Set", "toSeq", "()Lscala/collection/Seq;", true);
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/IterableLike");

                            mv.visitInsn(Opcodes.SWAP);

                            // TODO: what about specialized Function1?
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/IterableLike", "foreach", "(Lscala/Function1;)V", true);
                        }
                    };
                    mutateWith(newId, mutator);
                } else if (name.equals("map") || name.equals("flatMap")) {
                    final MutationIdentifier newId = this.context.registerMutation(
                            this.factory, "ordering matters for " + name + " in " + owner + "::" + name);

                    Mutator mutator = new Mutator() {
                        public void visitOriginal() {
                            visitMethodInsnOriginal(opc, owner, name, desc, b);
                        }

                        public void visitReplacement() {
                            if (state == State.SEEN_BREAKOUT) {
                                // replace Set with Seq

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

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableLike", name, "(Lscala/Function1;Lscala/collection/generic/CanBuildFrom;)Ljava/lang/Object;", true);
                            } else {
                                // kill canBuildFrom for set on the stack
                                mv.visitInsn(Opcodes.POP);

                                // replace Set with Seq
                                mv.visitInsn(Opcodes.SWAP);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/immutable/Set", "toSeq", "()Lscala/collection/Seq;", true);
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/Seq", "reverse", "()Ljava/lang/Object;", true);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableLike");
                                mv.visitInsn(Opcodes.SWAP);

                                // put canBuildFrom for Seq instead
                                mv.visitFieldInsn(Opcodes.GETSTATIC, "scala/collection/Seq$", "MODULE$", "Lscala/collection/Seq$;");
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "scala/collection/Seq$", "canBuildFrom", "()Lscala/collection/generic/CanBuildFrom;", false);

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableLike", name, "(Lscala/Function1;Lscala/collection/generic/CanBuildFrom;)Ljava/lang/Object;", true);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "scala/collection/TraversableOnce");
                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "scala/collection/TraversableOnce", "toSet", "()Lscala/collection/immutable/Set;", true);
                            }
                        }
                    };
                    mutateWith(newId, mutator);
                } else {
                    super.visitMethodInsn(opc, owner, name, desc, b);
                }
            } else {
                super.visitMethodInsn(opc, owner, name, desc, b);
            }

            if (owner.equals("scala/reflect/ClassTag$")) {
                if (name.equals("Int")) {
                    state = State.SEEN_INT_CLASS_TAG;
                } else if (name.equals("Double")) {
                    state = State.SEEN_DOUBLE_CLASS_TAG;
                } else if (name.equals("apply")) {
                    state = State.SEEN_OBJECT_CLASS_TAG;
                }
            } else if (name.equals("breakOut")) {
                state = State.SEEN_BREAKOUT;
            } else if (name.equals("canBuildFrom")) {
                state = State.SEEN_CAN_BUILD_FROM;
            } else {
                state = State.SEEN_NOTHING;
            }
        }
    }
}