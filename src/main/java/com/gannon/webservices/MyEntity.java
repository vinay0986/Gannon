package com.vinni.webservices;

import javax.ws.rs.FormParam;

public class MyEntity {

	@FormParam("file")
	private byte[] file;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	// Getter and Setters

}