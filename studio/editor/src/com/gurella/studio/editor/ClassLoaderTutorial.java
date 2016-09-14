package com.gurella.studio.editor;

//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.StringTokenizer;
//
//import org.eclipse.core.resources.IContainer;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IMarker;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IProjectDescription;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IWorkspace;
//import org.eclipse.core.resources.IncrementalProjectBuilder;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IPath;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.core.runtime.SubProgressMonitor;
//import org.eclipse.jdt.core.IClasspathAttribute;
//import org.eclipse.jdt.core.IClasspathEntry;
//import org.eclipse.jdt.core.IJavaModel;
//import org.eclipse.jdt.core.IJavaProject;
//import org.eclipse.jdt.core.IPackageFragmentRoot;
//import org.eclipse.jdt.core.JavaCore;
//import org.eclipse.jdt.core.JavaModelException;
//import org.eclipse.swt.widgets.Monitor;
//import org.osgi.framework.Bundle;
//
//import com.badlogic.gdx.utils.GdxRuntimeException;

//TODO unused
public class ClassLoaderTutorial {
//	public static void initialize(Monitor monitor, final JETEmitter jetEmitter) {
//		IProgressMonitor progressMonitor = BasicMonitor.toIProgressMonitor(monitor);
//		progressMonitor.beginTask("", 10);
//
//		progressMonitor.subTask("_UI_GeneratingJETEmitterFor_message" + jetEmitter.templateURI);
//
//		try {
//			initSafely(jetEmitter, progressMonitor);
//		} catch (Exception exception) {
//			throw new GdxRuntimeException(exception);
//		} finally {
//			progressMonitor.done();
//		}
//	}
//
//	private static void initSafely(final JETEmitter jetEmitter, IProgressMonitor progressMonitor)
//			throws MalformedURLException, JavaModelException, CoreException, ClassNotFoundException {
//		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
//
//		final JETCompiler jetCompiler = jetEmitter.templateURIPath == null
//				? new MyBaseJETCompiler(jetEmitter.templateURI, jetEmitter.encoding, jetEmitter.classLoader)
//				: new MyBaseJETCompiler(jetEmitter.templateURIPath, jetEmitter.templateURI, jetEmitter.encoding,
//						jetEmitter.classLoader);
//
//		progressMonitor.subTask("_UI_JETParsing_message " + jetCompiler.getResolvedTemplateURI());
//		jetCompiler.parse();
//		progressMonitor.worked(1);
//
//		String packageName = jetCompiler.getSkeleton().getPackageName();
//
//		if (jetEmitter.templateURIPath != null) {
//			URI templateURI = URI.createURI(jetEmitter.templateURIPath[0]);
//			URLClassLoader theClassLoader = null;
//			
//			if (templateURI.isPlatformResource()) {
//				// If the template path points at a project with a JET Nature,
//				// then we will assume that the templates we want to use are
//				// already compiled in this plugin Java project.
//				//
//				IProject project = workspace.getRoot().getProject(templateURI.segment(1));
//				if (JETNature.getRuntime(project) != null) {
//					List<URL> urls = new ArrayList<URL>();
//
//					// Compute the URL for where the classes for this
//					// project will be located.
//					//
//					IJavaProject javaProject = JavaCore.create(project);
//					urls.add(new File(
//							project.getLocation() + "/" + javaProject.getOutputLocation().removeFirstSegments(1) + "/")
//									.toURL());
//
//					// Compute the URLs for all the output folder of all the
//					// project dependencies.
//					//
//					for (IClasspathEntry classpathEntry : javaProject.getResolvedClasspath(true)) {
//						if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
//							IPath projectPath = classpathEntry.getPath();
//							IProject otherProject = workspace.getRoot().getProject(projectPath.segment(0));
//							IJavaProject otherJavaProject = JavaCore.create(otherProject);
//							urls.add(new File(otherProject.getLocation() + "/"
//									+ otherJavaProject.getOutputLocation().removeFirstSegments(1) + "/").toURL());
//						}
//					}
//
//					// Define a class loader that will look in the URLs first,
//					// and if it doesn't find the class there, uses the
//					// emitter's loader.
//					//
//					theClassLoader = new URLClassLoader(urls.toArray(new URL[0])) {
//						@Override
//						public Class<?> loadClass(String className) throws ClassNotFoundException {
//							try {
//								return super.loadClass(className);
//							} catch (ClassNotFoundException classNotFoundException) {
//								return jetEmitter.classLoader.loadClass(className);
//							}
//						}
//					};
//				}
//			} else if (templateURI.isPlatformPlugin()) {
//				final Bundle bundle = Platform.getBundle(templateURI.segment(1));
//				if (bundle != null) {
//					// Define a class loader that will look up the class in
//					// the bundle,
//					// and if it doesn't find it there, will look in the parent.
//					//
//					theClassLoader = new URLClassLoader(new URL[0], jetEmitter.classLoader) {
//						@Override
//						public Class<?> loadClass(String className) throws ClassNotFoundException {
//							try {
//								return bundle.loadClass(className);
//							} catch (ClassNotFoundException classNotFoundException) {
//								return super.loadClass(className);
//							}
//						}
//					};
//				}
//			}
//
//			if (theClassLoader != null) {
//				// Strip off the trailing "_" and load that class.
//				//
//				String className = (packageName.length() == 0 ? "" : packageName + ".")
//						+ jetCompiler.getSkeleton().getClassName();
//				if (className.endsWith("_")) {
//					className = className.substring(0, className.length() - 1);
//				}
//
//				try {
//					Class<?> theClass = theClassLoader.loadClass(className);
//					String methodName = jetCompiler.getSkeleton().getMethodName();
//					Method[] methods = theClass.getDeclaredMethods();
//					for (int i = 0; i < methods.length; ++i) {
//						if (methods[i].getName().equals(methodName)) {
//							jetEmitter.setMethod(methods[i]);
//							break;
//						}
//					}
//
//					// Don't do any of the other normally dynamic JETEmitter
//					// project processing.
//					//
//					return;
//				} catch (ClassNotFoundException exception) {
//					// Continue processing dynamically as normal.
//				}
//			}
//		}
//
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		jetCompiler.generate(outputStream);
//		final InputStream contents = new ByteArrayInputStream(outputStream.toByteArray());
//
//		if (!javaModel.isOpen()) {
//			javaModel.open(new SubProgressMonitor(progressMonitor, 1));
//		} else {
//			progressMonitor.worked(1);
//		}
//
//		final IProject project = workspace.getRoot().getProject(jetEmitter.getProjectName());
//		progressMonitor.subTask("_UI_JETPreparingProject_message " + project.getName());
//
//		IJavaProject javaProject;
//		if (!project.exists()) {
//			progressMonitor.subTask("JET creating project " + project.getName());
//			project.create(new SubProgressMonitor(progressMonitor, 1));
//			progressMonitor.subTask("_UI_JETCreatingProject_message " + project.getName());
//			IProjectDescription description = workspace.newProjectDescription(project.getName());
//			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
//			description.setLocation(null);
//			project.open(new SubProgressMonitor(progressMonitor, 1));
//			project.setDescription(description, new SubProgressMonitor(progressMonitor, 1));
//		} else {
//			project.open(new SubProgressMonitor(progressMonitor, 5));
//			IProjectDescription description = project.getDescription();
//			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
//			project.setDescription(description, new SubProgressMonitor(progressMonitor, 1));
//		}
//
//		javaProject = JavaCore.create(project);
//
//		// Get the existing classpath and remove the project root if
//		// necessary.
//		// Any new non-duplicate entries will be added to this.
//		//
//		List<IClasspathEntry> classpath = new ArrayList(new HashSet(Arrays.asList(javaProject.getRawClasspath())));
//		for (int i = 0, len = classpath.size(); i < len; i++) {
//			IClasspathEntry entry = classpath.get(i);
//			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
//					&& ("/" + project.getName()).equals(entry.getPath().toString())) {
//				classpath.remove(i);
//			}
//		}
//
//		// Add the new entries, including source, JRE container, and
//		// added variables and classpath containers.
//		//
//		progressMonitor.subTask("_UI_JETInitializingProject_message " + project.getName());
//		IClasspathEntry classpathEntry = JavaCore.newSourceEntry(new Path("/" + project.getName() + "/src"));
//
//		IClasspathEntry jreClasspathEntry = JavaCore
//				.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
//
//		classpath.add(classpathEntry);
//		classpath.add(jreClasspathEntry);
//		classpath.addAll(jetEmitter.classpathEntries);
//
//		IFolder sourceFolder = project.getFolder(new Path("src"));
//		if (!sourceFolder.exists()) {
//			sourceFolder.create(false, true, new SubProgressMonitor(progressMonitor, 1));
//		}
//		IFolder runtimeFolder = project.getFolder(new Path("bin"));
//		if (!runtimeFolder.exists()) {
//			runtimeFolder.create(false, true, new SubProgressMonitor(progressMonitor, 1));
//		}
//
//		javaProject.setRawClasspath(classpath.toArray(new IClasspathEntry[classpath.size()]),
//				new SubProgressMonitor(progressMonitor, 1));
//
//		javaProject.setOutputLocation(new Path("/" + project.getName() + "/bin"),
//				new SubProgressMonitor(progressMonitor, 1));
//
//		javaProject.close();
//
//		progressMonitor.subTask("_UI_JETOpeningJavaProject_message " + project.getName());
//		javaProject.open(new SubProgressMonitor(progressMonitor, 1));
//
//		IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
//		IPackageFragmentRoot sourcePackageFragmentRoot = null;
//		for (int j = 0; j < packageFragmentRoots.length; ++j) {
//			IPackageFragmentRoot packageFragmentRoot = packageFragmentRoots[j];
//			if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				sourcePackageFragmentRoot = packageFragmentRoot;
//				break;
//			}
//		}
//
//		StringTokenizer stringTokenizer = new StringTokenizer(packageName, ".");
//		IProgressMonitor subProgressMonitor = new SubProgressMonitor(progressMonitor, 1);
//		subProgressMonitor.beginTask("", stringTokenizer.countTokens() + 4);
//
//		subProgressMonitor.subTask("_UI_CreateTargetFile_message");
//		IContainer sourceContainer = sourcePackageFragmentRoot == null ? project
//				: (IContainer) sourcePackageFragmentRoot.getCorrespondingResource();
//		while (stringTokenizer.hasMoreElements()) {
//			String folderName = stringTokenizer.nextToken();
//			sourceContainer = sourceContainer.getFolder(new Path(folderName));
//			if (!sourceContainer.exists()) {
//				((IFolder) sourceContainer).create(false, true, new SubProgressMonitor(subProgressMonitor, 1));
//			}
//		}
//		IFile targetFile = sourceContainer.getFile(new Path(jetCompiler.getSkeleton().getClassName() + ".java"));
//		if (!targetFile.exists()) {
//			subProgressMonitor.subTask("_UI_JETCreating_message " + targetFile.getFullPath());
//			targetFile.create(contents, true, new SubProgressMonitor(subProgressMonitor, 1));
//		} else {
//			subProgressMonitor.subTask("_UI_JETUpdating_message " + targetFile.getFullPath());
//			targetFile.setContents(contents, true, true, new SubProgressMonitor(subProgressMonitor, 1));
//		}
//
//		subProgressMonitor.subTask("_UI_JETBuilding_message " + project.getName());
//		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new SubProgressMonitor(subProgressMonitor, 1));
//
//		IMarker[] markers = targetFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
//		boolean errors = false;
//		for (int i = 0; i < markers.length; ++i) {
//			IMarker marker = markers[i];
//			if (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR) {
//				errors = true;
//				subProgressMonitor.subTask(marker.getAttribute(IMarker.MESSAGE) + " : " + ("jet.mark.file.line"
//						+ targetFile.getLocation() + " " + marker.getAttribute(IMarker.LINE_NUMBER)));
//			}
//		}
//
//		if (!errors) {
//			subProgressMonitor
//					.subTask("_UI_JETLoadingClass_message" + jetCompiler.getSkeleton().getClassName() + ".class");
//
//			// Construct a proper URL for relative lookup.
//			//
//			List<URL> urls = new ArrayList<URL>();
//			urls.add(
//					new File(project.getLocation() + "/" + javaProject.getOutputLocation().removeFirstSegments(1) + "/")
//							.toURI().toURL());
//
//			// Determine all the bundles that this project depends on.
//			//
//			final Set<Bundle> bundles = new HashSet<Bundle>();
//			LOOP: for (IClasspathEntry jetEmitterClasspathEntry : jetEmitter.getClasspathEntries()) {
//				IClasspathAttribute[] classpathAttributes = jetEmitterClasspathEntry.getExtraAttributes();
//				if (classpathAttributes != null) {
//					for (IClasspathAttribute classpathAttribute : classpathAttributes) {
//						if (classpathAttribute.getName()
//								.equals("CodeGenUtil.EclipseUtil.PLUGIN_ID_CLASSPATH_ATTRIBUTE_NAME")) {
//							Bundle bundle = Platform.getBundle(classpathAttribute.getValue());
//							if (bundle != null) {
//								bundles.add(bundle);
//								continue LOOP;
//							}
//						}
//					}
//				}
//				// For any entry that doesn't correspond to a plugin in the
//				// running JVM, compute a URL for the classes.
//				//
//				urls.add(new URL("platform:/resource" + jetEmitterClasspathEntry.getPath() + "/"));
//			}
//
//			// Define a class loader that looks up classes using the URLs
//			// or the parent class loader,
//			// and failing those, tries to look up the class in each
//			// bundle in the running JVM.
//			//
//			URLClassLoader theClassLoader = new URLClassLoader(urls.toArray(new URL[0]), jetEmitter.classLoader) {
//				@Override
//				public Class<?> loadClass(String className) throws ClassNotFoundException {
//					try {
//						return super.loadClass(className);
//					} catch (ClassNotFoundException exception) {
//						for (Bundle bundle : bundles) {
//							try {
//								return bundle.loadClass(className);
//							} catch (ClassNotFoundException exception2) {
//								// Ignore because we'll rethrow the original
//								// exception eventually.
//							}
//						}
//						throw exception;
//					}
//				}
//			};
//			Class<?> theClass = theClassLoader.loadClass(
//					(packageName.length() == 0 ? "" : packageName + ".") + jetCompiler.getSkeleton().getClassName());
//			String methodName = jetCompiler.getSkeleton().getMethodName();
//			Method[] methods = theClass.getDeclaredMethods();
//			for (int i = 0; i < methods.length; ++i) {
//				if (methods[i].getName().equals(methodName)) {
//					jetEmitter.setMethod(methods[i]);
//					break;
//				}
//			}
//		}
//
//		subProgressMonitor.done();
//	}
}
