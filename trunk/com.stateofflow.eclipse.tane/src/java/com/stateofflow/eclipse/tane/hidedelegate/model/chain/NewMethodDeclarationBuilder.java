package com.stateofflow.eclipse.tane.hidedelegate.model.chain;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNode;
import com.stateofflow.eclipse.tane.rewrite.Rewrite;

class NewMethodDeclarationBuilder {
    private final Rewrite rewrite;
    private final Chain chain;
    private final MethodDeclaration methodDeclaration;

    public NewMethodDeclarationBuilder(final Rewrite rewrite, final Chain chain, final String methodName) {
        this.rewrite = rewrite;
        this.chain = chain;
        methodDeclaration = rewrite.newMethodDeclaration();
        writeNewMethodDeclarationSignature(methodName);
        writeNewMethodBody();
    }

    public MethodDeclaration build() {
        return methodDeclaration;
    }

    private void writeNewMethodBody() {
        methodDeclaration.setBody(new NewMethodBodyBuilder().build(rewrite, chain.copyExpression(rewrite), chain.isVoidExpressionType()));
    }

    @SuppressWarnings("unchecked")
    private void addModifier(final ModifierKeyword modifier) {
        methodDeclaration.modifiers().add(rewrite.newModifier(modifier));
    }

    private void addModifiers() {
        addVisibilityModifiers();
        addStaticModifiers();
    }

    private void addStaticModifiers() {
        copyModifier(ModifierKeyword.STATIC_KEYWORD);
    }

    private void addVisibilityModifiers() {
        copyModifier(ModifierKeyword.PUBLIC_KEYWORD);
        copyModifier(ModifierKeyword.PROTECTED_KEYWORD);
        copyModifier(ModifierKeyword.PRIVATE_KEYWORD);
    }

    private void copyModifier(final ModifierKeyword keyword) {
        if ((chain.getOriginModifiers() & keyword.toFlagValue()) != 0) {
            addModifier(keyword);
        }
    }

    private void writeNewMethodDeclarationSignature(final String methodName) {
        addModifiers();
        addReturnType();
        addName(methodName);
        addFormalParameters();
        addExceptions();
    }

    private void addExceptions() {
    	rewrite.copyExceptions(chain.getThrownExceptions(), methodDeclaration);
    }

    private void addName(final String methodName) {
        methodDeclaration.setName(rewrite.newSimpleName(methodName));
    }

    private void addReturnType() {
        methodDeclaration.setReturnType2(rewrite.addImportReturningType(chain.getReturnType()));
    }

    private void addFormalParameters() {
        for (final ChainNode node : chain) {
            node.copyParameters(methodDeclaration, rewrite);
        }
    }
}
