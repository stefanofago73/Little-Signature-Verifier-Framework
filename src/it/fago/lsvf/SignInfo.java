package it.fago.lsvf;

import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * 
 * @author Stefano Fago
 * 
 */
class SignInfo {
	
	public String signer;
	public boolean wholeDocument;
	public String revision;
	public String totalRevision;
	public String codecType;
	public Date date;
	public String reason;
	public byte[] contents = {};
	public String fContents;
	public X509Certificate certificate;
	private String encoded;

	/**
	 * @return String
	 */
	public String toString() {
		return "\nSIGNER:  [" + signer + "]\n  REASON:  [" + reason
				+ "]\n  DATE: [" + date + "]\n  CODEC:  [" + codecType
				+ "]\n  CONTENT-LENGTH(bytes):  [" + contents.length
				+ "]\n  REVISION:  [" + revision + "/" + totalRevision
				+ "]\n  CERTIFICATE COVER WHOLE DOC: [" + wholeDocument
				+ "]\n  CONTENT STRING: \n[" + fContents
				+ "]\n  CERTIFICATE: \n  " + certificate + "\n\n";
	}
	
}//END