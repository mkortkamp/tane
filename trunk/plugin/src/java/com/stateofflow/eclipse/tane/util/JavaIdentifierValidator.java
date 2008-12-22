package com.stateofflow.eclipse.tane.util;

public class JavaIdentifierValidator {
    public boolean validate(final String identifier) {
        return isValidLength(identifier) && isValidFirstCharacter(identifier) && isValidCharactersAfterTheFirst(identifier);
    }

    private boolean isValidCharactersAfterTheFirst(final String identifier) {
        for (int i = 1; i < identifier.length(); i++) {
            if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidFirstCharacter(final String identifier) {
        return Character.isJavaIdentifierStart(identifier.charAt(0));
    }

    private boolean isValidLength(final String identifier) {
        return identifier.length() > 0;
    }
}
