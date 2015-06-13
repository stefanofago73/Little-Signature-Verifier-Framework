package it.fago.lsvf;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.AcroFields.Item;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;

/**
 * 
 * @author Stefano Fago
 * 
 */
public class PdfUtils {
	//
	private static Map<Thread, SimpleDateFormat> formatters;
	//
	private static String cName = PdfUtils.class.getName();

	/**
	 * 
	 */
	public static void init() {
		formatters = new HashMap<Thread, SimpleDateFormat>(31);
	}

	/**
	 * 
	 */
	public static void destroy() {
		formatters.clear();
		formatters = null;
		cName = null;
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public static final Date fromPdfDateToJavaDate(String time) {
		Logger logger = LoggerFactory.getLogger(cName);
		if (time == null || time.length() == 0) {
			throw new RuntimeException("Invalid Input: ["
					+ String.valueOf(time) + "]");
		}
		final SimpleDateFormat DATE_FORMAT = formatterFor(Thread
				.currentThread());
		final String s = time.replaceAll("'", "");
		try {
			return DATE_FORMAT.parse(s);
		} catch (ParseException e) {
			logger.error("Problem with: [ {} ] ", time, e);
			return null;
		} finally {
			logger = null;
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static final String formattedContents(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("Void Data! data="
					+ String.valueOf(data));
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			String hexString = null;
			if ((i % 40) == 0) {
				hexString = Integer.toHexString(data[i] & 0xff);
				hexString = hexString.length() == 1 ? "\n0" + hexString + " "
						: "\n" + hexString + " ";
			} else {
				hexString = Integer.toHexString(data[i] & 0xff);
				hexString = hexString.length() == 1 ? "0" + hexString + " "
						: hexString + " ";
			}
			sb.append(hexString);
		}
		String result = sb.toString();
		sb.setLength(0);
		sb = null;
		return result;
	}

	/**
	 * 
	 * @param filepath
	 * @param data
	 * @param buffer
	 * @throws IOException
	 */
	public static final void analizePdf(String filepath, byte[] data,
			StringBuilder buffer) throws IOException {

		Logger logger = LoggerFactory.getLogger(cName);

		if (data == null) {
			buffer.append("CAN'T ANALIZE VOID CONTENT!...");
			logger = null;
			return;
		}
		ArrayList<SignInfo> signInfos = new ArrayList<SignInfo>();
		PdfReader reader = new PdfReader(data);
		AcroFields ff = reader.getAcroFields();
		Map<String, Item> fields = ff.getFields();
		ArrayList<String> signs = ff.getSignatureNames();
		Set<Entry<String, Item>> entrySet = fields.entrySet();
		for (Iterator<Entry<String, Item>> iterator = entrySet.iterator(); iterator
				.hasNext();) {
			Entry<String, Item> entry = iterator.next();
			if (ff.getFieldType(entry.getKey()) == AcroFields.FIELD_TYPE_SIGNATURE) {
				if (signs.contains(entry.getKey())) {
					String key = entry.getKey();
					try {
						PdfDictionary dict = ff.getSignatureDictionary(entry
								.getKey());
						PdfObject name = dict.get(PdfName.NAME);
						PdfObject codec = dict.get(PdfName.SUBFILTER);
						PdfObject date = dict.get(PdfName.M);
						PdfObject reason = dict.get(PdfName.REASON);
						PdfObject contents = dict.get(PdfName.CONTENTS);
						SignInfo info = new SignInfo();
						info.signer = String.valueOf(name);
						info.reason = String.valueOf(reason);
						info.date = fromPdfDateToJavaDate(date.toString());
						info.codecType = String.valueOf(codec);
						info.contents = contents == null ? new byte[] {}
								: contents.getBytes();
						info.revision = String.valueOf(ff.getRevision(key));
						info.totalRevision = String.valueOf(ff
								.getTotalRevisions());
						info.wholeDocument = ff
								.signatureCoversWholeDocument(key);
						X509Certificate cert = null;
						try {
							PdfPKCS7 signature = ff.verifySignature(key, null);
							// signature.verify();
							cert = signature.getSigningCertificate();
							info.certificate = cert;
							info.fContents = formattedContents(info.contents);
						} catch (Exception exc) {
							logger.error(
									"Error [ {} ] make impossible to harvest Certificate data...",
									exc.toString(), exc);
						}
						signInfos.add(info);
					} catch (Exception exc) {
						logger.error("CAN'T ANALIZE SIGN: [ {} ]", key, exc);
					} finally {
						logger = null;
					}
				}
			}
		}
		logger = null;
		buffer.append("\nfound: ").append(signInfos.size())
				.append(" signature for: ").append(filepath).append("\n");
		for (Iterator<SignInfo> iterator = signInfos.iterator(); iterator
				.hasNext();) {
			SignInfo info = iterator.next();
			buffer.append("\n" + filepath + " information: \n" + info + "\n");
		}
	}

	// =================================================================
	//
	//
	// =================================================================

	private static final SimpleDateFormat formatterFor(final Thread t) {
		final SimpleDateFormat tmp = formatters.get(t);
		if (tmp != null) {
			return tmp;
		}
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
				"'D:'yyyyMMddHHmmssZ");
		formatters.put(t, DATE_FORMAT);
		return DATE_FORMAT;
	}

}// END