package org.nimy.eclipse.editor.xml.ui;

import java.util.List;

public class Entity {
	private String type = null;
	private String name = null;
	private Entity parent = null;
	private String value = null;
	private List<Entity> children = null;

	public Entity(String name) {
		this.name = name;
	}

	public List<Entity> getChildren() {
		return this.children;
	}

	public void setChildren(List<Entity> children) {
		this.children = children;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Entity getParent() {
		return this.parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}