package org.clockworx.battlearena.editor;

public interface WizardStage<E extends EditorContext<E>> {

    void enter(E context);
}