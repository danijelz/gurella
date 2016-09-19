package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorTable;
import com.gurella.engine.editor.ui.EditorUi;

public class TagsPropertyEditorFactory implements PropertyEditorFactory<Bits> {
	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<Bits> context) {
		EditorUi editorUi = context.getEditorUi();
		EditorTable<Object> tagsTable = editorUi.createTable(parent);
		//context.getV
		// TODO Auto-generated method stub
		
	}

}
