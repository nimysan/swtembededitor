package org.nimy.eclipse.swt.source.editor.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Preconditions;

public class UIResourceContext {

	private UIResourceContext() {
		imageRegistry = JFaceResources.getImageRegistry();
	}

	public static UIResourceContext getInstance() {
		return new UIResourceContext();
	}

	private final ImageRegistry imageRegistry;

	public Image getImage(final Class<?> path, final String name) {
		Preconditions.checkArgument(path != null, "The image class can not be empty!");
		Image cacheImage = null;
		if (name != null && !name.equals("")) {
			cacheImage = imageRegistry.get(name);
			if (cacheImage == null) {
				try {
					ImageDescriptor img = ImageDescriptor.createFromFile(path, name);
					if (img != null) {
						cacheImage = img.createImage();
						imageRegistry.put(name, img);
					}
				} catch (Exception e) {
					Debug.error(e);
				}
			}
		}
		return cacheImage;
	}

	public ImageDescriptor getImageDescriptor(final Class<?> path, final String name) {
		Preconditions.checkArgument(path != null, "The image class can not be empty!");
		ImageDescriptor cacheImage = null;
		if (name != null && !name.equals("")) {
			cacheImage = imageRegistry.getDescriptor(name);
			if (cacheImage == null) {
				try {
					ImageDescriptor descriptor = ImageDescriptor.createFromFile(path, name);
					if (descriptor != null) {
						imageRegistry.put(name, descriptor);
						return descriptor;
					}
				} catch (Exception e) {
					Debug.error(e);
				}
			}
		}
		return cacheImage;
	}
}
