package com.stateofflow.eclipse.tane.increasescope;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.stateofflow.eclipse.tane.flowanalysis.Analyser;
import com.stateofflow.eclipse.tane.util.Range;

public class DeclarationFinder extends ASTVisitor implements Analyser<VariableDeclarationFragment> {
    private List<VariableDeclarationFragment> declarations;
    private Range range;
    
    @Override
    public List<VariableDeclarationFragment> analyse(Range range, ASTNode node) {
        this.range = range;
        declarations = new ArrayList<VariableDeclarationFragment>();
        node.accept(this);
        return declarations;
    }
    
    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        return false;
    }
    
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        return false;
    }
    
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        if (range.includes(node.getStartPosition(), node.getLength())) {
            declarations.add(node);
        }
        return false;
    }
}
