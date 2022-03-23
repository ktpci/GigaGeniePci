package com.kt.gigagenie.pci.data;


import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.snd.NonAudiblePlayer;

public class SndSvrData {
	private String returnCode;
	private String message;

	private String soundId;
	private String fileUrl;

	public SndSvrData(JsonObject resObj) {
		JsonObject response = (JsonObject) resObj.get("response");
		returnCode = response.getString("return_code");
		message = response.getString("message");

		soundId = resObj.getString("sound_id");
		fileUrl = resObj.getString("file_url");

		GLog.printInfo("SndSvrData", "regist to beacon server finished. regist-->" + toString());
	}

	public void destroy() {
		returnCode = null;
		message = null;
		soundId = null;
		fileUrl = null;
	}

	public String getMessage() { return message; }
	public String getSoundId() { return soundId; }
	public String getFileUrl() { return fileUrl; }
	public String getReturnCode() { return returnCode; }

	public void setMessage( String _message ) { message = _message; }
	public void setSoundId( String _soundId ) { soundId = _soundId; }
	public void setFileUrl( String _fileUrl ) { fileUrl = _fileUrl; }
	public void setReturnCode( String _returnCode ) { returnCode = _returnCode; }


	public String toString(){
		return "returnCode : " + returnCode + " , message : " + message + " , soundId : " + soundId + " , fileUrl : " + fileUrl;
	}

}