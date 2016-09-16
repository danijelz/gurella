package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.audio.SoundDuration;
import com.gurella.studio.GurellaStudioPlugin;

public class AudioInspectableContainer extends InspectableContainer<IFile> {
	private Label titleLabel;
	private Label durationLabel;
	private Button playButton;
	private Button stopButton;
	private Button pauseButton;
	private Button muteButton;
	private ProgressBar progressBar;

	private Music music;
	private float totalDuration;

	public AudioInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		Composite body = getBody();
		body.setLayout(new GridLayout(2, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		music = Gdx.audio.newMusic(fileHandle);
		body.addDisposeListener(e -> music.dispose());
		totalDuration = SoundDuration.totalDuration(fileHandle);

		Composite header = toolkit.createComposite(body);
		header.setLayout(new GridLayout(2, false));
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).applyTo(header);

		titleLabel = toolkit.createLabel(header, target.getName()/*, SWT.WRAP | SWT.LEFT*/);
		GridDataFactory.swtDefaults().grab(true, false).hint(150, 18).applyTo(titleLabel);

		durationLabel = toolkit.createLabel(header, "0:00:00 / " + formatDuration((int) totalDuration));
		GridDataFactory.swtDefaults().grab(false, false).align(SWT.RIGHT, SWT.TOP).applyTo(durationLabel);

		progressBar = new ProgressBar(body, SWT.SMOOTH | SWT.HORIZONTAL);
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		layoutData.horizontalSpan = 2;
		layoutData.minimumWidth = 100;
		progressBar.setLayoutData(layoutData);
		progressBar.setMinimum(0);
		progressBar.setMaximum((int) (totalDuration * 10000));

		playButton = toolkit.createButton(body, "", SWT.PUSH);
		playButton.setImage(GurellaStudioPlugin.getImage("icons/play-button.png"));
		playButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BEGINNING, true, true));
		playButton.addListener(SWT.Selection, e -> music.play());

		stopButton = toolkit.createButton(body, "", SWT.PUSH);
		stopButton.setImage(GurellaStudioPlugin.getImage("icons/stop-button.png"));
		stopButton.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, true, true));
		stopButton.addListener(SWT.Selection, e -> music.stop());

		getDisplay().timerExec(40, () -> updateProgress());

		reflow(true);
	}

	private void updateProgress() {
		if (!isDisposed()) {
			float position = music.getPosition();
			progressBar.setSelection((int) (position * 10000));
			durationLabel.setText(formatDuration((int) position) + " / " + formatDuration((int) totalDuration));
			getDisplay().timerExec(40, () -> updateProgress());
		}
	}

	private static String formatDuration(int duration) {
		int durationTemp = duration;
		int hours = durationTemp / 3600;
		durationTemp = durationTemp % 3600;
		int minutes = durationTemp / 60;
		int seconds = durationTemp % 60;

		StringBuffer buffer = new StringBuffer().append(hours).append(":");
		if (minutes < 10) {
			buffer.append('0');
		}
		buffer.append(minutes).append(":");
		if (seconds < 10) {
			buffer.append('0');
		}
		buffer.append(seconds);

		return buffer.toString();
	}
}
