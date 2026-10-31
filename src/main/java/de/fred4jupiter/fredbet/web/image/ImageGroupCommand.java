package de.fred4jupiter.fredbet.web.image;

import jakarta.validation.constraints.NotBlank;

public class ImageGroupCommand {

	private Long id;

	@NotBlank
	private String name;

	private boolean deletable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
}
