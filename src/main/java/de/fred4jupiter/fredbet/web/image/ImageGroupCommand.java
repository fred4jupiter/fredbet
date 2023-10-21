package de.fred4jupiter.fredbet.web.image;

import jakarta.validation.constraints.NotBlank;

public class ImageGroupCommand implements Comparable<ImageGroupCommand>{

	private Long id;

	@NotBlank
	private String name;

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

	@Override
	public int compareTo(ImageGroupCommand imageGroupCommand) {
		return this.getName().compareTo(imageGroupCommand.getName());
	}
}
