package it.fago.lsvf;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
public final class SignaturesVerifierUtils {
	//
	private static final String cName = SignaturesVerifierUtils.class.getName();

	private SignaturesVerifierUtils() {
	}

	/**
	 * 
	 * @param fullPathFile
	 * @return
	 */
	public static final byte[] loadContentFromFile(String fullPathFile) {
		Logger logger = LoggerFactory.getLogger(cName);
		byte[] data = null;
		File f = new File(fullPathFile);

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fullPathFile, "r");
			int len = (int) f.length();
			byte[] buffer = new byte[len];
			raf.readFully(buffer);
			data = buffer;
			buffer = null;
		} catch (Exception ex) {
			logger.error("Error Processing: {}  ---> {} ", fullPathFile,
					ex.toString(), ex);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// suppressed
				}
			}
			logger = null;
		}
		return data;
	}

	/**
	 * 
	 * @param args
	 */
	public static final void checkIfArgumentsArePresent(String[] args) {
		Logger logger = LoggerFactory.getLogger(cName);

		final int len = args.length;
		logger.info("Passed Args: {} = {} ", len, Arrays.toString(args));

		if (args == null || args.length == 0) {
			logger.error("It needed to choose [pdf or p7m] and a folder/file path!");
			logger = null;
			System.exit(-1);
		}
		logger = null;
	}

	/**
	 * 
	 */
	public static final void loadProviderAndStore() {
		Logger logger = LoggerFactory.getLogger(cName);
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
		} catch (Exception e) {
			logger.warn("Error loading Security Providers and CA CERT STORE!",
					e);
			logger = null;
			System.exit(-2);
		}
		logger = null;
	}

	/**
	 * 
	 * @param arg
	 * @param type
	 * @return
	 */
	public static final File[] verifyFileOrDirectory(final String arg,
			final String type) {

		Logger logger = LoggerFactory.getLogger(cName);

		try {

			if (arg == null) {
				throw new IllegalArgumentException("Null File Path!");
			}

			if (Security.getProvider("BC") == null) {
				Security.addProvider(new BouncyCastleProvider());
			}

			File f = new File(arg);

			logger.info("ELEMENT  [ {} ] FILE: -  DIRECTORY: ",
					f.getAbsolutePath(), f.isFile(), f.isDirectory());

			if (!f.canRead()) {
				logger.error("Problem on : {}  can read: {} ", arg, f.canRead());
				logger = null;
				System.exit(-3);
			}

			if (f.isFile()) {
				return new File[] { f };
			}

			if (f.isDirectory()) {
				File[] listFiles = f.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(type);
					}
				});
				return listFiles;
			}
			return new File[] {};
		} finally {
			logger = null;
		}
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static final int numOfWorker(String arg) {

		Logger logger = LoggerFactory.getLogger(cName);

		int result = Runtime.getRuntime().availableProcessors() * 2;

		if (arg == null || arg.length() == 0) {
			logger.info("It will use [ {} ] Threads", result);
			logger = null;
			return result;
		}

		try {
			result = Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			logger.error("wrong number [ {} ]", arg);
		}

		logger.info("It will use [ {} ] Threads", result);
		logger = null;
		return result;
	}

}// END
