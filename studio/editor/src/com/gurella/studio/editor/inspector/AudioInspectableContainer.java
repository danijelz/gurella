package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.audio.SoundDuration;
import com.gurella.studio.GurellaStudioPlugin;

public class AudioInspectableContainer extends InspectableContainer<IFile> {
	private Button play;
	private Button stop;
	private ProgressBar progress;

	private Music music;
	private float totalDuration;

	public AudioInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		Composite body = getBody();
		body.setLayout(new GridLayout(2, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		play = toolkit.createButton(body, "Play", SWT.PUSH);
		play.setLayoutData(new GridData(SWT.RIGHT, SWT.BEGINNING, true, false));
		play.addListener(SWT.Selection, e -> music.play());

		stop = toolkit.createButton(body, "Stop", SWT.PUSH);
		stop.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, true, false));
		stop.addListener(SWT.Selection, e -> music.stop());

		progress = new ProgressBar(body, SWT.SMOOTH | SWT.HORIZONTAL);
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		layoutData.horizontalSpan = 2;
		layoutData.minimumWidth = 100;
		progress.setLayoutData(layoutData);

		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		music = Gdx.audio.newMusic(fileHandle);
		totalDuration = SoundDuration.totalDuration(fileHandle);
		progress.setMinimum(0);
		progress.setMaximum((int) (totalDuration * 1000));

		body.addDisposeListener(e -> music.dispose());

		reflow(true);
	}
}
