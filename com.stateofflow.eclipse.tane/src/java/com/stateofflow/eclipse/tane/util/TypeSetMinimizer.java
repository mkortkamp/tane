package com.stateofflow.eclipse.tane.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class TypeSetMinimizer {
    public Set<ITypeBinding> getMinimalSet(final Set<ITypeBinding> types) {
        final Set<ITypeBinding> copy = new HashSet<ITypeBinding>(types);
        copy.removeAll(getSubsumed(types));
        return copy;
    }

    private Set<ITypeBinding> getSubsumed(final Set<ITypeBinding> types) {
        final Set<ITypeBinding> subclasses = new HashSet<ITypeBinding>();
        for (final ITypeBinding type : types) {
            if (isSubsumedBySuperclass(types, type)) {
                subclasses.add(type);
            }
        }
        return subclasses;
    }

    private boolean isSubsumedBySuperclass(final Set<ITypeBinding> types, final ITypeBinding type) {
        for (final ITypeBinding potentialSuperclass : types) {
            if (potentialSuperclass != type && type.isSubTypeCompatible(potentialSuperclass)) {
                return true;
            }
        }
        return false;
    }
}
