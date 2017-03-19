package de.fred4jupiter.fredbet.web.admin;

import org.springframework.web.multipart.MultipartFile;

public class ExcelUploadCommand {

	private MultipartFile myFile;

	public MultipartFile getMyFile() {
		return myFile;
	}

	public void setMyFile(MultipartFile myFile) {
		this.myFile = myFile;
	}

}
